package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import tilerunner.Tilerunner;

@Autonomous(name = "Blue Grab Foundation")
public class BlueGrabFoundation extends CknOpMode {

    private final String moduleName = "RedGrabFoundation";

    private enum State{
        DRIVE_TO_FOUNDATION,
        HOOK_FOUNDATION,
        DRIVE_TO_WALL,
        GET_TO_CORNER,
        BACK_UP,
        TURN,
        RELEASE_FOUNDATION,
        PARK,
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
        sm.start(State.DRIVE_TO_FOUNDATION);
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
                case DRIVE_TO_FOUNDATION:
                    event.clear();

                    // Sideways (X) drive to foundation
                    robot.pidDrive.setTarget(0, 30, 0, event, 3.0);

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
                    robot.pidDrive.setTarget(-20,0,0,event, 3.0);
                    sm.waitForSingleEvent(event, State.BACK_UP);
                    break;

                case BACK_UP:
                    event.clear();
                    robot.pidDrive.setTarget(0,-20,0,event, 3.0);
                    sm.waitForSingleEvent(event, State.RELEASE_FOUNDATION);
                    break;


                //case TURN:
                   // event.clear();
                    //robot.pidDrive.setTarget(0,2,90,event, 3.0);
                    //sm.waitForSingleEvent(event, State.RELEASE_FOUNDATION);
                case RELEASE_FOUNDATION:
                    event.clear();
                    robot.foundationGrabber.stopPid();
                    robot.foundationGrabber.release(event, 5.0);
                    //robot.driveBase.setSpeed(1.0);
                    sm.waitForSingleEvent(event, State.PARK);
                    //break;
                case PARK:
                    event.clear();
                    //Sideways drive to park zone.
                    robot.pidDrive.setTarget(50,-20,0,event,30.0);

                    sm.waitForSingleEvent(event, State.END);
                    break;
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
