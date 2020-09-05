package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenados.testbot.CknTestBot;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;


@TeleOp(name = "Blank Cknlib TeleOp")
public class BlankChickenlibTeleop extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();

    CknTestBot robot;

    //test comment

    @Override
    public void runOpMode() {

        robot = new CknTestBot(hardwareMap, telemetry, false);

        robot.dashboard.setLine(0, "Robot Initialized!");

        waitForStart();

        robot.dashboard.setLine(1, "Robot Running");

        while (opModeIsActive()) {
            CknUtil.CknLoopCounter.getInstance().loop++;
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            // Put teleop code below this line


            robot.driveBase.mecanumDrive(gamepad1.left_stick_y, gamepad1.right_stick_y, gamepad1.left_stick_x);


            // Put Teleop code above this line
            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
