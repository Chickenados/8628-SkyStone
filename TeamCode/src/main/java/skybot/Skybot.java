package skybot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import chickenlib.opmode.CknDashboard;
import chickenlib.opmode.CknRobot;
import chickenlib.pid.CknPidController;
import chickenlib.robot.CknDriveBase;
import chickenlib.robot.CknMecanumDriveBase;
import chickenlib.robot.CknPidDrive;
import chickenlib.sensor.CknBNO055IMU;

public class Skybot extends CknRobot {

    HardwareMap hwMap;
    boolean useVuforia;

    //Vuforia Targets
    //CknVuforia vuforia;
    //CameraName webcameName;
    //tilerunner.VuforiaVision vuforiaVision;

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

    public Skybot(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false);
    }

    public Skybot(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){
        this.hwMap = hwMap;

        //Initialize Dashboard
        dashboard = CknDashboard.createInstance(telemetry);

        //Set up the IMU
        imu = new CknBNO055IMU(hwMap, SkybotInfo.IMU_NAME);

        //
        // Drive Train Subsystem
        //
        frontLeft = hwMap.dcMotor.get(SkybotInfo.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(SkybotInfo.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(SkybotInfo.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(SkybotInfo.REAR_RIGHT_NAME);

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

        //
        // PID Subsystems
        //

        xPid = new CknPidController("skybotXPid",
                new CknPidController.PidCoefficients(SkybotInfo.X_ENCODER_PID_P, SkybotInfo.X_ENCODER_PID_I, SkybotInfo.X_ENCODER_PID_D),
                SkybotInfo.X_ENCODER_PID_TOLERANCE, driveBase::getXPosition);

        yPid = new CknPidController("skybotYPid",
                new CknPidController.PidCoefficients(SkybotInfo.Y_ENCODER_PID_P, SkybotInfo.Y_ENCODER_PID_I, SkybotInfo.Y_ENCODER_PID_D),
                SkybotInfo.X_ENCODER_PID_TOLERANCE, driveBase::getYPosition);

        turnPid = new CknPidController("skybotYPid",
                new CknPidController.PidCoefficients(SkybotInfo.TURN_PID_P, SkybotInfo.TURN_PID_I, SkybotInfo.TURN_PID_D),
                SkybotInfo.X_ENCODER_PID_TOLERANCE, driveBase::getHeading);

        pidDrive = new CknPidDrive(driveBase, xPid, yPid, turnPid);
        pidDrive.setStallTimeout(1.0);

    }





}
