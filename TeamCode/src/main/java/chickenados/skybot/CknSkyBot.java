package chickenados.skybot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import chickenlib.CknDriveBase;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.display.CknSmartDashboard;
import chickenlib.inputstreams.CknEncoderInputStream;
import chickenlib.inputstreams.CknLocationInputStream;
import chickenlib.location.CknLocationTracker;
import chickenlib.location.CknPose;
import chickenlib.opmode.CknRobot;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;
import chickenlib.vision.CknVuforia;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

public class CknSkyBot extends CknRobot {
    HardwareMap hwMap;
    boolean useVuforia;

    //Vuforia Targets
    CknVuforia vuforia;
    CameraName webcameName;
    VuforiaVision vuforiaVision;

    //Drivetrain subsystem
    public CknDriveBase driveBase;
    public CknPIDDrive pidDrive;
    public CknLocationTracker locationTracker;

    public CknPIDController yPid;
    public CknPIDController turnPid;

    CknBNO055IMU imu;

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    //Grabber subsystem
    DcMotor grabberArmMotor;
    SkybotGrabberArm grabberArm;
    CknPIDController grabberPid;

    Servo stoneGrabber;

    public CknSmartDashboard dashboard;

    public CknSkyBot(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false);
    }

    public CknSkyBot(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){

        this.hwMap = hwMap;

        this.useVuforia = useVuforia;

        // Acclerometer Parameters
        //TODO: Look at this because we probably aren't going to use it
        CknAccelerometer.Parameters aParameters = new CknAccelerometer.Parameters();
        aParameters.doIntegration = true;

        imu = new CknBNO055IMU(hwMap,"imu", aParameters);


        //
        // Initialize Drive Train system
        //

        frontLeft = hwMap.dcMotor.get(CknSkyBotInfo.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(CknSkyBotInfo.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(CknSkyBotInfo.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(CknSkyBotInfo.REAR_RIGHT_NAME);

        // Reverse Motors
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set motors to braking
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        CknDriveBase.Parameters params = new CknDriveBase.Parameters();
        params.driveTypes.add(CknDriveBase.DriveType.TANK);
        params.driveTypes.add(CknDriveBase.DriveType.ARCADE);
        params.driveTypes.add(CknDriveBase.DriveType.MECANUM);
        params.ticksPerRev = CknSkyBotInfo.ENCODER_TICKS_PER_REV;
        params.gearRatio = CknSkyBotInfo.GEAR_RATIO;
        params.wheelDiameter = CknSkyBotInfo.WHEEL_DIAMETER_INCHES;

        driveBase = new CknDriveBase(frontLeft, frontRight, rearLeft, rearRight, params);
        driveBase.setMode(CknDriveBase.DriveType.TANK);
        driveBase.setPositionScale(CknSkyBotInfo.X_ENCODER_SCALE, CknSkyBotInfo.Y_ENCODER_SCALE);

        //
        // Location Tracking subsystem
        //

        CknLocationTracker.Parameters LTParams = new CknLocationTracker.Parameters();
        LTParams.useEncoders = true;
        LTParams.useGyro = true;

        locationTracker = new CknLocationTracker(driveBase, imu.gyro, imu.accelerometer, LTParams);
        locationTracker.resetLocation();
        locationTracker.setTaskEnabled(true);

        //
        // Initialize SmartDashboard system
        //
        CknSmartDashboard.Parameters dashParams = new CknSmartDashboard.Parameters();
        dashParams.displayWidth = 400;
        dashParams.numLines = 32;
        dashboard = CknSmartDashboard.createInstance(telemetry, dashParams);


        //
        // PID Drive systems
        //

        CknPIDController.Parameters yParams = new CknPIDController.Parameters();
        yParams.allowOscillation = false;
        yParams.useWraparound = false;

        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(CknSkyBotInfo.Y_ENCODER_PID_P,
                CknSkyBotInfo.Y_ENCODER_PID_I, CknSkyBotInfo.Y_ENCODER_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.Y_POSITION),
                yParams);

        CknPIDController.Parameters turnParams = new CknPIDController.Parameters();
        turnParams.allowOscillation = true;
        turnParams.settlingTimeThreshold = 0.3;
        turnParams.useWraparound = false;
        turnParams.maxTarget = 360;
        turnParams.minTarget = 0;
        turnParams.threshold = 2.0;

        turnPid = new CknPIDController(new CknPIDController.PIDCoefficients(CknSkyBotInfo.TURN_PID_P,
                CknSkyBotInfo.TURN_PID_I, CknSkyBotInfo.TURN_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.HEADING),
                turnParams);

        pidDrive = new CknPIDDrive(driveBase, yPid, turnPid);


        //
        // Grabber Arm Subsystem
        //
        CknPIDController.Parameters grabberParams = new CknPIDController.Parameters();
        grabberParams.allowOscillation = false;
        grabberParams.useWraparound = false;

        grabberArmMotor = hwMap.dcMotor.get(CknSkyBotInfo.GRABBER_ARM_MOTOR_NAME);
        grabberArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grabberArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        grabberArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        grabberPid = new CknPIDController(new CknPIDController.PIDCoefficients(CknSkyBotInfo.GRABBER_PID_P, CknSkyBotInfo.GRABBER_PID_I, CknSkyBotInfo.GRABBER_PID_D),
                new CknEncoderInputStream(grabberArmMotor), grabberParams);
        grabberArm = new SkybotGrabberArm(grabberArmMotor, grabberPid);


        stoneGrabber = hwMap.servo.get(CknSkyBotInfo.STONE_GRABBER_NAME);

        if(useVuforia){
            initVuforia();
        }

    }

    private void initVuforia(){

        float phoneXRotate;
        float phoneYRotate;
        float phoneZRotate = 0.0f;

        webcameName = hwMap.get(WebcamName.class, CknSkyBotInfo.WEBCAME_NAME);
        // Set this int to -1 if you want to disable the camera monitor.
        int cameraMonitorViewID = hwMap.appContext.getResources().getIdentifier("cameraMonitorViewId",
                "id", hwMap.appContext.getPackageName());

        vuforia = new CknVuforia(CknSkyBotInfo.VUFORIA_KEY, cameraMonitorViewID, webcameName, BACK);

        // We need to rotate the camera around it's long axis to bring the correct camera forward.
        phoneYRotate = CknSkyBotInfo.CAMERA_CHOICE == BACK ? -90.0f : 90.0f;

        // Rotate the phone vertical about the X axis if it's in portrait mode
        phoneXRotate = CknSkyBotInfo.CAMERA_IS_PORTRAIT ? 90.0f : 0.0f;

        final int CAMERA_FORWARD_DISPLACEMENT = (int)((CknSkyBotInfo.ROBOT_LENGTH/2.0 - CknSkyBotInfo.CAMERA_FRONT_OFFSET)* CknSkyBotInfo.mmPerInch);
        final int CAMERA_VERTICAL_DISPLACEMENT = (int)(CknSkyBotInfo.CAMERA_HEIGHT_OFFSET*CknSkyBotInfo.mmPerInch);
        final int CAMERA_LEFT_DISPLACEMENT = (int)((CknSkyBotInfo.ROBOT_WIDTH/2.0 - CknSkyBotInfo.CAMERA_LEFT_OFFSET)*CknSkyBotInfo.mmPerInch);

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        phoneYRotate, phoneZRotate, phoneXRotate));

        vuforiaVision = new VuforiaVision(vuforia, robotFromCamera);
    }

    public CknPose getSkystonePose(){
        CknPose pose = null;

        if(vuforiaVision != null){
            OpenGLMatrix robotLocation = vuforiaVision.getRobotLocation();
            if (robotLocation != null)
            {
                VectorF translation = vuforiaVision.getLocationTranslation(robotLocation);
                Orientation orientation = vuforiaVision.getLocationOrientation(robotLocation);
                pose = new CknPose(
                        translation.get(1)/CknSkyBotInfo.mmPerInch, -translation.get(0)/CknSkyBotInfo.mmPerInch,
                        orientation.thirdAngle);
            }
        }

        return pose;
    }

}

