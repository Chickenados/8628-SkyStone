package chickenados.tilerunner;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

import chickenados.testbot.CknTestBotAuto;
import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.location.CknPose;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;

@Autonomous(name = "MoveSideways", group = "TileRunner")
public class TileRunnerAutoMoveSideways extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();
    TileRunner robot;
    enum State {
        FRONT_LEFT,
        FRONT_RIGHT,
        BACK_LEFT,
        BACK_RIGHT,
        END;
    }

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();
    private CknStopwatch stopwatch = new CknStopwatch(event);

    private State currentState;

    @Override
    public void runOpMode() {

        robot = new TileRunner(hardwareMap, telemetry, false);

        sm.start(State.FRONT_LEFT);
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        waitForStart();

        //robot.vuforiaVision.setEnabled(true);

        while (opModeIsActive()) {
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());


            if (sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {

                    case FRONT_LEFT:
                        event.reset();
                        robot.frontLeft.setTargetPosition(1000);
                        robot.frontLeft.setPower(0.5);
                        sm.waitForEvent(event, State.FRONT_RIGHT);
                        break;
                    case FRONT_RIGHT:
                        event.reset();
                        robot.frontRight.setTargetPosition(-1000);
                        sm.waitForEvent(event, State.BACK_LEFT);
                        break;
                    case BACK_LEFT:
                        event.reset();
                        robot.backLeft.setTargetPosition(-1000);
                        sm.waitForEvent(event, State.BACK_RIGHT);
                        break;
                    case BACK_RIGHT:
                            event.reset();
                            robot.backRight.setTargetPosition(1000);
                            sm.waitForEvent(event, State.END);
                            break;
                    case END:
                        event.reset();
                        sm.stop();
                        break;
                }

            }
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}