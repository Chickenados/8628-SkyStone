package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import tilerunner.Tilerunner;
import tilerunner.TilerunnerInfo;

@TeleOp(name = "Tilerunner Teleop", group = "FtcTeleop")
public class TilerunnerTeleop extends CknOpMode {

    Tilerunner robot;

    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);
    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    // Main loop for the Teleop Opmode
    @Override
    public void runContinuous(double elapsedTime){

        // Drivetrain Control
        robot.driveBase.holonomicDrive(-gamepad1.right_stick_x, gamepad1.right_stick_y, -gamepad1.left_stick_x);

        // Precision Mode
        if(gamepad1.right_stick_button){
            robot.driveBase.setSpeed(TilerunnerInfo.PRECISION_SPEED);
        } else {
            robot.driveBase.setSpeed(TilerunnerInfo.FULL_SPEED);
        }

        // Grabber Arm Control
        if(gamepad1.right_bumper){
            robot.grabberArm.straightUp(2.0);
        } else if(gamepad1.left_bumper){
            robot.grabberArm.extend(2.0);
        }

        //Manual grabber arm control
        if(gamepad1.right_trigger != 0.0){
            robot.grabberArm.manualControl(-0.5);
        } else if(gamepad1.left_trigger != 0.0){
            robot.grabberArm.manualControl(0.5);
        } else if(!robot.grabberArm.isPidActive()){
            robot.grabberArm.manualControl(0.0);
        }

        //Recalibrate the arm
        if(gamepad1.dpad_down){
            robot.grabberArm.recalibrate();
        }

        //Servo at end of arm
        if(gamepad1.a){
            robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_OPEN_POS);
        } else if(gamepad1.b){
            robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_CLOSED_POS);
        }

        //Capstone Servo
        if(gamepad2.a){
            robot.capstoneServo.setPosition(TilerunnerInfo.CAPSTONE_UP_POS);
        } else if(gamepad2.b){
            robot.capstoneServo.setPosition(TilerunnerInfo.CAPSTONE_RELEASE_POS);
        }

        //Foundation Hook
        /*if(gamepad1.x){
            robot.foundationGrabber.release(2.0);
        } else if(gamepad1.y){
            robot.foundationGrabber.grab(2.0);
        }*/
        //Manual Foundation Control
        if(gamepad2.right_trigger > 0.1){
            robot.foundationGrabber.manualControl(-gamepad2.right_trigger);
        } else if(gamepad2.left_trigger > 0.1){
            robot.foundationGrabber.manualControl(gamepad2.left_trigger);
        } else if(!robot.foundationGrabber.isPidActive()){
            robot.foundationGrabber.manualControl(0.0);
        }

    }

    @Override
    public void startMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        robot.startMode(nextMode);
    }

    @Override
    public void stopMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        robot.stopMode(prevMode);
    }

}
