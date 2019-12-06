package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.robot.CknPidDrive;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import skybot.Skybot;
import tilerunner.Tilerunner;
@Disabled
@Autonomous(name = "PID Movement Test", group="FtcAuto")
public class TestMovement extends CknOpMode {

    private final String moduleName = "TestMovement";

    private enum State {
        DRIVE,
        END;
    }

    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;

    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);

        robot.pidDrive.setStallTimeout(0.0);

        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        sm.start(State.DRIVE);
    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    @Override
    public void runContinuous(double elapsedTime){
        displayOdometry();

        State state = sm.checkReadyAndGetState();


        if(state == null){
            //robot.dashboard.displayPrintf(4, "State: null");
        } else {

            robot.dashboard.displayPrintf(4, "State: %s", state);

            switch (state){
                case DRIVE:

                    robot.pidDrive.setTarget(10, 0, 0, event, 5.0);

                    sm.waitForSingleEvent(event, State.END);
                    break;
                case END:
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

    public void displayOdometry(){
        robot.dashboard.displayPrintf(1, "X: %f", robot.driveBase.getXPosition());
        robot.dashboard.displayPrintf(2, "Y: %f", robot.driveBase.getYPosition());
        robot.dashboard.displayPrintf(3, "Heading: %f", robot.driveBase.getHeading());
        robot.xPid.displayPidInfo(5);
        robot.yPid.displayPidInfo(7);
        robot.turnPid.displayPidInfo(9);
    }

}
