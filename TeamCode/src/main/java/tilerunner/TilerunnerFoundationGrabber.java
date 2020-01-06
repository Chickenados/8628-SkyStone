package tilerunner;

import chickenlib.pid.CknPidMotor;
import chickenlib.util.CknEvent;

public class TilerunnerFoundationGrabber {

    CknPidMotor pidMotor;

    public TilerunnerFoundationGrabber(CknPidMotor pidMotor){
        this.pidMotor = pidMotor;
    }

    public void setHoldPosition(boolean hold){
        pidMotor.setHoldPosition(hold);
    }

    public boolean isPidActive(){
        return pidMotor.isActive();
    }

    public void grab(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.FOUNDATION_DOWN_POSITION, event, timeout);
    }

    public void grab(double timeout){
        grab(null, timeout);
    }

    public void release(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.FOUNDATION_UP_POSITION, event, timeout);
    }

    public void release(double timeout){
        release(null, timeout);
    }

    public void manualControl(double power){
        pidMotor.setMotorPower(power);
    }

    //telling foudndation grabber to stay
    public void stopPid(){
        pidMotor.stopPid(true);
    }
}
