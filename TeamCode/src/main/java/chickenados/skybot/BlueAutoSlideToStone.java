package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.location.CknPose;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;

@Autonomous(name = "BLUE Slide To Stone")
public class BlueAutoSlideToStone extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();
    CknSkyBot robot;

    enum State{
        MOVE_FORWARD_TO_SCAN,
        MOVE_FORWARD_FROM_SCAN,
        SCAN,
        TURN_TO_STONE,
        MOVE_AGAIN_TO_STONE,
        EXTEND_ARM,
        GRAB_STONE,
        MOVE_TO_LOW_POSITION,
        BACK_UP,
        MOVE_TO_FOUNDATION,
        DROP_STONE,
        COME_BACK,
        RETRACT,
        PARK,
        MOVE_FROM_WALL,
        TURN_TO_STONE2,
        LOWER_ARM,
        FORWARD_TO_STONE2,
        GRAB_STONE2,
        STONE2_LOW_POSITION,
        BACK_FROM_STONE2,
        STONE2_TURN_TO_FOUNDATION,
        STONE2_MOVE_TO_FOUNDATION,
        STONE2_RELEASE,
        BACK_AND_PARK,
        END;

    }

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();
    private CknStopwatch stopwatch = new CknStopwatch(event);

    private State currentState;

    OpenGLMatrix vuforiaLocation = null;
    CknPose skystonePose;
    double searchStartTime;
    double turnAmount;

    @Override
    public void runOpMode(){

        robot = new CknSkyBot(hardwareMap, telemetry, true);

        sm.start(State.MOVE_FORWARD_TO_SCAN);

        waitForStart();

        robot.vuforiaVision.setEnabled(true);

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());

            if(sm.getState() == State.SCAN){
                skystonePose = robot.getSkystonePose();
            }

            if(sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {
                    case MOVE_FORWARD_TO_SCAN:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(9.0,0,2.0, event);
                        sm.waitForEvent(event, State.TURN_TO_STONE);
                        break;
                    case SCAN:
                        event.reset();

                        stopwatch.setTimer(0.5);

                        sm.waitForEvent(event, State.MOVE_FORWARD_FROM_SCAN);
                        break;
                    case MOVE_FORWARD_FROM_SCAN:
                        event.reset();

                        skystonePose = robot.getSkystonePose();
                        //robot.pidDrive.driveDistanceTank(5.0,0,2.0, event);

                        sm.waitForEvent(event, State.TURN_TO_STONE);
                        break;
                    case TURN_TO_STONE:
                        event.reset();

                        if(skystonePose != null){
                            turnAmount = Math.toDegrees(Math.atan(skystonePose.y/skystonePose.x));
                        } else {
                            turnAmount = 0;
                        }

                        robot.pidDrive.driveDistanceTank(0, turnAmount,2, event);
                        sm.waitForEvent(event, State.EXTEND_ARM);
                        break;
                    case EXTEND_ARM:
                        event.reset();

                        robot.grabberArm.extend(event, 2.0);

                        sm.waitForEvent(event, State.MOVE_AGAIN_TO_STONE);
                        break;
                    case MOVE_AGAIN_TO_STONE:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(8, turnAmount, 2, event);
                        sm.waitForEvent(event, State.GRAB_STONE);
                        break;
                    case GRAB_STONE:
                        event.reset();

                        robot.stoneGrabber.setPosition(97);

                        stopwatch.setTimer(2.0);

                        sm.waitForEvent(event, State.MOVE_TO_LOW_POSITION);
                        break;
                    case MOVE_TO_LOW_POSITION:
                        event.reset();

                        robot.grabberArm.lowPosition(event,2.0);

                        sm.waitForEvent(event, State.BACK_UP);
                        break;
                    case BACK_UP:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(-11, 90,2, event);
                        sm.waitForEvent(event, State.MOVE_TO_FOUNDATION);
                        break;
                    case MOVE_TO_FOUNDATION:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(40, 90,2, event);

                        sm.waitForEvent(event, State.DROP_STONE);
                        break;
                    case DROP_STONE:
                        event.reset();

                        robot.stoneGrabber.setPosition(0);
                        stopwatch.setTimer(0.3);

                        sm.waitForEvent(event, State.RETRACT);
                        break;

                    case RETRACT:
                        event.reset();

                        robot.grabberArm.retract(event, 2);

                        sm.waitForEvent(event, State.PARK);

                        break;
                    case PARK:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-6,90,2,event);

                        sm.waitForEvent(event, State.END





                        );
                        break;

                    /*case COME_BACK:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-40,90,2,event);

                        sm.waitForEvent(event, State.MOVE_FROM_WALL);
                        break;
                    case MOVE_FROM_WALL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(5,90,2,event);

                        sm.waitForEvent(event, State.TURN_TO_STONE2);
                        break;
                    case TURN_TO_STONE2:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0,0,2,event);

                        sm.waitForEvent(event, State.LOWER_ARM);
                        break;
                    case LOWER_ARM:
                        event.reset();

                        robot.grabberArm.extend(event, 2.0);

                        sm.waitForEvent(event, State.FORWARD_TO_STONE2);
                        break;
                    case FORWARD_TO_STONE2:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(5,0,2,event);

                        sm.waitForEvent(event, State.GRAB_STONE2);
                        break;
                    case GRAB_STONE2:
                        event.reset();

                        robot.stoneGrabber.setPosition(97);
                        stopwatch.setTimer(1.2);

                        sm.waitForEvent(event, State.STONE2_LOW_POSITION);
                        break;
                    case STONE2_LOW_POSITION:
                        event.reset();

                        robot.grabberArm.lowPosition(event, 2.0);

                        sm.waitForEvent(event, State.BACK_FROM_STONE2);
                        break;
                    case BACK_FROM_STONE2:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-11, 0,2, event);

                        sm.waitForEvent(event, State.STONE2_TURN_TO_FOUNDATION);
                        break;
                    case STONE2_TURN_TO_FOUNDATION:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, 90, 2, event);

                        sm.waitForEvent(event, State.STONE2_MOVE_TO_FOUNDATION);
                        break;
                    case STONE2_MOVE_TO_FOUNDATION:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(45, 90, 2, event);

                        sm.waitForEvent(event, State.STONE2_RELEASE);
                        break;
                    case STONE2_RELEASE:
                        event.reset();

                        robot.stoneGrabber.setPosition(0);
                        stopwatch.setTimer(.7);

                        sm.waitForEvent(event, State.BACK_AND_PARK);
                        break;
                    case BACK_AND_PARK:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-12, 90, 2, event);

                        sm.waitForEvent(event, State.END);
                        break;*/
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
