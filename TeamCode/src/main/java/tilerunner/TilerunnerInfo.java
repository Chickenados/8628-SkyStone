package tilerunner;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

public class TilerunnerInfo {

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
    public static final int STONE_GRABBER_OPEN_POS = 0;
    public static final int STONE_GRABBER_CLOSED_POS = 105;

    public static final String GRABBER_ARM_MOTOR_NAME = "grabberArm";
    public static final double GRABBER_ARM_MOTOR_SPEED = 0.5;

    public static final double GRABBER_PID_P = 0.007;
    public static final double GRABBER_PID_I = 0.0;
    public static final double GRABBER_PID_D = 0.0;
    public static final double GRABBER_PID_TOLERANCE = 1.0;

    //Arm positions
    public static final int GRABBER_EXTENDED_ENCODER_COUNT = 2250;
    public static final int GRABBER_RETRACTED_ENCODER_COUNT = 0;
    public static final int GRABBER_STRAIGHT_UP_ENCODER_COUNT = 1000;

    //
    // Foundation Grabber/hook subsystem
    //
    public static final String FOUNDATION_HOOK_MOTOR_NAME = "foundation";
    public static final int FOUNDATION_HOOK_GRAB_POS = 800;
    public static final int FOUNDATION_HOOK_RELEASE_POS = 50;

    public static final double FOUND_PID_P = 0.0005;
    public static final double FOUND_PID_I = 0.0;
    public static final double FOUND_PID_D = 0.0;
    public static final double FOUND_PID_TOLERANCE = 1.0;


    //
    // Tele Op Variables
    //
    public static final double FULL_SPEED = 1.0;
    public static final double PRECISION_SPEED = 0.3;

    // Y PID values. This is forwards/backwards movement
    public static final double Y_ENCODER_PID_P = 0.03;
    public static final double Y_ENCODER_PID_I = 0.0;
    public static final double Y_ENCODER_PID_D = 0.0;
    public static final double Y_ENCODER_PID_TOLERANCE = 1.0;
    public static final double Y_ENCODER_SCALE = 0.0056099869 * (1.630882353);

    // X PID values. This is sideways movement
    public static final double X_ENCODER_PID_P = 0.1;
    public static final double X_ENCODER_PID_I = 0.0;
    public static final double X_ENCODER_PID_D = 0.0;
    public static final double X_ENCODER_PID_TOLERANCE = 1.0;
    public static final double X_ENCODER_SCALE = 0.0056099869 * (1.630882353);

    // Turn PID values
    public static final double TURN_PID_P = 0.03;
    public static final double TURN_PID_I = 0.0;
    public static final double TURN_PID_D = 0.0;
    public static final double TURN_PID_TOLERANCE = 0.0;
    public static final double TURN_PID_SCALE = 1.0;

    // Adafruit BNO055 IMU
    public static final String IMU_NAME = "imu";

    //
    // Vuforia Variablea
    //

    // Webcam
    public static final String WEBCAME_NAME = "Webcam 1";
    public static final String VUFORIA_KEY = "AV2hPmr/////AAABmQLD9hUunkK4tSZiwFAlrpZPoN76Ej8hCf1AdzRK5+dWdO6VF0iKY/cqgZLxkQ4RCD0KXMvXtiUx87IkUWaghhJYq446Zx2MDU12MXtsE9hq8p3alcdmCCvCun+veOD/mwKlEXDnZYl8jMzxcCOpEqr3Uc2MzsjpFbrdr+m5tYXmNAKQrN9Bq4VALSSl/pUhk1/swPiJenMa938xu0pN4C+xuOCyAmNX44yln0q8GnoGmtmdMCg3NTOiEDm6K/fFTLI1nWN2LOWzVQZ88Ul0EIjgdTfA+DYgz5O8AS/leZcUn7WTbPbhy/5NaqorhI+6u1YMYYFaPq41j3lenoUU+6DdfK133dZ8+M57EvFVXJSv";
    // Vuforia
    public static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    public static final boolean CAMERA_IS_PORTRAIT = false  ;
    public static final double ROBOT_LENGTH                    = 17.5; //Robot length in inches
    public static final double ROBOT_WIDTH                     = 17.5; //Robot width in inches
    public static final float CAMERA_FRONT_OFFSET  = 0f;   //Camera offset from front of robot in inches
    public static final float CAMERA_HEIGHT_OFFSET = 5.5f;   //Camera offset from floor of robot in inches
    public static final float CAMERA_LEFT_OFFSET     = 13.5f; //Camera offset from the left side of the robot in inches

    public static final String TRACKABLES_FILE_NAME = "Skystone";
}
