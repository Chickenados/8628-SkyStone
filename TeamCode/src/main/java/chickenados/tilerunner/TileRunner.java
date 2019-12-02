package chickenados.tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import chickenlib.CknPIDDrive;
import chickenlib.opmode.CknRobot;
import chickenlib.inputstreams.CknEncoderInputStream;
import chickenlib.CknPIDController;
import chickenlib.CknDriveBase;
import chickenlib.display.CknSmartDashboard;
import chickenlib.location.CknLocationTracker;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;


public class TileRunner extends CknRobot {
    HardwareMap hwMap;
    boolean useVuforia;

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
        CknAccelerometer.Parameters aParameters = new CknAccelerometer.Parameters();
        aParameters.doIntegration = true;

        imu = new CknBNO055IMU(hwMap,"imu", aParameters);

        stoneGrabber = hwMap.servo.get(TileRunnerInfo.STONE_GRABBER_NAME);
        //Drive Train
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


        CknPIDController.Parameters foundationParams = new CknPIDController.Parameters();
        foundationParams.allowOscillation = false;
        foundationParams.useWraparound = false;

        foundationGrabberMotor = hwMap.dcMotor.get(TileRunnerInfo.FOUNDATION_MOTOR_NAME);
        foundationGrabberMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        foundationGrabberMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        foundationPid = new CknPIDController(new CknPIDController.PIDCoefficients(TileRunnerInfo.FOUNDATION_GRABBER_P, TileRunnerInfo.FOUNDATION_GRABBER_I, TileRunnerInfo.FOUNDATION_GRABBER_D),
                new CknEncoderInputStream(foundationGrabberMotor), foundationParams);

        foundationGrabber = new TileRunnerFoundationGrabber(foundationGrabberMotor, foundationPid);


        CknPIDController.Parameters grabberParams = new CknPIDController.Parameters();
        grabberParams.allowOscillation = false;
        grabberParams.useWraparound = false;

        grabberArmMotor = hwMap.dcMotor.get(TileRunnerInfo.GRABBER_ARM_MOTOR_NAME);
        grabberArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grabberArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //grabberArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        grabberPid = new CknPIDController(new CknPIDController.PIDCoefficients(TileRunnerInfo.GRABBER_PID_P, TileRunnerInfo.GRABBER_PID_I, TileRunnerInfo.GRABBER_PID_D),
                new CknEncoderInputStream(grabberArmMotor), grabberParams);
        grabberArm = new TileRunnerGrabberArm(grabberArmMotor, grabberPid);

    }
}