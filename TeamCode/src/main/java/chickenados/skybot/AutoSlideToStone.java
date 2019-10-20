package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

import chickenados.testbot.CknTestBotAuto;
import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;

@Autonomous(name = "Test Slide To Stone")
public class AutoSlideToStone extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();
    CknSkyBot robot;

    enum State{
        SEARCH,
        MOVE_TO_STONE,
        MOVE_TO_BRIDGE,
        MOVE_TO_PARK,
        END;
    }

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();

    private State currentState;

    OpenGLMatrix vuforiaLocation = null;

    @Override
    public void runOpMode(){

        robot = new CknSkyBot(hardwareMap, telemetry, true);

        sm.start(State.SEARCH);

        waitForStart();


        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());

            if(currentState == State.SEARCH){
                //vuforiaLocation = robot.trackLocation();
                if(vuforiaLocation != null && vuforiaLocation.getTranslation().get(0) != 0){
                    event.set(true);
                }
            }

            if(sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {
                    case SEARCH:
                        event.reset();

                        sm.waitForEvent(event, State.MOVE_TO_STONE);
                        break;
                    case MOVE_TO_STONE:
                        event.reset();

                        //robot.pidDrive.driveDistanceTank(4, 90, 1, event);
                        //robot.pidDrive.driveDistanceTank(0, 0, 1, event);
                        //based on vuforia target location the distance changes
                        robot.pidDrive.driveDistanceTank(19, 0, 2, event);
                        robot.grabberArm.extend(null,2.0);
                        robot.stoneGrabber.setPosition(180);
                        robot.grabberArm.lowPosition(null,2.0);
                        sm.waitForEvent(event, State.MOVE_TO_BRIDGE);
                        break;
                    case MOVE_TO_BRIDGE:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(0, 0, 1, event);
                        sm.waitForEvent(event, State.MOVE_TO_PARK);
                        break;
                    case MOVE_TO_PARK:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(0, 0, 1, event);
                        sm.waitForEvent(event, State.END);
                        break;
                    case END:
                        robot.dashboard.setLine(3, "Location: X: " + vuforiaLocation.getTranslation().get(0) / 25.4f +
                                " Y: " + vuforiaLocation.getTranslation().get(1) / 25.4f + " Z: " +
                                vuforiaLocation.getTranslation().get(2) / 25.4f);
                        event.reset();
                        sm.stop();
                        break;
                }
            }



            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
