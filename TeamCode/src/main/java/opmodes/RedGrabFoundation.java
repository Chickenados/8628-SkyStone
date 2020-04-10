package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import tilerunner.Tilerunner;



@Autonomous(name = "Red Grab Foundation")
public class RedGrabFoundation extends CknOpMode {

    private final String moduleName = "RedGrabFoundation";

    private enum State{
        SIDE,
        DRIVE_TO_FOUNDATION,
        HOOK_FOUNDATION,
        DRIVE_TO_WALL,
        GET_TO_CORNER,
        BACK_UP,
        RELEASE_FOUNDATION,
        DRIVE,
        RESET,
        PARK,
        RESET_AGAIN,
        PARK_AGAIN,
        TURN,
        END;
    }

    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;

    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);

        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        sm.start(State.SIDE);
    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    @Override
    public void runContinuous(double elapsedTime){

        State state = sm.checkReadyAndGetState();

        if(state == null){
            //robot.dashboard.displayPrintf(4, "State: null");
        } else {

            robot.dashboard.displayPrintf(4, "State: %s", state);

            switch (state){
                case SIDE:
                    event.clear();

                    // Sideways (X) drive to foundation
                    robot.pidDrive.setTarget(13, 0, 0, event, 3.0);

                    sm.waitForSingleEvent(event, State.DRIVE_TO_FOUNDATION);
                    break;

                case DRIVE_TO_FOUNDATION:
                    event.clear();

                    // Sideways (X) drive to foundation
                    robot.pidDrive.setTarget(0, 27.5, 0, event, 3.0);

                    sm.waitForSingleEvent(event, State.HOOK_FOUNDATION);
                    break;
                case HOOK_FOUNDATION:

                    event.clear();
                    robot.foundationGrabber.grab(event, 3.0);

                    sm.waitForSingleEvent(event, State.DRIVE_TO_WALL);
                    break;
                case DRIVE_TO_WALL:

                    event.clear();
                    //May need to add a motion downwards for foundation grabber maybe set power
                    //Sideways slow drive to wall
                    //robot.driveBase.setSpeed(0.5);
                    robot.pidDrive.setTarget(0, -38, 0, event, 3.0);

                    sm.waitForSingleEvent(event, State.GET_TO_CORNER);
                    //Look here
                    break;

                case GET_TO_CORNER:
                    event.clear();
                    robot.pidDrive.setTarget(12,0,0,event, 3.0);
                    sm.waitForSingleEvent(event, State.BACK_UP);
                    break;

                case BACK_UP:
                    event.clear();
                    robot.pidDrive.setTarget(0,-10,0,event, 3.0);
                    sm.waitForSingleEvent(event, State.RELEASE_FOUNDATION);
                    break;


                //case TURN:
                   // event.clear();
                    //robot.pidDrive.setTarget(0,2,90,event, 3.0);
                    //sm.waitForSingleEvent(event, State.RELEASE_FOUNDATION);
                case RELEASE_FOUNDATION:
                    event.clear();
                    robot.foundationGrabber.stopPid();
                    robot.foundationGrabber.release(event, 3.0);
                    //robot.driveBase.setSpeed(1.0);
                    sm.waitForSingleEvent(event, State.DRIVE);
                    //break;
                case DRIVE:
                    event.clear();
                    //DRIVE TO WALL HALFWAY
                    robot.pidDrive.setTarget(-10,0,0,event,3.0);

                    sm.waitForSingleEvent(event, State.RESET);
                    break;
                case RESET:
                    event.clear();
                    //BACK UP TO RESET
                    robot.pidDrive.setTarget(0,-6,0,event,3.0);

                    sm.waitForSingleEvent(event, State.PARK);
                    break;
                case PARK:
                    event.clear();
                    //Sideways drive to park zone.
                    //ADDED IN NEG 20 TO TRY AND REACH WALL
                    robot.pidDrive.setTarget(-60,0,0,event,3.0);

                    sm.waitForSingleEvent(event, State.RESET_AGAIN);
                    break;
                case RESET_AGAIN:
                    event.clear();
                    robot.pidDrive.setTarget(0,-6,0,event,3.0);

                    sm.waitForSingleEvent(event, State.PARK_AGAIN);
                    break;
                case PARK_AGAIN:
                    event.clear();

                    robot.pidDrive.setTarget(-10,0,0,event,3.0);

                    sm.waitForSingleEvent(event, State.END);
                    break;
                /*case TURN:
                    event.clear();

                    robot.pidDrive.setTarget(3,0,90,event,3.0);

                    sm.waitForSingleEvent(event, State.END);
                    break;*/


                case END:
                    event.clear();
                    robot.driveBase.stop();
                    sm.stop();
                    break;
            }

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
