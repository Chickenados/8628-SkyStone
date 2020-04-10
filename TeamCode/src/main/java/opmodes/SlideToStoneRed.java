/*package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import chickenados.tilerunner.SkystoneAnalyzer;
import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import chickenlib.util.CknTimer;
import chickenlib.util.CknUtil;
import tilerunner.Tilerunner;
import tilerunner.TilerunnerGrabberArm;
import tilerunner.TilerunnerInfo;

@Autonomous(name = "Slide to stone red")
public class SlideToStoneRed extends CknOpMode {



    private final String moduleName = "slideToStoneRed";

    private enum State{
        // Potentially:
        // SCAN_STONES,

        EXTEND,
        DRIVE,
        GRAB,
        STRAIGHT_UP,
        BACK_UP,
        DRIVE_TO_BRIDGE,
        DROP,
        PARK,
        RETRACT,
        END;
    }

    private final boolean DO_SCAN_MINERALS = false;
    private final int SCAN_TIMEOUT = 5;

    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;
    CknTimer timer;
    TilerunnerGrabberArm tilerunnerGrabberArm;

    //private SkystoneAnalyzer.SkystoneState SkystoneState = SkystoneAnalyzer.SkystoneState.UNKNOWN;

    //Called when init button is pressed
    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, true);

        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        timer = new CknTimer("autoTimer");

        //Set this to the first state in the program
        sm.start(State.EXTEND);
    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    // Main loop of program, loops after run is pressed
    @Override
    public void runContinuous(double elapsedTime){

       State state = sm.checkReadyAndGetState();

        if(state == null){
            //robot.dashboard.displayPrintf(4, "State: null");
        } else {

            robot.dashboard.displayPrintf(4, "State: %s", state);

            switch (state) {
                /*case SCAN_STONES:
                    event.reset();

                    double startTime = CknUtil.getCurrentTime();

                    while(SkystoneState== SkystoneAnalyzer.SkystoneState.UNKNOWN
                            && CknUtil.getCurrentTime() < startTime + SCAN_TIMEOUT){
                        SkystoneState = robot.analyzer.analyzeTFOD(robot.tfod.getUpdatedRecognitions());
                        robot.dashboard.setLine(3, "Gold State: " + goldState);
                    }
                    event.set(true);

                    sm.waitForEvent(event, State.LOWER_LIFT);
                    break;*/
package opmodes;

import chickenlib.opmode.CknOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import chickenlib.util.CknTimer;
import tilerunner.Tilerunner;
import tilerunner.TilerunnerInfo;

@Autonomous(name = "Slide to stone red")
public class SlideToStoneRed extends CknOpMode {

    private final String moduleName = "slideToStoneRed";

    private enum State{
        EXTEND,
        DRIVE,
        GRAB,
        STRAIGHT_UP,
        BACK_UP,
        DRIVE_TO_BRIDGE,
        DROP,
        PARK,
        GO_BACK,
        GO_FORWARD,
        GRAB_AGAIN,
        BACK,
        STRAFE,
        DROP_AGAIN,
        PARK_AGAIN,
        RETRACT,
        END;
    }

    Tilerunner robot;
    CknStateMachine<State> sm;
    CknEvent event;
    CknTimer timer;

    //Called when init button is pressed
    @Override
    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);

        event = new CknEvent(moduleName);
        sm = new CknStateMachine<>(moduleName);
        timer = new CknTimer("autoTimer");

        //Set this to the first state in the program
        sm.start(State.EXTEND);
    }

    @Override
    public void initPeriodic(){
        super.initPeriodic();
    }

    // Main loop of program, loops after run is pressed
    @Override
    public void runContinuous(double elapsedTime){

        State state = sm.checkReadyAndGetState();

        if(state == null){
            //robot.dashboard.displayPrintf(4, "State: null");
        } else {

            robot.dashboard.displayPrintf(4, "State: %s", state);

            switch (state) {
                case EXTEND:

                    robot.grabberArm.extend(event,2.0);
                    sm.waitForSingleEvent(event,State.DRIVE);
                    break;

                case DRIVE:

                    //Example sideways movement
                    //arguments: pidDrive.setTarget(xTarget, yTarget, turnTarget, event, timeout);
                    robot.pidDrive.setTarget(0, -17, 0, event, 2.0);

                    sm.waitForSingleEvent(event, State.GRAB);
                    break;

                case GRAB:

                    robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_CLOSED_POS);
                    timer.set(1.5, event);

                    sm.waitForSingleEvent(event,State.BACK_UP);
                    break;

                case STRAIGHT_UP:
                    //robot.grabberArm.setMode(DcMotor.RunMode)
                   //robot.grabberArm.lowPosition(event,3.0);
                    //robot.grabberArm.setMode(DcMotor)
                    //robot.grabberArm.goToPosition(1600,event, 3.0);
                    //robot.grabberArm
                    //timer.set(4.0,event);
                    //robot.grabberArm.straightUp(event,4.0);
                    //tilerunnerGrabberArm.lowPosition(event, 4.0);
                    //robot.grabberArm.lowPosition(event,4.0);
                    //robot.grabberArm.goToPosition(TilerunnerInfo.GRABBER_STRAIGHT_UP_ENCODER_COUNT,event,4.0);
                    //robot.grabberArm.goToPosition(TilerunnerInfo.GRABBER_SECOND_POSITION_ENCODER_COUNT,event,4.0);
                    //robot.grabberArm.setPosition(TilerunnerInfo.GRABBER_SECOND_POSITION_ENCODER_COUNT);
                    robot.grabberArm.secondPosition(event, 4.0);
                    sm.waitForSingleEvent(event,State.BACK_UP);
                    break;

                case BACK_UP:

                    robot.pidDrive.setTarget(0,10,0,event,1.5);
                    sm.waitForSingleEvent(event, State.DRIVE_TO_BRIDGE);
                    break;

                case DRIVE_TO_BRIDGE:

                    robot.pidDrive.setTarget(-40,0,0,event,3.0);
                    sm.waitForSingleEvent(event, State.DROP);
                    robot.driveBase.setSpeed(2.0);
                    break;

                case DROP:

                    robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_OPEN_POS);
                    timer.set(2.0, event);
                    sm.waitForSingleEvent(event,State.GO_BACK);
                    break;

                /*case PARK:

                    robot.pidDrive.setTarget(13,0,0,event,3.0);
                    sm.waitForSingleEvent(event, State.GO_BACK);
                    break;*/
                case GO_BACK:

                    robot.pidDrive.setTarget(33.5,0,0,event,2.5);
                    sm.waitForSingleEvent(event, State.GO_FORWARD);
                    break;
                case GO_FORWARD:

                    robot.pidDrive.setTarget(0,-15,0,event,1.5);
                    sm.waitForSingleEvent(event, State.GRAB_AGAIN);
                    break;

                case GRAB_AGAIN:

                    robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_CLOSED_POS);
                    timer.set(1.5, event);

                    sm.waitForSingleEvent(event,State.BACK);
                    break;
                case BACK:

                    robot.pidDrive.setTarget(0,10,0,event,3.0);
                    sm.waitForSingleEvent(event, State.STRAFE);
                    break;

                    //MAYBE TURN HERE TO DROP ON FOUNDATION? more comments
                case STRAFE:

                    robot.pidDrive.setTarget(-31,0,0,event,3.0);
                    robot.driveBase.setSpeed(2.0);

                    sm.waitForSingleEvent(event, State.DROP_AGAIN);

                    break;

                case DROP_AGAIN:

                    robot.stoneGrabber.setPosition(TilerunnerInfo.STONE_GRABBER_OPEN_POS);
                    timer.set(1.0, event);
                    sm.waitForSingleEvent(event,State.PARK_AGAIN);
                    break;
                case PARK_AGAIN:

                    robot.pidDrive.setTarget(12.5,0,0,event,4.0);
                    sm.waitForSingleEvent(event, State.END);
                    break;

                case END:
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
