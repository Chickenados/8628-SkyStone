package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import tilerunner.Tilerunner;

@Autonomous(name = "ParkOnly")
public class ParkOnly extends CknOpMode {



    private final String moduleName = "ParkOnly";


    private enum State{
        MOVE_FORWARD,
        END;
    }

    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;

    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);

        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        sm.start(State.MOVE_FORWARD);

    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    @Override
    public void runContinuous(double elapsedTime){

        State state = sm.checkReadyAndGetState();

        if(state == null){
            //robot.dashboard.displayPrintf(4, "State: null");
        } else {

            robot.dashboard.displayPrintf(4, "State: %s", state);

            switch (state) {
                case MOVE_FORWARD:

                    robot.pidDrive.setTarget(-30.5,0,event,3.0);
                    sm.waitForSingleEvent(event, State.END);
                    break;

                case END:
                    robot.driveBase.stop();
                    sm.stop();
                    break;

            }
        }

    }

    //ignore
    @Override
    public void startMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        robot.startMode(nextMode);
    }

    @Override
    public void stopMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){
        robot.stopMode(prevMode);
    }

}


