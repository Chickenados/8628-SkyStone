package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenados.testbot.CknTestBot;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@TeleOp(name = "Skybot TeleOp")
public class SkyBotTeleop extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();
    CknSkyBot robot;

    @Override
    public void runOpMode(){

        robot = new CknSkyBot(hardwareMap, telemetry, false);

        waitForStart();

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);


            robot.driveBase.mecanumDrive(gamepad1.right_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);

            if(gamepad1.x){
                robot.driveBase.setSpeed(CknSkyBotInfo.PRECISION_SPEED);
            }


            if(gamepad1.right_bumper){
                robot.grabberArm.retract(null,2.0);
            } else if (gamepad1.left_bumper){
                robot.grabberArm.extend(null, 2.0);
            } else if (gamepad1.right_trigger>0.1){
                robot.grabberArm.highPosition(null, 2.0);
            } else if (gamepad1.left_trigger>0.1){
                robot.grabberArm.lowPosition(null, 2.0);
            }

            if(gamepad1.a){
                robot.stoneGrabber.setPosition(0);
            } else if(gamepad1.b){
                robot.stoneGrabber.setPosition(93);
            }


            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
