package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "TestBot")
public class TestTeleOp extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;

    DcMotor armMotor;

    @Override
    public void runOpMode(){

        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        rearLeft = hardwareMap.dcMotor.get("backLeft");
       // rearRight = hardwareMap.dcMotor.get("backRight");

        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        armMotor = hardwareMap.dcMotor.get("armMotor");

        waitForStart();

        while(opModeIsActive()){

            frontLeft.setPower(gamepad1.left_stick_y);
            rearLeft.setPower(gamepad1.left_stick_y);

            frontRight.setPower(gamepad1.right_stick_y);
         //   rearRight.setPower(gamepad1.right_stick_y);

            if(gamepad1.left_trigger > 0.0){
                armMotor.setPower(0.6);
            } else if(gamepad1.right_trigger > 0.0){
                armMotor.setPower(-0.6);
            } else {
                armMotor.setPower(0);
            }
        }

    }

}
