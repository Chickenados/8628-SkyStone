package chickenados.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

//@Disabled
@TeleOp(name = "Measure Encoder Distance")
public class MeasureEncoderDist extends LinearOpMode {

    DcMotor testMotor;
    int motorDistance = 0;

    @Override
    public void runOpMode(){

        testMotor = hardwareMap.dcMotor.get("grabberArm");
        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        testMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while(opModeIsActive()){
            testMotor.setPower(gamepad1.right_stick_y);
            motorDistance = testMotor.getCurrentPosition();
            telemetry.addData("Motor Distance: ", motorDistance);
            telemetry.update();

        }

    }
}
