package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;

@Autonomous(name = "Grab Foundation Red")
public class RetrieveFoundationRed extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();
    CknSkyBot robot;

    enum State{
        FORWARD,
        FORWARD_AGAIN,
        GRAB,
        BACK,
        LIFT_ARM,
        TURN,
        MOVE,
        BACK_AGAIN,
        RETRACT,
        PUSH,
        REVERSE,
        END;

    }

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();
    private CknStopwatch stopwatch = new CknStopwatch(event);

    private State currentState;

    @Override
    public void runOpMode(){

        robot = new CknSkyBot(hardwareMap, telemetry, false);

        sm.start(State.FORWARD);

        waitForStart();

        //robot.vuforiaVision.setEnabled(true);

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());


            if(sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {


                    case FORWARD:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(15, 0, 2.0, event);

                        sm.waitForEvent(event, State.FORWARD_AGAIN);
                        break;
                    case FORWARD_AGAIN:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(5, 0, 2.0, event);

                        sm.waitForEvent(event, State.GRAB);
                        break;
                    case GRAB:
                        event.reset();

                        robot.grabberArm.extend(event, 2);

                        sm.waitForEvent(event, State.BACK);
                        break;
                    case BACK:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-23, 0, 2.0, event);

                        sm.waitForEvent(event, State.TURN);
                        break;
                    case TURN:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, -15, 2.0, event);

                        sm.waitForEvent(event, State.LIFT_ARM);
                        break;
                    case LIFT_ARM:
                        event.reset();

                        robot.grabberArm.lowPosition(event, 2);

                        sm.waitForEvent(event, State.MOVE);
                        break;
                    case MOVE:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(1,-15,2.0,event);

                        sm.waitForEvent(event, State.BACK_AGAIN);
                        break;
                    case BACK_AGAIN:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-4, -15, 2, event);

                        sm.waitForEvent(event, State.RETRACT);
                        break;
                    case RETRACT:
                        event.reset();

                        robot.grabberArm.retract(event,2 );

                        sm.waitForEvent(event, State.PUSH);
                        break;
                    case PUSH:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(2,-15,2, event);

                        sm.waitForEvent(event, State.REVERSE);
                        break;
                    case REVERSE:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-7,-0,2,event);

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
