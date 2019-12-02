package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.robot.CknPidDrive;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import skybot.Skybot;
import tilerunner.Tilerunner;

@Autonomous(name = "PID Movement Test")
public class TestMovement extends CknOpMode {

    private final String moduleName = "TestMovement";

    private enum State {
        DRIVE,
        END;
    }

    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;

    public void initPeriodic(){
        super.initPeriodic();
    }

    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);
        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        sm.start(State.DRIVE);
    }

    @Override
    public void runContinuous(double elapsedTime){
        State state = sm.checkReadyAndGetState();

        if(state == null){

        } else {

            switch (state){
                case DRIVE:

                    robot.pidDrive.setTarget(10, 0, 0, event, 2.0);

                    sm.waitForSingleEvent(event, State.END);
                    break;
                case END:
                    sm.stop();
                    break;
            }

        }
    }

    @Override
    public void startMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){

    }

    @Override
    public void stopMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        
    }

}
