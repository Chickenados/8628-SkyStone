package chickenados.tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import chickenados.skybot.CknSkyBotInfo;
import chickenlib.CknDriveBase;
import chickenlib.CknPIDDrive;
import chickenlib.display.CknSmartDashboard;
import chickenlib.location.CknLocationTracker;
import chickenlib.opmode.CknRobot;

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


        frontLeft = hwMap.dcMotor.get("frontLeft");
        frontRight = hwMap.dcMotor.get("frontRight");
        backLeft = hwMap.dcMotor.get("backLeft");
        backRight = hwMap.dcMotor.get("backRight");

        CknDriveBase.Parameters params = new CknDriveBase.Parameters();
        params.driveTypes.add(CknDriveBase.DriveType.TANK);
        params.driveTypes.add(CknDriveBase.DriveType.ARCADE);
        params.driveTypes.add(CknDriveBase.DriveType.MECANUM);
        params.ticksPerRev = CknSkyBotInfo.ENCODER_TICKS_PER_REV;
        params.gearRatio = CknSkyBotInfo.GEAR_RATIO;
        params.wheelDiameter = CknSkyBotInfo.WHEEL_DIAMETER_INCHES;

        driveBase = new CknDriveBase(frontLeft, frontRight, backLeft, backRight, params);
        driveBase.setMode(CknDriveBase.DriveType.MECANUM);
    }
}