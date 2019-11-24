package chickenados.tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import chickenlib.opmode.CknRobot;

import chickenlib.CknDriveBase;
import chickenlib.display.CknSmartDashboard;
import chickenlib.location.CknLocationTracker;


public class TileRunner extends CknRobot {


    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    //Drivetrain subsystem
    public CknDriveBase driveBase;
    public CknLocationTracker locationTracker;

    public CknSmartDashboard dashboard;

    public TileRunner(HardwareMap hwMap) {

        frontLeft = hwMap.dcMotor.get(TileRunnerInfo.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(TileRunnerInfo.FRONT_RIGHT_NAME);
        backLeft = hwMap.dcMotor.get(TileRunnerInfo.REAR_LEFT_NAME);
        backRight = hwMap.dcMotor.get(TileRunnerInfo.REAR_RIGHT_NAME);

        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
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

    }
}