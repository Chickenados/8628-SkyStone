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

    public void grab(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.FOUNDATION_HOOK_GRAB_POS, event, timeout);
    }

    public void grab(double timeout){
        grab(null, timeout);
    }

    public void release(CknEvent event, double timeout){
        pidMotor.setTarget(TilerunnerInfo.FOUNDATION_HOOK_RELEASE_POS, event, timeout);
    }

    public void release(double timeout){
        release(null, timeout);
    }
}
