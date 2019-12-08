package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import chickenlib.util.CknTimer;
import tilerunner.Tilerunner;
import tilerunner.TilerunnerGrabberArm;
import tilerunner.TilerunnerInfo;

@Autonomous(name = "Slide to stone red")
public class SlideToStoneRed extends CknOpMode {



    private final String moduleName = "slideToStoneRed";

    private enum State{
        EXTEND,
        DRIVE,
        GRAB,
        STRAIGHT_UP,
        BACK_UP,
        DRIVE_TO_BRIDGE,
        DROP,
        PARK,
        RETRACT,
        END;
    }


    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;
    CknTimer timer;
    TilerunnerGrabberArm tilerunnerGrabberArm;

    //Called when init button is pressed
    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);

        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        timer = new CknTimer("autoTimer");

        //Set this to the first state in the program
        sm.start(State.EXTEND);
    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    // Main loop of program, loops after run is pressed
    @Override
    public void runContinuous(double elapsedTime){

       State state = sm.checkReadyAndGetState();

        if(state == null){
            //robot.dashboard.displayPrintf(4, "State: null");
        } else {

            robot.dashboard.displayPrintf(4, "State: %s", state);

            switch (state) {
                case EXTEND:

                    robot.grabberArm.extend(event,2.0);
                    sm.waitForSingleEvent(event,State.DRIVE);
                    break;

                case DRIVE:

                    //Example sideways movement
                    //arguments: pidDrive.setTarget(xTarget, yTarget, turnTarget, event, timeout);
                    robot.pidDrive.setTarget(0, -17, 0, event, 3.0);

                    sm.waitForSingleEvent(event, State.GRAB);
                    break;

                case GRAB:

                    robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_CLOSED_POS);
                    timer.set(2.0, event);

                    sm.waitForSingleEvent(event,State.STRAIGHT_UP);
                    break;

                case STRAIGHT_UP:
                    //robot.grabberArm.setMode(DcMotor.RunMode)
                   //robot.grabberArm.lowPosition(event,3.0);
                    //robot.grabberArm.setMode(DcMotor)
                    //robot.grabberArm.goToPosition(1600,event, 3.0);
                    //robot.grabberArm
                    //timer.set(4.0,event);
                    //robot.grabberArm.straightUp(event,4.0);
                    //tilerunnerGrabberArm.lowPosition(event, 4.0);
                    //robot.grabberArm.lowPosition(event,4.0);
                    //robot.grabberArm.goToPosition(TilerunnerInfo.GRABBER_STRAIGHT_UP_ENCODER_COUNT,event,4.0);
                    //robot.grabberArm.goToPosition(TilerunnerInfo.GRABBER_SECOND_POSITION_ENCODER_COUNT,event,4.0);
                    //robot.grabberArm.setPosition(TilerunnerInfo.GRABBER_SECOND_POSITION_ENCODER_COUNT);
                    robot.grabberArm.secondPosition(event, 4.0);
                    sm.waitForSingleEvent(event,State.BACK_UP);
                    break;

                case BACK_UP:

                    robot.pidDrive.setTarget(0,9,0,event,2.0);
                    sm.waitForSingleEvent(event, State.DRIVE_TO_BRIDGE);
                    break;

                case DRIVE_TO_BRIDGE:

                    robot.pidDrive.setTarget(-67,0,0,event,3.0);
                    sm.waitForSingleEvent(event, State.DROP);
                    break;

                case DROP:

                    robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_OPEN_POS);
                    timer.set(2.0, event);
                    sm.waitForSingleEvent(event,State.PARK);
                    break;

                case PARK:

                    robot.pidDrive.setTarget(12,0,0,event,3.0);
                    sm.waitForSingleEvent(event, State.RETRACT);
                    break;

                case RETRACT:

                    robot.grabberArm.retract(event,3.0);
                    sm.waitForSingleEvent(event,State.END);
                    break;

                case END:
                    sm.stop();
                    break;
            }
        }

    }

    //ignore
    @Override
    public void startMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        robot.startMode(nextMode);
    }

    @Override
    public void stopMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        robot.stopMode(prevMode);
    }

}
