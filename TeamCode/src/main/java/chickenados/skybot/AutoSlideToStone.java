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

        robot.startVuforiaTracking();

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());

            if(currentState == State.SEARCH){
                vuforiaLocation = robot.trackLocation();
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
        robot.stopVuforiaTracking();
    }
}
