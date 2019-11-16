package chickenados.testbot;
import android.service.quicksettings.Tile;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@TeleOp(name = "TileRunnerTeleOp")
public class TileRunnerTeleop extends LinearOpMode{
    CknTaskManager mgr = new CknTaskManager();
    TileRunner robot;

    @Override
    public void runOpMode() {

        robot = new TileRunner(hardwareMap, telemetry, false);

        waitForStart();
        while (opModeIsActive()) {
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);


            robot.driveBase.mecanumDrive(-gamepad1.right_stick_y, -gamepad1.right_stick_x, gamepad1.left_stick_x);

            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }