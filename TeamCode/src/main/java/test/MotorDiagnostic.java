package test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import chickenlib.opmode.CknDashboard;
import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import tilerunner.Tilerunner;

@TeleOp(name = "Motor Diagnositc")
public class MotorDiagnostic extends CknOpMode {

    DcMotor motor;
    final String MOTOR_NAME = "grabberArm";

    @Override
    public void initRobot(){
        motor = hardwareMap.dcMotor.get(MOTOR_NAME);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void runContinuous(double elapsedTime){
        CknDashboard.getInstance().displayPrintf(1, "Motor Position: %d", motor.getCurrentPosition());
        CknDashboard.getInstance().displayPrintf(2, "Joystick: %f", gamepad1.right_stick_y);
        motor.setPower(gamepad1.right_stick_y);

    }

    @Override
    public void startMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){

    }

    @Override
    public void stopMode(CknRobot.RunMode prevMode, CknRobot.RunMode nextMode){

    }

}
