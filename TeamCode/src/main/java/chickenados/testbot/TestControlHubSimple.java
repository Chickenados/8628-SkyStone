package chickenados.testbot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TestControlHubSimple")
public class TestControlHubSimple extends LinearOpMode {

    TestBot testBot;

    boolean rightBumperHeld = false;

    @Override
    public void runOpMode(){


        testBot = new TestBot(hardwareMap);

        waitForStart();

        while(opModeIsActive()){


            testBot.frontLeft.setPower(gamepad1.left_stick_y);
            testBot.frontRight.setPower(gamepad1.right_stick_y);
            testBot.backLeft.setPower(gamepad1.left_stick_y);
            testBot.backRight.setPower(gamepad1.right_stick_y);
            //testBot.grabberPivot.setPower(gamepad2.left_stick_y);
            //testBot.grabberRotater.setPosition(gamepad1.right_trigger);

            //if(gamepad2.right_bumper && !rightBumperHeld){
            //  rightBumperHeld = true;
            //testBot.drive.setSpeed(CknTestBotInfo.PRECISION_SPEED);
            //   } else if(rightBumperHeld && !gamepad1.right_bumper){
            //     rightBumperHeld = false;
            //   testBot.driveBase.setSpeed(CknTestBotInfo.FULL_SPEED);
        }
    }

}