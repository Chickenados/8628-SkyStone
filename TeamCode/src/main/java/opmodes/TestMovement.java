package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import skybot.Skybot;
import tilerunner.Tilerunner;

@Autonomous(name = "PID Movement Test")
public class TestMovement extends CknOpMode {

    CknRobot robot;

    public void initRobot(){
        robot = new Tilerunner(hardwareMap, telemetry, false);
    }

    public void runContinuous(){

    }

}
