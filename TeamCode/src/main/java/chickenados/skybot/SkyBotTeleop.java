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

    boolean rightBumperHeld = false;
    @Override
    public void runOpMode(){

        robot = new CknSkyBot(hardwareMap, telemetry, false);

        waitForStart();

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);


            robot.driveBase.mecanumDrive(gamepad1.right_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);

            if(gamepad2.right_bumper && !rightBumperHeld){
                rightBumperHeld=true;
                robot.driveBase.setSpeed(CknSkyBotInfo.PRECISION_SPEED);
            } else if(rightBumperHeld && !gamepad1.right_bumper){
                rightBumperHeld = false;
                robot.driveBase.setSpeed(CknSkyBotInfo.FULL_SPEED);
            }


            if(gamepad2.x){
                robot.grabberArm.retract(null,2.0);
            } else if (gamepad2.y){
                robot.grabberArm.extend(null, 2.0);
            } else if (gamepad2.a){
                robot.grabberArm.highPosition(null, 2.0);
            } else if (gamepad2.b){
                robot.grabberArm.lowPosition(null, 2.0);
            }

            if(gamepad1.a){
                robot.stoneGrabber.setPosition(0);
            } else if(gamepad1.b){
                robot.stoneGrabber.setPosition(100);
            }


            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
