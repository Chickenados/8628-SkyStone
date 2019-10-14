package chickenados.skybot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import chickenlib.CknPIDController;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;

public class SkybotGrabberArm implements CknTaskManager.Task {

    CknPIDController grabberPID;
    DcMotor armMotor;
    int motorPosition;
    double startTime;
    double timeout;
    CknEvent event;


    public SkybotGrabberArm(DcMotor armMotor, CknPIDController pid){
        this.armMotor = armMotor;
        this.grabberPID = pid;
        this.motorPosition = armMotor.getCurrentPosition();
    }


    public void extend(CknEvent event, double timeout){
        this.startTime = CknUtil.getCurrentTime();
        this.timeout = timeout;
        this.grabberPID.setSetPoint(CknSkyBotInfo.GRABBER_EXTENDED_ENCODER_COUNT, false);
        this.event = event;
        setTaskEnabled(true);
    }

    public void retract(CknEvent event, double timeout){
        this.startTime = CknUtil.getCurrentTime();
        this.timeout = timeout;
        this.grabberPID.setSetPoint(CknSkyBotInfo.GRABBER_RETRACTED_ENCODER_COUNT, false);
        this.event = event;
        setTaskEnabled(true);
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

        armMotor.setPower(Range.clip(motorPower, -1.0, 1.0));
        if(grabberPID.onTarget() || CknUtil.getCurrentTime() > startTime + timeout){
            stop();
        }

    }
}
