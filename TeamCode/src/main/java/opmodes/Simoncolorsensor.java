package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous (name = "Simoncolorsensor")
public class Simoncolorsensor extends LinearOpMode {

    DcMotor leftWheel;
    DcMotor rightWheel;

    @Override
    public void runOpMode() throws InterruptedException {

        leftWheel = hardwareMap.dcMotor.get("frontLeft");
        rightWheel = hardwareMap.dcMotor.get("frontRight");
        leftWheel.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        leftWheel.setPower(1);
        rightWheel.setPower(1);

        sleep(2000);

        leftWheel.setPower(0);
        rightWheel.setPower(0);

        sleep(1000);

        leftWheel.setPower(1);
        rightWheel.setPower(-1);


    }
}
