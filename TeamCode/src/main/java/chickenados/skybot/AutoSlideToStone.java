package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

import chickenados.testbot.CknTestBotAuto;
import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.location.CknPose;
import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;

@Autonomous(name = "Test Slide To Stone")
public class AutoSlideToStone extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();
    CknSkyBot robot;

    enum State{
        MOVE_FORWARD,
        SEARCH,
        MOVE_TO_STONE,
        MOVE_AGAIN_TO_STONE,
        MOVE_TO_LOW_POSITION,
        MOVE_TO_BRIDGE,
        MOVE_TO_FOUNDATION,
        MOVE_FOUNDATION,
        MOVE_AWAY,
        END;
    }

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();

    private State currentState;

    OpenGLMatrix vuforiaLocation = null;
    CknPose skystonePose;

    @Override
    public void runOpMode(){

        robot = new CknSkyBot(hardwareMap, telemetry, true);

        sm.start(State.MOVE_FORWARD);

        waitForStart();

        robot.vuforiaVision.setEnabled(true);

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());

            if(currentState == State.SEARCH){
                skystonePose = robot.getSkystonePose();
                if(skystonePose != null) {
                    robot.dashboard.setLine(3, "Location X: " + skystonePose.x + " Y: " + skystonePose.y);
                    if(skystonePose.x != 0){
                        event.set(true);
                        robot.vuforiaVision.setEnabled(false);
                        sm.setState(State.MOVE_TO_STONE);
                    }
                }
            }

            if(sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {
                    case MOVE_FORWARD:
                        event.reset();
                        robot.stoneGrabber.setPosition(0);
                        robot.pidDrive.driveDistanceTank(10,0,2.0, event);
                        sm.waitForEvent(event, State.SEARCH);
                    case SEARCH:
                        event.reset();

                        sm.setState(State.MOVE_TO_STONE);
                        break;
                    case MOVE_TO_STONE:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(skystonePose.x+4.5, 90,2, event);
                        sm.waitForEvent(event, State.MOVE_AGAIN_TO_STONE);
                        break;
                    case MOVE_AGAIN_TO_STONE:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(8, -90, 2, event);
                        robot.grabberArm.extend(null,2.0);
                        robot.stoneGrabber.setPosition(93);
                        sm.waitForEvent(event, State.MOVE_TO_LOW_POSITION);
                        break;
                    case MOVE_TO_LOW_POSITION:
                        event.reset();
                        robot.grabberArm.lowPosition(null,2.0);
                        sm.waitForEvent(event, State.END);
                        break;
                    case MOVE_TO_FOUNDATION:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(-42, -90,2, event);
                        sm.waitForEvent(event, State.MOVE_FOUNDATION);
                        break;
                    case MOVE_FOUNDATION:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(-6, 90,2, event);
                        sm.waitForEvent(event, State.MOVE_AWAY);
                        break;
                    case MOVE_AWAY:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(24, 0,2, event);
                        sm.waitForEvent(event, State.END);
                        break;
                    case MOVE_TO_BRIDGE:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(48, -90,2, event);
                        sm.waitForEvent(event, State.END);
                        break;
                    case END:
                        robot.vuforiaVision.setEnabled(false);
                        event.reset();
                        sm.stop();
                        break;
                }
            }



            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
