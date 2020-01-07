package chickenlib.pid;

import com.qualcomm.robotcore.hardware.DcMotor;

import chickenlib.hardware.CknMotor;
import chickenlib.opmode.CknRobot;
import chickenlib.opmode.CknTaskMgr;
import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;

public class CknPidMotor {

    private static final String moduleName = "CknPidMotor";
    CknTaskMgr.TaskObject pidMotorTaskObj;
    CknTaskMgr.TaskObject stopTaskObj;

    CknMotor motor;
    CknPidController pid;

    private boolean active;
    private CknEvent notifyEvent;
    private double expiredTimeout = 0.0;
    private double startTime = CknUtil.getCurrentTime();
    private double stalledTimeout = 0.0;
    private boolean holdPosition = false;


    public CknPidMotor(CknMotor motor, CknPidController pid){
        this.motor = motor;
        this.pid = pid;

        pidMotorTaskObj = CknTaskMgr.getInstance().createTask(moduleName + ".pidMotorTask", this::pidMotorTask);
        stopTaskObj = CknTaskMgr.getInstance().createTask(moduleName + ".stopTask", this::stopTask);
    }

    public void setHoldPosition(boolean holdPosition){
        this.holdPosition = holdPosition;
    }

    /**
     * Sets the timeout for stall detection to cancel the PID movement.
     * @param timeout
     */
    public void setStalledTimeout(double timeout){
        this.stalledTimeout = timeout;
    }

    /**
     * Returns true if the motor controlled by the PID is stalled.
     */
    public boolean isMotorStalled(){
        return motor.isStalled(stalledTimeout);
    }

    public void setMotorMaxSpeed(double maxSpeed){
        pid.setOutputRange(-maxSpeed, maxSpeed);
    }

    /**
     * Allows manual control over the motor power.
     * Cancels all PID movements.
     * @param power
     */
    public void setMotorPower(double power){
        if(active) stopPid(false); // StopMotor is false because we want to use the motor during manual control.

        motor.setPower(power);
    }

    /**
     * Resets the motor position.
     * Can be done in either hardware or software, but hardware usually takes a long time so it should
     * only be done in init().
     * @param hardware
     */
    public void resetMotorPosition(boolean hardware){
        motor.resetOdometry(hardware);
    }

    public CknMotor getMotor(){
        return motor;
    }

    /**
     * Sets the target of the PID controlling the motor.
     * @param target the target for the PID.
     */
    public void setTarget(double target, CknEvent event, double timeout){

        if(event != null){
            event.clear();
        }
        notifyEvent = event;

        if(pid != null){
            pid.setTarget(target);
        }

        expiredTimeout = timeout;
        startTime = CknUtil.getCurrentTime();

        setTaskEnabled(true);
    }

    public boolean isActive(){
        return active;
    }

    public void setTaskEnabled(boolean enabled){

        if(enabled){
            pidMotorTaskObj.registerTask(CknTaskMgr.TaskType.OUTPUT_TASK);
            stopTaskObj.registerTask(CknTaskMgr.TaskType.STOP_TASK);
        } else {
            pidMotorTaskObj.unregisterTask(CknTaskMgr.TaskType.OUTPUT_TASK);
            stopTaskObj.unregisterTask(CknTaskMgr.TaskType.STOP_TASK);
        }
        active = enabled;
    }

    /**
     * Stops the pid action.
     */
    public void stopPid(boolean stopMotor){
        setTaskEnabled(false);

        if(stopMotor) {
            motor.setPower(0.0);
        }

        if(pid != null){
                pid.reset();
        }

    }

    public void pidMotorTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){

        // Get motor power from given PID.
        double motorPower = pid == null? 0.0: pid.getOutput();

        //Check if motor has stalled or expired.
        boolean stalled = stalledTimeout != 0.0 && isMotorStalled();
        boolean expired = expiredTimeout != 0.0 && CknUtil.getCurrentTime() > startTime + expiredTimeout;

        //Check if we are on target.
        boolean onTarget = pid == null || pid.isOnTarget();

        //If we have stalled, expired, or are on target, stop the PID movement.
        if(stalled || expired || onTarget){
            //Stop the motor
            if(holdPosition) {
                stopPid(true);
            }

            //Activate the event
            if(notifyEvent != null){
                notifyEvent.set(true);
                notifyEvent = null;
            }
        } else {
            motor.setPower(motorPower);
        }

    }

    public void stopTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){
        stopPid(true);
    }

}
