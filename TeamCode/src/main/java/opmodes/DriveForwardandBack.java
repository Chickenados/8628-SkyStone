    // autonomous program that drives bot forward a set distance, stops then
// backs up to the starting point using encoders to measure the distance.
// This example assumes there is one encoder, attached to the left motor.
package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.DcMotorController;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStateMachine;
import tilerunner.Tilerunner;

    @Autonomous(name = "Drive Forward And Back")
    public class DriveForwardandBack extends LinearOpMode {

       // Tilerunner robot;

        DcMotor frontLeft;
        DcMotor frontRight;
        DcMotor backLeft;
        DcMotor backRight;

        //private final String moduleName = "DriveForwardAndBack";

        /*package org.firstinspires.ftc.teamcode;

        import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
        import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.DcMotorController;

        @Autonomous(name="Drive Encoder", group="Exercises")
        //@Disabled
        public class DriveWithEncoder extends LinearOpMode
        {*/


        @Override
        public void runOpMode() throws InterruptedException
        {
            frontLeft = hardwareMap.dcMotor.get("frontLeft");
            frontRight = hardwareMap.dcMotor.get("frontRight");
            backLeft = hardwareMap.dcMotor.get("backRight");
            backRight = hardwareMap.dcMotor.get("backRight");

            frontLeft.setDirection(DcMotor.Direction.REVERSE);

            // reset encoder count kept by left motor.
            frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            // set left motor to run to target encoder position and stop with brakes on.
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // set right motor to run without regard to an encoder.
            frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            telemetry.addData("Mode", "waiting");
            telemetry.update();

            // wait for start button.

            waitForStart();

            telemetry.addData("Mode", "running");
            telemetry.update();

            // set left motor to run to 5000 encoder counts.

            frontLeft.setTargetPosition(5000);

            // set both motors to 25% power. Movement will start.

            frontLeft.setPower(0.25);
            frontRight.setPower(0.25);

            // wait while opmode is active and left motor is busy running to position.

            while (opModeIsActive() && frontLeft.isBusy())
            {
                telemetry.addData("encoder-fwd", frontLeft.getCurrentPosition() + "  busy=" + frontLeft.isBusy());
                telemetry.update();
                idle();
            }

            // set motor power to zero to turn off motors. The motors stop on their own but
            // power is still applied so we turn off the power.

            frontLeft.setPower(0.0);
            frontRight.setPower(0.0);

            // wait 5 sec so you can observe the final encoder position.

            resetStartTime();

            while (opModeIsActive() && getRuntime() < 5)
            {
                telemetry.addData("encoder-fwd-end", frontLeft.getCurrentPosition() + "  busy=" + frontLeft.isBusy());
                telemetry.update();
                idle();
            }

            // Now back up to starting point. In this example instead of
            // having the motor monitor the encoder, we will monitor the encoder ourselves.

            frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            frontLeft.setPower(-0.25);
            frontRight.setPower(-0.25);

            while (opModeIsActive() && frontLeft.getCurrentPosition() > 0)
            {
                telemetry.addData("encoder-back", frontLeft.getCurrentPosition());
                telemetry.update();
                idle();
            }

            // set motor power to zero to stop motors.

            frontLeft.setPower(0.0);
            frontRight.setPower(0.0);

            // wait 5 sec so you can observe the final encoder position.

            resetStartTime();

            while (opModeIsActive() && getRuntime() < 5)
            {
                telemetry.addData("encoder-back-end", frontLeft.getCurrentPosition());
                telemetry.update();
                idle();
            }
        }
    }

