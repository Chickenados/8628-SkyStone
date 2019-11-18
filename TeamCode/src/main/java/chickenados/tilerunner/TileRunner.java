package chickenados.tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import chickenados.skybot.CknSkyBotInfo;
import chickenados.skybot.TileRunnerInfo;
import chickenlib.opmode.CknRobot;
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

        CknDriveBase.Parameters params = new CknDriveBase.Parameters();
        driveBase = new CknDriveBase(frontLeft, frontRight, backLeft, backRight, params);
        driveBase.setMode(CknDriveBase.DriveType.MECANUM);
        driveBase.setPositionScale(TileRunnerInfo.X_ENCODER_SCALE, TileRunnerInfo.Y_ENCODER_SCALE);

    }
}