package tilerunner;

import chickenlib.pid.CknPidMotor;
import chickenlib.util.CknEvent;

public class TilerunnerGrabberArm {

    CknPidMotor pidMotor;

    public TilerunnerGrabberArm(CknPidMotor pidMotor){
        this.pidMotor = pidMotor;
    }

    public void recalibrate(){
        pidMotor.resetMotorPosition(false);
    }

    public boolean isPidActive(){
        return pidMotor.isActive();
    }

    public boolean motorHasPower(){
        return pidMotor.getMotor().getPower() != 0.0;
    }

    public void extend(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_EXTENDED_ENCODER_COUNT, event, timeout);
    }

    public void extend(double timeout){
        extend(null, timeout);
    }

    public void retract(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_RETRACTED_ENCODER_COUNT, event, timeout);
    }

    public void retract(double timeout){
        retract(null, timeout);
    }

    public void straightUp(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_STRAIGHT_UP_ENCODER_COUNT, event, timeout);
    }

    public void straightUp(double timeout){
        straightUp(null, timeout);
    }

    public void manualControl(double power){
        pidMotor.setMotorPower(power);
    }
}
