package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@TeleOp(name = "Skybot Auto")
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
                robot.grabberArm.retract(null, 2.0);
            } else if (gamepad1.y){
                robot.grabberArm.extend(null, 2.0);
            }

            //CLAIRE: Figure out the right numbers to set the position. I just guessed with 0 and 180.
            if(gamepad1.a){
                //robot.stoneGrabber.setPosition(0);
            } else {
                //robot.stoneGrabber.setPosition(180);
            }

            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
