package chickenados.testbot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Test Bot Teleop")
public class TestBotTeleop extends LinearOpMode {

    TestBot testBot;


    @Override
    public void runOpMode(){

        testBot = new TestBot(hardwareMap);

        waitForStart();

        while(opModeIsActive()){


            testBot.frontLeft.setPower(gamepad1.left_stick_y);
            testBot.frontRight.setPower(gamepad1.right_stick_y);
            testBot.backLeft.setPower(gamepad1.left_stick_y);
            testBot.backRight.setPower(gamepad1.right_stick_y);

        }

    }


}
