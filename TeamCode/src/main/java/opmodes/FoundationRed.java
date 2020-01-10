package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import tilerunner.Tilerunner;

@Autonomous(name = "FoundationRed")
public class FoundationRed extends CknOpMode {

    private final String moduleName = "FoundationRed";

    private enum State{
        DRIVE_BACKWARDS,
        HOOK_FOUNDATION,
        DRIVE_TO_WALL,
        RELEASE_FOUNDATION,
        STRAFE_TO_PARK,
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
        sm.start(State.DRIVE_BACKWARDS);
    }
//test
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
                case DRIVE_BACKWARDS:
                    event.clear();

                    // Sideways (X) drive to foundation
                    robot.pidDrive.setTarget(0, 20, 0, event, 3.0);

                    sm.waitForSingleEvent(event, State.HOOK_FOUNDATION);
                    break;
                case HOOK_FOUNDATION:

                    event.clear();
                    robot.foundationGrabber.grab(event, 1.0);

                    sm.waitForSingleEvent(event, State.DRIVE_TO_WALL);
                    break;
                case DRIVE_TO_WALL:
                    event.clear();

                    //Sideways slow drive to wall
                    //robot.driveBase.setSpeed(0.5);
                    robot.pidDrive.setTarget(0, -20, 0, event, 5.0);

                    sm.waitForSingleEvent(event, State.RELEASE_FOUNDATION);
                case RELEASE_FOUNDATION:
                    event.clear();

                    robot.foundationGrabber.release(event, 1.0);
                    //robot.driveBase.setSpeed(1.0);




                    //Forward drive to park zone
                    //robot.pidDrive.setTarget(-30, 0, event, 3.0);
                    sm.waitForSingleEvent(event, State.STRAFE_TO_PARK);
                case STRAFE_TO_PARK:
                    event.clear();

                    robot.pidDrive.setTarget(0,-30,0,event, 2.0);

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