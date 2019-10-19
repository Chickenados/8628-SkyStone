package chickenados.skybot;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

public class CknSkyBotInfo {

    private static final float mmPerInch        = 25.4f;

    //
    // Drive Train Motor Names
    //
    public static final String REAR_LEFT_NAME = "backLeft";
    public static final String REAR_RIGHT_NAME = "backRight";
    public static final String FRONT_LEFT_NAME = "frontLeft";
    public static final String FRONT_RIGHT_NAME = "frontRight";

    //
    // Grabber Subsystem
    //
    public static final String STONE_GRABBER_NAME = "stoneGrabber";

    //port zero hub two -Anna

    public static final String GRABBER_ARM_MOTOR_NAME = "grabberArm";

    public static final double GRABBER_PID_P = 0.007;
    public static final double GRABBER_PID_I = 0.0;
    public static final double GRABBER_PID_D = 0.0;

    public static final int GRABBER_EXTENDED_ENCODER_COUNT = 1000;
    public static final int GRABBER_RETRACTED_ENCODER_COUNT = 0;
    public static final int GRABBER_HIGH_POSITION_ENCODER_COUNT = 750;
    public static final int GRABBER_LOW_POSITION_ENCODER_COUNT = 250;


    // Tele Op Variables
    public static final double FULL_SPEED = 1.0;
    public static final double PRECISION_SPEED = 0.3;

    //TODO: Update these values
    public static final double WHEEL_DIAMETER_INCHES = 4;
    public static final int ENCODER_TICKS_PER_REV = 520;
    public static final double GEAR_RATIO = 1;

    public static final double Y_ENCODER_PID_P = 0.00095;
    public static final double Y_ENCODER_PID_I = 0.0;
    public static final double Y_ENCODER_PID_D = 0.0;

    public static final double TURN_PID_P = 0.025;
    public static final double TURN_PID_I = 0.0;
    public static final double TURN_PID_D = 0.0;

    public static final String IMU_NAME = "imu";


    // Webcam
    //TODO: Make sure these are correct
    public static final String WEBCAME_NAME = "Webcam";
    public static final String VUFORIA_KEY = "AV2hPmr/////AAABmQLD9hUunkK4tSZiwFAlrpZPoN76Ej8hCf1AdzRK5+dWdO6VF0iKY/cqgZLxkQ4RCD0KXMvXtiUx87IkUWaghhJYq446Zx2MDU12MXtsE9hq8p3alcdmCCvCun+veOD/mwKlEXDnZYl8jMzxcCOpEqr3Uc2MzsjpFbrdr+m5tYXmNAKQrN9Bq4VALSSl/pUhk1/swPiJenMa938xu0pN4C+xuOCyAmNX44yln0q8GnoGmtmdMCg3NTOiEDm6K/fFTLI1nWN2LOWzVQZ88Ul0EIjgdTfA+DYgz5O8AS/leZcUn7WTbPbhy/5NaqorhI+6u1YMYYFaPq41j3lenoUU+6DdfK133dZ8+M57EvFVXJSv";

    // Vuforia numbers
    private static final float CAMERA_FORWARD_DISPLACEMENT  = 8.625f * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
    private static final float CAMERA_VERTICAL_DISPLACEMENT = 12.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
    private static final float CAMERA_LEFT_DISPLACEMENT     = -5.25f * mmPerInch;     // eg: Camera is ON the robot's center line

    private static float phoneXRotate    = 90;
    private static float phoneYRotate    = -90;
    private static float phoneZRotate    = 0;


    public static OpenGLMatrix robotFromCamera = OpenGLMatrix
            .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

}
