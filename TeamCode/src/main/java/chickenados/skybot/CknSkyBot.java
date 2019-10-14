package chickenados.skybot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import chickenlib.CknDriveBase;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.display.CknSmartDashboard;
import chickenlib.inputstreams.CknEncoderInputStream;
import chickenlib.inputstreams.CknLocationInputStream;
import chickenlib.location.CknLocationTracker;
import chickenlib.opmode.CknRobot;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;

public class CknSkyBot extends CknRobot {
    HardwareMap hwMap;
    private boolean useVuforia;
    private VuforiaLocalizer vuforia;

    //Vuforia Targets
    VuforiaTrackables skystoneTrackables;
    VuforiaTrackable stoneTarget;
    OpenGLMatrix lastLocation = null;

    List<VuforiaTrackable> allTrackables = new ArrayList<>();
    List<VuforiaTrackable> visibleTrackables = new ArrayList<>();

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

        grabberPid = new CknPIDController(new CknPIDController.PIDCoefficients(CknSkyBotInfo.GRABBER_PID_P, CknSkyBotInfo.GRABBER_PID_I, CknSkyBotInfo.GRABBER_PID_D),
                new CknEncoderInputStream(grabberArmMotor), grabberParams);

        grabberArmMotor = hwMap.dcMotor.get(CknSkyBotInfo.GRABBER_ARM_MOTOR_NAME);
        grabberArm = new SkybotGrabberArm(grabberArmMotor, grabberPid);


        //stoneGrabber = hwMap.servo.get(CknSkyBotInfo.STONE_GRABBER_NAME);

        if(useVuforia){
            initVuforia();
        }

    }

    private void initVuforia(){

        int cameraMonitorViewId = hwMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hwMap.appContext.getPackageName());

        // Init Vuforia
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(/*cameraMonitorViewId*/);

        //Use only if using phone camera
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        parameters.vuforiaLicenseKey = CknSkyBotInfo.VUFORIA_KEY;

        //USE FOR WEBCAM
        //parameters.cameraName = hwMap.get(WebcamName.class, CknSkyBotInfo.WEBCAME_NAME);



        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        if(useVuforia){

            skystoneTrackables = this.vuforia.loadTrackablesFromAsset("Skystone");
            stoneTarget = skystoneTrackables.get(0);
            stoneTarget.setName("Stone Target");

            allTrackables.add(stoneTarget);

            //Set phone info for all trackables
            for(VuforiaTrackable t : allTrackables){
                ((VuforiaTrackableDefaultListener) t.getListener())
                        .setPhoneInformation(CknSkyBotInfo.robotFromCamera, parameters.cameraDirection);
            }

        }
    }

    //Start tracking the targets initialized
    public void startVuforiaTracking(){
        if(useVuforia){
            skystoneTrackables.activate();
        }
    }

    //Stop tracking vuforia targets
    public void stopVuforiaTracking(){
        if(useVuforia){
            skystoneTrackables.deactivate();
        }
    }

    public OpenGLMatrix trackLocation(){
        if(!useVuforia) return null;

        boolean targetVisible = false;

        for(VuforiaTrackable t : allTrackables){
            if (((VuforiaTrackableDefaultListener)t.getListener()).isVisible()) {
                targetVisible = true;
                if(!visibleTrackables.contains(t)){
                    visibleTrackables.add(t);
                }

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)t.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }

            } else {
                if(visibleTrackables.contains(t)) visibleTrackables.remove(t);
            }
        }

        return lastLocation;

    }

}

