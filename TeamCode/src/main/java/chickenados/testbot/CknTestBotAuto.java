package chickenados.testbot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;

@Autonomous(name="TestBot Autonomous")
public class CknTestBotAuto extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();

    CknTestBot robot;

    enum State{
        MOVEAWAY,
        UNDERBRIDGE,
        END;
    }

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();

    private State currentState;

    @Override
    public void runOpMode(){

        robot = new CknTestBot(hardwareMap, telemetry, false);

        waitForStart();

        sm.start(State.MOVEAWAY);

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());



            if(sm.isReady()){

                currentState = sm.getState();

                switch (currentState){
                    case MOVEAWAY:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(10, 0, 1, event);

                        sm.waitForEvent(event, State.UNDERBRIDGE);
                        break;
                    case UNDERBRIDGE:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(20,250,1, event);

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
