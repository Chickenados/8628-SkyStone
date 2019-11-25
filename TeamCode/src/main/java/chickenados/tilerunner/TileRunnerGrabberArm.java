package chickenados.tilerunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import chickenlib.CknPIDController;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;


public class TileRunnerGrabberArm implements CknTaskManager.Task {

        CknPIDController grabberPID;
        DcMotor armMotor;
        int motorPosition;
        double startTime;
        double timeout;
        CknEvent event;


        public TileRunnerGrabberArm(DcMotor armMotor, CknPIDController pid){
                this.armMotor = armMotor;
                this.grabberPID = pid;
                this.motorPosition = armMotor.getCurrentPosition();
        }


        public void extend(CknEvent event, double timeout){
                goToPosition(TileRunnerInfo.GRABBER_EXTENDED_ENCODER_COUNT, event, timeout);
        }

        public void retract(CknEvent event, double timeout){
                goToPosition(TileRunnerInfo.GRABBER_RETRACTED_ENCODER_COUNT, event, timeout);
        }

        public void lowPosition(CknEvent event, double timeout){
                goToPosition(TileRunnerInfo.GRABBER_LOW_POSITION_ENCODER_COUNT, event, timeout);
        }

        public void highPosition(CknEvent event, double timeout){
                goToPosition(TileRunnerInfo.GRABBER_HIGH_POSITION_ENCODER_COUNT, event, timeout);
        }

        public void goToPosition(double position, CknEvent event, double timeout){
                this.startTime = CknUtil.getCurrentTime();
                this.timeout = timeout;
                this.grabberPID.setSetPoint(position, false);
                this.event = event;
                setTaskEnabled(true);
        }


        //Manually move arm with controller to calibrate zero position
        public void manualControl(double motorPower){
        setTaskEnabled(false);
        armMotor.setPower(motorPower);
        }

        //Set zero position
        public void calibrateZeroPosition(){
                armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                this.motorPosition = armMotor.getCurrentPosition();
        }

        public void stop(){
                setTaskEnabled(false);
                armMotor.setPower(0);
                grabberPID.reset();
                if(event != null){
                        event.set(true);
                }
        }

        public void setTaskEnabled(boolean enabled){
                if(enabled){
                CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
                } else {
                CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
                }
        }

@Override
public void preContinuous(){
        motorPosition = armMotor.getCurrentPosition();
        }

@Override
public void postContinuous(){


        double motorPower = grabberPID.getOutput();

        armMotor.setPower(Range.clip(motorPower, -TileRunnerInfo.GRABBER_ARM_MOTOR_SPEED, TileRunnerInfo.GRABBER_ARM_MOTOR_SPEED));
        if(grabberPID.onTarget() || CknUtil.getCurrentTime() > startTime + timeout){
        stop();
        }

        }
        }
