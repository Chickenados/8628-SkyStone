package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

            //
            //  GAMEPAD 1
            //

            robot.driveBase.mecanumDrive(-gamepad1.right_stick_y, -gamepad1.right_stick_x, gamepad1.left_stick_x);

            //Precision Mode
            if(gamepad1.x){
                robot.driveBase.setSpeed(CknSkyBotInfo.PRECISION_SPEED);
            } else robot.driveBase.setSpeed(CknSkyBotInfo.FULL_SPEED);

            //Grabber Arm Control
            if(gamepad1.right_bumper){
               robot.grabberArm.retract(null,2.0);
            } else if (gamepad1.left_bumper){
                robot.grabberArm.extend(null, 2.0);
            } else if (gamepad1.right_trigger>0.1){
                robot.grabberArm.highPosition(null, 2.0);
            } else if (gamepad1.left_trigger>0.1){
                robot.grabberArm.lowPosition(null, 2.0);
            }

            //Manual Calibration
            if(gamepad1.dpad_down){
                robot.grabberArm.manualControl(-0.5);
            } else if(gamepad1.dpad_up){
                robot.grabberArm.manualControl(0.5);
            }

            if(gamepad1.y){
                robot.grabberArm.calibrateZeroPosition();
            }

            //Stone Grabber "hand" control
            if(gamepad1.a){
                robot.stoneGrabber.setPosition(0);
            } else if(gamepad1.b){
                robot.stoneGrabber.setPosition(97);
            }
            
            //
            //  GAMEPAD 2
            //

            //Foundation Grabber Control
            if(gamepad2.x){
                robot.frontFoundation.setPosition(0);
            } else if(gamepad2.y) {
                robot.frontFoundation.setPosition(60);
            }
            if(gamepad2.a){
                robot.sideFoundation.setPosition(0);
            } else if(gamepad2.b) {
                robot.sideFoundation.setPosition(60);

            }


            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
