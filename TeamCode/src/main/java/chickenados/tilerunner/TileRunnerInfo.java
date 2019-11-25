package chickenados.tilerunner;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;


public class TileRunnerInfo {

    public static final float mmPerInch        = 25.4f;

    //
    // Drive Train Motor Names
    //
    public static final String REAR_LEFT_NAME = "backLeft";
    public static final String REAR_RIGHT_NAME = "backRight";
    public static final String FRONT_LEFT_NAME = "frontLeft";
    public static final String FRONT_RIGHT_NAME = "frontRight";
    // Tele Op Variables
    public static final double FULL_SPEED = 1.0;
    public static final double PRECISION_SPEED = 0.3;


    //Grabber servo
    public static final String STONE_GRABBER_NAME = "stoneGrabber";

    //
    // Foundation Hook Subsystem
    //
    public static final String FOUNDATION_MOTOR_NAME = "foundation";
    public static final double FOUNDATION_MOTOR_SPEED = 0.5;

    public static final double FOUNDATION_GRABBER_P = 0.007;
    public static final double FOUNDATION_GRABBER_I = 0.0;
    public static final double FOUNDATION_GRABBER_D = 0.0;

    //

    //Grabber arm subsystem
    //
    public static final String GRABBER_ARM_MOTOR_NAME = "grabberArm";
    public static final double GRABBER_ARM_MOTOR_SPEED = 0.5;

    public static final double GRABBER_PID_P = 0.007;
    public static final double GRABBER_PID_I = 0.0;
    public static final double GRABBER_PID_D = 0.0;

    public static final int GRABBER_EXTENDED_ENCODER_COUNT = -2325;
    //gamepad1 left bumper, parallel to ground position
    public static final int GRABBER_RETRACTED_ENCODER_COUNT = 0;
    //gamepad1 right bumper, all the way back
    public static final int GRABBER_HIGH_POSITION_ENCODER_COUNT = -1000;
    //gamepad1 right trigger
    public static final int GRABBER_LOW_POSITION_ENCODER_COUNT = -1600;
    //gamepad1 left trigger under alliance bridge/dropping position

    //TODO: Update these values
    public static final double WHEEL_DIAMETER_INCHES = 4;
    public static final int ENCODER_TICKS_PER_REV = 520;
    public static final double GEAR_RATIO = 1;

    public static final double Y_ENCODER_PID_P = 0.05;
    public static final double Y_ENCODER_PID_I = 0.0;
    public static final double Y_ENCODER_PID_D = 0.0;
    public static final double Y_ENCODER_SCALE = .0112199738;


    public static final double X_ENCODER_PID_P = 0.1;
    public static final double X_ENCODER_PID_I = 0.0;
    public static final double X_ENCODER_PID_D = 0.0;
    public static final double X_ENCODER_SCALE = 1.0;

    public static final double TURN_PID_P = 0.025;
    public static final double TURN_PID_I = 0.0;
    public static final double TURN_PID_D = 0.0;

    public static final String IMU_NAME = "imu";

}
