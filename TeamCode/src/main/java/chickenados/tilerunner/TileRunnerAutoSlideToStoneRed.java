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
import android.service.quicksettings.Tile;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

import chickenados.testbot.CknTestBotAuto;
import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.location.CknPose;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;

@Autonomous(name = "TileRunner RED Slide To Stone", group = "TileRunner")
public class TileRunnerAutoSlideToStoneRed {


        CknTaskManager mgr = new CknTaskManager();
        TileRunner robot;
    enum State {
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
        TURN,
        MOVE_FORWARD,
        RETRACT,
        PARK,
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

    //@Override
    public void runOpMode() {

       //robot = new TileRunner(hardwareMap, telemetry, true);

        sm.start(State.MOVE_FORWARD_TO_SCAN);

     //   waitForStart();

        robot.vuforiaVision.setEnabled(true);

     //   while (opModeIsActive()) {
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());

            if (sm.getState() == State.SCAN) {
                skystonePose = robot.getSkystonePose();
            }

            if (sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {case MOVE_FORWARD_TO_SCAN:
                    event.reset();
                    robot.pidDrive.driveDistanceTank(9.0, 0, 2.0, event);
                    sm.waitForEvent(event, State.TURN_TO_STONE);
                    break;
                    case SCAN:
                        event.reset();

                        stopwatch.setTimer(5);

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

                        if (skystonePose != null) {
                            turnAmount = Math.toDegrees(Math.atan(skystonePose.y / skystonePose.x));
                        } else {
                            turnAmount = 0;
                        }

                        robot.pidDrive.driveDistanceTank(0, turnAmount, 2, event);
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
                        sm.waitForEvent(event, State.RETRACT);
                        break;
                    case GRAB_STONE:
                        event.reset();

                        robot.stoneGrabber.setPosition(97);

                        stopwatch.setTimer(2.0);

                        sm.waitForEvent(event, State.MOVE_TO_LOW_POSITION);
                        break;
                    case MOVE_TO_LOW_POSITION:
                        event.reset();

                        robot.grabberArm.lowPosition(event, 2.0);

                        sm.waitForEvent(event, State.BACK_UP);
                        break;
                    case BACK_UP:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(-11, -90, 2, event);
                        sm.waitForEvent(event, State.MOVE_TO_FOUNDATION);
                        break;
                    case MOVE_TO_FOUNDATION:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(40, -90, 2, event);

                        sm.waitForEvent(event, State.DROP_STONE);
                        break;

                    case TURN:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(2,90,2,event);
                        sm.waitForEvent(event,State.MOVE_FORWARD);
                        break;

                    case MOVE_FORWARD:
                        event.reset();
                        robot.pidDrive.driveDistanceTank(2,90,2,event);

                        sm.waitForEvent(event,State.DROP_STONE);
                    case DROP_STONE:
                        event.reset();

                        robot.stoneGrabber.setPosition(0);
                        stopwatch.setTimer(0.3);

                        sm.waitForEvent(event, State.RETRACT);
                        break;
                    case RETRACT:
                        event.reset();

                        robot.grabberArm.retract(event, 2);

                        sm.waitForEvent(event, State.END);
                        break;
                    case PARK:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-10,-135,2,event);

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


