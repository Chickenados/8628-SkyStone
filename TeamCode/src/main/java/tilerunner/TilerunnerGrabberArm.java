package tilerunner;

import chickenlib.pid.CknPidMotor;
import chickenlib.util.CknEvent;

public class     TilerunnerGrabberArm {

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

    //extend
    public void extend(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_EXTENDED_ENCODER_COUNT, event, timeout);
    }

    public void extend(double timeout){
        extend(null, timeout);
    }

    //retract
    public void retract(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_RETRACTED_ENCODER_COUNT, event, timeout);
    }

    public void retract(double timeout){
        retract(null, timeout);
    }


    //low position
    public void lowPosition(double timeout){
        lowPosition(null, timeout);
    }

    public void lowPosition(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_AUTO_HEIGHT_ENCODER_COUNT, event, timeout);
    }

    //straight up
    public void straightUp(double timeout){
        straightUp(null, timeout);
    }

    public void straightUp(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_STRAIGHT_UP_ENCODER_COUNT, event, timeout);
    }

    //second position
    public void secondPosition(double timeout){
        secondPosition(null, timeout);
    }

    public void secondPosition(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.GRABBER_SECOND_POSITION_ENCODER_COUNT, event, timeout);
    }


    //go to position
    public void goToPosition(double target, CknEvent event, double timeout){
        pidMotor.setTarget(target, event, timeout);
    }

    public void manualControl(double power){
        pidMotor.setMotorPower(power);
    }
}