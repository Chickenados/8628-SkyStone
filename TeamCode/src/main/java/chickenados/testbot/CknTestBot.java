package chickenados.testbot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import chickenlib.CknDriveBase;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.display.CknSmartDashboard;
import chickenlib.inputstreams.CknLocationInputStream;
import chickenlib.location.CknLocationTracker;
import chickenlib.opmode.CknRobot;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;

public class CknTestBot extends CknRobot {

    HardwareMap hwMap;
    private boolean useVuforia;
    private VuforiaLocalizer vuforia;

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

    public CknSmartDashboard dashboard;

    public CknTestBot(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false);
    }

    public CknTestBot(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){

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

        frontLeft = hwMap.dcMotor.get(CknTestBotInfo.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(CknTestBotInfo.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(CknTestBotInfo.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(CknTestBotInfo.REAR_RIGHT_NAME);

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
        //params.driveTypes.add(CknDriveBase.DriveType.ARCADE);
        //params.driveTypes.add(CknDriveBase.DriveType.MECANUM);
        params.ticksPerRev = CknTestBotInfo.ENCODER_TICKS_PER_REV;
        params.gearRatio = CknTestBotInfo.GEAR_RATIO;
        params.wheelDiameter = CknTestBotInfo.WHEEL_DIAMETER_INCHES;

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

        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(CknTestBotInfo.Y_ENCODER_PID_P,
                CknTestBotInfo.Y_ENCODER_PID_I, CknTestBotInfo.Y_ENCODER_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.Y_POSITION),
                yParams);

        CknPIDController.Parameters turnParams = new CknPIDController.Parameters();
        turnParams.allowOscillation = true;
        turnParams.settlingTimeThreshold = 0.3;
        turnParams.useWraparound = false;
        turnParams.maxTarget = 360;
        turnParams.minTarget = 0;
        turnParams.threshold = 2.0;

        turnPid = new CknPIDController(new CknPIDController.PIDCoefficients(CknTestBotInfo.TURN_PID_P,
                CknTestBotInfo.TURN_PID_I, CknTestBotInfo.TURN_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.HEADING),
                turnParams);

        pidDrive = new CknPIDDrive(driveBase, yPid, turnPid);

    }

}
