package tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;

import chickenlib.hardware.CknMotor;
import chickenlib.opmode.CknDashboard;
import chickenlib.opmode.CknRobot;
import chickenlib.pid.CknPidController;
import chickenlib.pid.CknPidMotor;
import chickenlib.robot.CknDriveBase;
import chickenlib.robot.CknMecanumDriveBase;
import chickenlib.robot.CknPidDrive;
import chickenlib.robot.CknVuforia;
import chickenlib.sensor.CknBNO055IMU;

public class Tilerunner extends CknRobot {

    HardwareMap hwMap;
    boolean useVuforia;

    //Vuforia Targets
    CknVuforia vuforia;
    CameraName webcameName;
    VuforiaVision vuforiaVision;

    //Drivetrain subsystem
    public CknDriveBase driveBase;
    public CknPidDrive pidDrive;

    public CknPidController xPid;
    public CknPidController yPid;
    public CknPidController turnPid;

    CknBNO055IMU imu;

    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor rearLeft;
    public DcMotor rearRight;

    //Grabber Arm subsystem
    CknMotor grabberArmMotor;
    CknPidController grabberArmPid;
    CknPidMotor grabberArmPidMotor;
    public TilerunnerGrabberArm grabberArm;

    public Servo stoneGrabber;

    //Foundation Hook subsystem
    CknMotor foundationGrabberMotor;
    CknPidController foundationGrabberPid;
    CknPidMotor foundationGrabberPidMotor;
    public TilerunnerFoundationGrabber foundationGrabber;

    public CknDashboard dashboard;

    public Tilerunner(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false);
    }

    public Tilerunner(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){
        this.hwMap = hwMap;

        //Initialize Dashboard
        dashboard = CknDashboard.getInstance();

        //Set up the IMU
        imu = new CknBNO055IMU(hwMap, TilerunnerInfo.IMU_NAME);

        //
        // Drive Train Subsystem
        //
        frontLeft = hwMap.dcMotor.get(TilerunnerInfo.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(TilerunnerInfo.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(TilerunnerInfo.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(TilerunnerInfo.REAR_RIGHT_NAME);

        // Reverse Motors
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set motors to braking
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Initialize Drive Base
        driveBase = new CknMecanumDriveBase(frontLeft, frontRight, rearLeft, rearRight, imu);
        driveBase.setDriveScale(TilerunnerInfo.X_ENCODER_SCALE, TilerunnerInfo.Y_ENCODER_SCALE, TilerunnerInfo.TURN_PID_SCALE);

        //Reset the encoders on the motors. This is a hardware reset and should only be done in init.
        driveBase.resetMotorOdometry();

        //
        // PID Drive Subsystems
        //

        xPid = new CknPidController("tilerunnerXPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.X_ENCODER_PID_P, TilerunnerInfo.X_ENCODER_PID_I, TilerunnerInfo.X_ENCODER_PID_D),
                TilerunnerInfo.X_ENCODER_PID_TOLERANCE, driveBase::getXPosition);

        yPid = new CknPidController("tilerunnerYPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.Y_ENCODER_PID_P, TilerunnerInfo.Y_ENCODER_PID_I, TilerunnerInfo.Y_ENCODER_PID_D),
                TilerunnerInfo.Y_ENCODER_PID_TOLERANCE, driveBase::getYPosition);

        turnPid = new CknPidController("tilerunnerTurnPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.TURN_PID_P, TilerunnerInfo.TURN_PID_I, TilerunnerInfo.TURN_PID_D),
                TilerunnerInfo.TURN_PID_TOLERANCE, driveBase::getHeading);
        turnPid.setAbsoluteSetPoint(true);

        pidDrive = new CknPidDrive(driveBase, xPid, yPid, turnPid);
        pidDrive.setStallTimeout(1.0);

        //
        // Grabber Arm Subsystem
        //
        grabberArmMotor = new CknMotor(TilerunnerInfo.GRABBER_ARM_MOTOR_NAME);
        grabberArmMotor.resetOdometry(true);
        grabberArmMotor.enableMotorOdometry(true);

        grabberArmPid = new CknPidController("tilerunnerGrabberPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.GRABBER_PID_P, TilerunnerInfo.GRABBER_PID_I, TilerunnerInfo.GRABBER_PID_D),
                TilerunnerInfo.GRABBER_PID_TOLERANCE, grabberArmMotor::getPosition);
        grabberArmPid.setAbsoluteSetPoint(true);

        grabberArmPidMotor = new CknPidMotor(grabberArmMotor, grabberArmPid);
        grabberArmPidMotor.setMotorMaxSpeed(TilerunnerInfo.GRABBER_ARM_MOTOR_SPEED);
        grabberArmPidMotor.setStalledTimeout(0.5);
        grabberArm = new TilerunnerGrabberArm(grabberArmPidMotor);

        // Servo at the end of arm
        stoneGrabber = hwMap.servo.get(TilerunnerInfo.STONE_GRABBER_NAME);

        //
        // Foundation Hook Subsystem
        //
        foundationGrabberMotor = new CknMotor(TilerunnerInfo.FOUNDATION_HOOK_MOTOR_NAME);
        foundationGrabberMotor.resetOdometry(true);
        foundationGrabberMotor.enableMotorOdometry(true);


        foundationGrabberPid = new CknPidController("tilerunnerHookpid", new CknPidController.PidCoefficients(TilerunnerInfo.FOUND_PID_P, TilerunnerInfo.FOUND_PID_I, TilerunnerInfo.FOUND_PID_D),
                TilerunnerInfo.FOUND_PID_TOLERANCE, foundationGrabberMotor::getPosition);
        foundationGrabberPid.setAbsoluteSetPoint(true);

        foundationGrabberPidMotor = new CknPidMotor(foundationGrabberMotor, foundationGrabberPid);
        foundationGrabberPidMotor.setStalledTimeout(0.5);
        foundationGrabber = new TilerunnerFoundationGrabber(foundationGrabberPidMotor);


    }

    public void startMode(RunMode runMode){
        if(driveBase != null && runMode == RunMode.AUTO_MODE){
            imu.setEnabled(true);
            driveBase.setOdometryEnabled(true);
        }
    }

    public void stopMode(RunMode runMode){

        // Disabled the IMU input task.
        if(imu.isEnabled()){
            imu.setEnabled(false);
        }

        // Disable odometry tracking with the drivebase.
        if(driveBase.isOdometryEnabled()){
            driveBase.setOdometryEnabled(false);
        }


    }





}
