package chickenados.tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import chickenados.skybot.CknSkyBotInfo;
import chickenados.skybot.VuforiaVision;
import chickenlib.CknPIDDrive;
import chickenlib.location.CknPose;
import chickenlib.opmode.CknRobot;
import chickenlib.inputstreams.CknEncoderInputStream;
import chickenlib.CknPIDController;
import chickenlib.CknDriveBase;
import chickenlib.display.CknSmartDashboard;
import chickenlib.location.CknLocationTracker;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;
import chickenlib.vision.CknVuforia;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;


public class TileRunner extends CknRobot {
    HardwareMap hwMap;
    boolean useVuforia;

    //Vuforia Targets
    CknVuforia vuforia;
    CameraName webcameName;
    VuforiaVision vuforiaVision;

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    //Grabber subsystem
    DcMotor grabberArmMotor;
    TileRunnerGrabberArm grabberArm;
    CknPIDController grabberPid;


    //Stone grabber servo
    Servo stoneGrabber;

    //Foundation Grabber subsystem
    DcMotor foundationGrabberMotor;
    TileRunnerFoundationGrabber foundationGrabber;
    CknPIDController foundationPid;



    //Drivetrain subsystem
    public CknDriveBase driveBase;
    public CknPIDDrive pidDrive;
    public CknLocationTracker locationTracker;
    public CknPIDController yPid;
    public CknPIDController turnPid;

    CknBNO055IMU imu;

    public CknSmartDashboard dashboard;

    public TileRunner(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false);
    }

    public TileRunner(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){

        this.hwMap = hwMap;
        this.useVuforia = useVuforia;

        //
        // Initialize Dashboard system
        //
        CknSmartDashboard.Parameters dashParams = new CknSmartDashboard.Parameters();
        dashParams.displayWidth = 400;
        dashParams.numLines = 32;
        dashboard = CknSmartDashboard.createInstance(telemetry, dashParams);

        //
        // IMU
        //
        CknAccelerometer.Parameters aParameters = new CknAccelerometer.Parameters();
        aParameters.doIntegration = true;

        imu = new CknBNO055IMU(hwMap,"imu", aParameters);

        //
        // Drive Train
        //

        frontLeft = hwMap.dcMotor.get(TileRunnerInfo.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(TileRunnerInfo.FRONT_RIGHT_NAME);
        backLeft = hwMap.dcMotor.get(TileRunnerInfo.REAR_LEFT_NAME);
        backRight = hwMap.dcMotor.get(TileRunnerInfo.REAR_RIGHT_NAME);

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        CknDriveBase.Parameters params = new CknDriveBase.Parameters();
        params.driveTypes.add(CknDriveBase.DriveType.TANK);
        params.driveTypes.add(CknDriveBase.DriveType.ARCADE);
        params.driveTypes.add(CknDriveBase.DriveType.MECANUM);
        params.ticksPerRev = TileRunnerInfo.ENCODER_TICKS_PER_REV;
        params.gearRatio = TileRunnerInfo.GEAR_RATIO;
        params.wheelDiameter = TileRunnerInfo.WHEEL_DIAMETER_INCHES;
        driveBase = new CknDriveBase(frontLeft, frontRight, backLeft, backRight, params);
        driveBase.setMode(CknDriveBase.DriveType.MECANUM);
        driveBase.setPositionScale(TileRunnerInfo.X_ENCODER_SCALE, TileRunnerInfo.Y_ENCODER_SCALE);

        //
        // Foundation Grabber
        //

        CknPIDController.Parameters foundationParams = new CknPIDController.Parameters();
        foundationParams.allowOscillation = false;
        foundationParams.useWraparound = false;

        foundationGrabberMotor = hwMap.dcMotor.get(TileRunnerInfo.FOUNDATION_MOTOR_NAME);
        foundationGrabberMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        foundationGrabberMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        foundationPid = new CknPIDController(new CknPIDController.PIDCoefficients(TileRunnerInfo.FOUNDATION_GRABBER_P, TileRunnerInfo.FOUNDATION_GRABBER_I, TileRunnerInfo.FOUNDATION_GRABBER_D),
                new CknEncoderInputStream(foundationGrabberMotor), foundationParams);

        foundationGrabber = new TileRunnerFoundationGrabber(foundationGrabberMotor, foundationPid);

        //
        // Stone Grabber Arm
        //

        // Servo at end of arm
        stoneGrabber = hwMap.servo.get(TileRunnerInfo.STONE_GRABBER_NAME);

        // DC Motor PID for arm
        CknPIDController.Parameters grabberParams = new CknPIDController.Parameters();
        grabberParams.allowOscillation = false;
        grabberParams.useWraparound = false;

        // Dc Motor for arm
        grabberArmMotor = hwMap.dcMotor.get(TileRunnerInfo.GRABBER_ARM_MOTOR_NAME);
        grabberArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grabberArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //grabberArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        grabberPid = new CknPIDController(new CknPIDController.PIDCoefficients(TileRunnerInfo.GRABBER_PID_P, TileRunnerInfo.GRABBER_PID_I, TileRunnerInfo.GRABBER_PID_D),
                new CknEncoderInputStream(grabberArmMotor), grabberParams);
        grabberArm = new TileRunnerGrabberArm(grabberArmMotor, grabberPid);

        // Initialize Vuforia
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
      /*  int cameraMonitorViewID = hwMap.appContext.getResources().getIdentifier("cameraMonitorViewId",
                "id", hwMap.appContext.getPackageName());

        vuforia = new CknVuforia(CknSkyBotInfo.VUFORIA_KEY, cameraMonitorViewID, webcameName, BACK);
*/
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
                        translation.get(1)/ CknSkyBotInfo.mmPerInch, -translation.get(0)/CknSkyBotInfo.mmPerInch,
                        orientation.thirdAngle);
            }
        }

        return pose;
    }
}