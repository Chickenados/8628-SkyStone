package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Autonomous (name = "Simoncolorsensor")
public class Simoncolorsensor extends LinearOpMode {

    DcMotor leftWheel;
    DcMotor rightWheel;
    ColorSensor colorSensor;

    @Override
    public void runOpMode() throws InterruptedException {


        color_sensor = hardwareMap.colorSensor.get("colorSensor");
        leftWheel = hardwareMap.dcMotor.get("frontLeft");
        rightWheel = hardwareMap.dcMotor.get("frontRight");
        leftWheel.setDirection(DcMotor.Direction.REVERSE);
        color_sensor.enableLed(true);

        waitForStart();

        int colorSensed = colorSensor.argb();

        if (colorSensed >= 330 && colorSensed <= 360) {
                leftWheel.setPower(1);
                rightWheel.setPower(1);
                sleep(2000);
                rightWheel.setPower(0);
                leftWheel.setPower(0);
        }
        /*leftWheel.setPower(1);
        rightWheel.setPower(1);

        sleep(2000);

        leftWheel.setPower(0);
        rightWheel.setPower(0);

        sleep(1000);

        leftWheel.setPower(1);
        rightWheel.setPower(-1);
        */

    }
}
