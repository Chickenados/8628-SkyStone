package chickenados.tilerunner;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;


    @Autonomous(name = "MoveSidewaysBlue", group = "TileRunner")
    public class TileRunnerAutoMoveSidewaysBlue extends LinearOpMode {

        CknTaskManager mgr = new CknTaskManager();
        TileRunner robot;
        enum State {
            FRONT_LEFT,
            FRONT_RIGHT,
            BACK_LEFT,
            BACK_RIGHT,
            GRAB_FOUNDATION,
            MOVE_BACK,
            RELEASE_FOUNDATION,
            FRONT_LEFT_BACK,
            FRONT_RIGHT_BACK,
            BACK_LEFT_BACK,
            BACK_RIGHT_BACK,
            END;
        }

        CknStateMachine<chickenados.tilerunner.TileRunnerAutoMoveSidewaysBlue.State> sm = new CknStateMachine<>();
        private CknEvent event = new CknEvent();
        private CknStopwatch stopwatch = new CknStopwatch(event);

        private chickenados.tilerunner.TileRunnerAutoMoveSidewaysBlue.State currentState;

        @Override
        public void runOpMode() {

            robot = new TileRunner(hardwareMap, telemetry, false);

            sm.start(chickenados.tilerunner.TileRunnerAutoMoveSidewaysBlue.State.FRONT_LEFT);
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
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.FRONT_RIGHT);
                            break;
                        case FRONT_RIGHT:
                            event.reset();
                            robot.frontRight.setTargetPosition(-1000);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.BACK_LEFT);
                            break;
                        case BACK_LEFT:
                            event.reset();
                            robot.backLeft.setTargetPosition(-1000);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.BACK_RIGHT);
                            break;
                        case BACK_RIGHT:
                            event.reset();
                            robot.backRight.setTargetPosition(1000);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.GRAB_FOUNDATION);
                            break;
                        case GRAB_FOUNDATION:
                            event.reset();
                            robot.foundationGrabber.goToPosition(1000, null, 1.0);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.END);
                            break;
                        case FRONT_LEFT_BACK:
                            event.reset();
                            robot.frontLeft.setTargetPosition(1000);
                            robot.frontLeft.setPower(0.5);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.FRONT_RIGHT_BACK);
                            break;
                        case FRONT_RIGHT_BACK:
                            event.reset();
                            robot.frontRight.setTargetPosition(-1000);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.BACK_LEFT_BACK);
                            break;
                        case BACK_LEFT_BACK:
                            event.reset();
                            robot.backLeft.setTargetPosition(-1000);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.BACK_RIGHT_BACK);
                            break;
                        case BACK_RIGHT_BACK:
                            event.reset();
                            robot.backRight.setTargetPosition(1000);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.RELEASE_FOUNDATION);
                            break;
                        case RELEASE_FOUNDATION:
                            event.reset();
                            robot.foundationGrabber.goToPosition(50, null, 1.0);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.MOVE_BACK);
                        case MOVE_BACK:
                            event.reset();
                            robot.pidDrive.driveDistanceTank(18,0,2,event);
                            sm.waitForEvent(event, TileRunnerAutoMoveSidewaysBlue.State.END);
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