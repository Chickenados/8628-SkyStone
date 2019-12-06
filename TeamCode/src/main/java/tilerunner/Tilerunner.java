package tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;

import chickenlib.opmode.CknDashboard;
import chickenlib.opmode.CknRobot;
import chickenlib.pid.CknPidController;
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

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    //Grabber subsystem
    DcMotor grabberArmMotor;

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

        //
        // PID Subsystems
        //

        xPid = new CknPidController("skybotXPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.X_ENCODER_PID_P, TilerunnerInfo.X_ENCODER_PID_I, TilerunnerInfo.X_ENCODER_PID_D),
                TilerunnerInfo.X_ENCODER_PID_TOLERANCE, driveBase::getXPosition);

        yPid = new CknPidController("skybotYPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.Y_ENCODER_PID_P, TilerunnerInfo.Y_ENCODER_PID_I, TilerunnerInfo.Y_ENCODER_PID_D),
                TilerunnerInfo.Y_ENCODER_PID_TOLERANCE, driveBase::getYPosition);

        turnPid = new CknPidController("skybotYPid",
                new CknPidController.PidCoefficients(TilerunnerInfo.TURN_PID_P, TilerunnerInfo.TURN_PID_I, TilerunnerInfo.TURN_PID_D),
                TilerunnerInfo.TURN_PID_TOLERANCE, driveBase::getHeading);

        pidDrive = new CknPidDrive(driveBase, xPid, yPid, turnPid);
        pidDrive.setStallTimeout(1.0);

    }

    public void startMode(RunMode runMode){
        if(driveBase != null && runMode == RunMode.AUTO_MODE){
            driveBase.setOdometryEnabled(true);
        }
    }

    public void stopMode(RunMode runMode){



    }





}
