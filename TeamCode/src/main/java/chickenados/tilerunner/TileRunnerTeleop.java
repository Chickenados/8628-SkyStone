package chickenados.tilerunner;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

import chickenados.tilerunner.TileRunner;

@TeleOp(name = "TileRunner Teleop", group = "TileRunner")
public class TileRunnerTeleop extends LinearOpMode {

    TileRunner robot;
    CknTaskManager mgr = new CknTaskManager();

    @Override
    public void runOpMode(){

        robot = new TileRunner(hardwareMap, telemetry, false);

        waitForStart();

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);
            robot.driveBase.mecanumDrive(gamepad1.right_stick_y, -gamepad1.right_stick_x, -gamepad1.left_stick_x);


            //Precision speed
            if (gamepad2.x){
                robot.driveBase.setSpeed(TileRunnerInfo.PRECISION_SPEED);
            } else robot.driveBase.setSpeed(TileRunnerInfo.FULL_SPEED);

            //stone grabber
            if(gamepad1.a){
                robot.stoneGrabber.setPosition(0);
            } else if(gamepad1.b){
                robot.stoneGrabber.setPosition(104);
            }

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

            //Foundation Grabber
            if(gamepad1.x){
                robot.foundationGrabber.goToPosition(50, null, 1.0);
            } else if(gamepad1.y){
                robot.foundationGrabber.goToPosition(1000, null, 1.0);
            }


            //Manual Calibration
            if(!robot.grabberArm.enabled) {
                if (gamepad1.dpad_down) {
                    robot.grabberArm.manualControl(-0.5);
                } else if (gamepad1.dpad_up) {
                    robot.grabberArm.manualControl(0.5);
                } else {
                    robot.grabberArm.manualControl(0);
                }
            }

            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
