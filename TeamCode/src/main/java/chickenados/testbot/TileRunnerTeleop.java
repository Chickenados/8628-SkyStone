package chickenados.testbot;

import android.service.quicksettings.Tile;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "TileRunner Teleop", group = "TileRunner")
public class TileRunnerTeleop extends LinearOpMode {

    TileRunner robot;

    @Override
    public void runOpMode(){

        robot = new TileRunner(hardwareMap);

        waitForStart();

        while(opModeIsActive()){

            robot.driveBase.mecanumDrive(gamepad1.right_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);

        }

    }
}
