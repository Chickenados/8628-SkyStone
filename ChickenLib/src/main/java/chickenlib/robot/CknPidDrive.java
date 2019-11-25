package chickenlib.robot;

import chickenlib.logging.CknDbgTrace;
import chickenlib.opmode.CknRobot;
import chickenlib.opmode.CknTaskMgr;
import chickenlib.pid.CknPidController;
import chickenlib.util.CknEvent;
import chickenlib.util.CknPose2D;
import chickenlib.util.CknUtil;

public class CknPidDrive {

    private static final String moduleName = "CknPidDrive";
    protected static final CknDbgTrace globalTracer = CknDbgTrace.getGlobalTracer();
    protected static final boolean debugEnabled = false;
    private static final boolean tracingEnabled = false;
    private static final boolean useGlobalTracer = false;
    private static final CknDbgTrace.TraceLevel traceLevel = CknDbgTrace.TraceLevel.API;
    private static final CknDbgTrace.MsgLevel msgLevel = CknDbgTrace.MsgLevel.INFO;


    private CknPidController xPid, yPid, turnPid;
    private CknDriveBase driveBase;
    private CknTaskMgr.TaskObject pidDriveTaskObj;
    private CknTaskMgr.TaskObject stopTaskObj;

    private double stallTimeout = 0.0;
    private double expiredTimeout = 0.0;
    private double startTime = CknUtil.getCurrentTime();

    private CknEvent notifyEvent;

    public CknPidDrive(CknDriveBase driveBase, CknPidController xPid, CknPidController yPid, CknPidController turnPid){
        this.driveBase = driveBase;
        this.xPid = xPid;
        this.yPid = yPid;
        this.turnPid = turnPid;

        pidDriveTaskObj = CknTaskMgr.getInstance().createTask(moduleName + ".pidDriveTask", this::driveTask);
        stopTaskObj = CknTaskMgr.getInstance().createTask(moduleName + ".stopTask", this::stopTask);
    }

    public void setStallTimeout(double timeout){
        this.stallTimeout = timeout;
    }

    /**
     * Set the target of the PID to do a new movement.
     * @param xTarget
     * @param yTarget
     * @param turnTarget
     * @param event
     * @param timeout
     */
    public void setTarget(double xTarget, double yTarget, double turnTarget, CknEvent event, double timeout){

        if(xPid != null){
            xPid.setTarget(xTarget);
        }
        if(yPid != null){
            yPid.setTarget(yTarget);
        }
        if(turnPid != null){
            turnPid.setTarget(turnTarget);
        }

        if(event != null){
            event.clear();
        }
        this.notifyEvent = event;

        expiredTimeout = timeout;
        startTime = CknUtil.getCurrentTime();
        driveBase.resetStallTimer();

        //Start the PIDdrive task.
        setTaskEnabled(true);
    }

    public void setTarget(double yTarget, double turnTarget, CknEvent event, double timeout){
        setTarget(0.0, yTarget, turnTarget, event, timeout);
    }

    /**
     * Stop the PID movement.
     */
    public void stopPids(){

        setTaskEnabled(false);
        driveBase.stop();

        if(xPid != null){
            xPid.reset();
        }

        if(yPid != null){
            yPid.reset();
        }

        if(turnPid != null){
            turnPid.reset();
        }

    }

    public void setTaskEnabled(boolean enabled){

        if(enabled){
            pidDriveTaskObj.registerTask(CknTaskMgr.TaskType.OUTPUT_TASK);
            stopTaskObj.registerTask(CknTaskMgr.TaskType.STOP_TASK);
        }
        else
        {
            pidDriveTaskObj.unregisterTask(CknTaskMgr.TaskType.OUTPUT_TASK);
            stopTaskObj.unregisterTask(CknTaskMgr.TaskType.STOP_TASK);
        }

    }

    /**
     * Task to Handle PID movement
     * @param taskType
     * @param runMode
     */
    public void driveTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){

        //Retrieve power values from avaliable PIDs
        double xPower = xPid == null? 0.0: xPid.getOutput();
        double yPower = yPid == null? 0.0: yPid.getOutput();
        double turnPower = turnPid == null? 0.0: turnPid.getOutput();

        // Check if a motor is stalled or if the pid movement has timed out.
        boolean stalled = stallTimeout != 0.0 && driveBase.isStalled(stallTimeout);
        boolean expired = expiredTimeout != 0.0 && CknUtil.getCurrentTime() > startTime + expiredTimeout;

        //Check if any PIDs are on target
        boolean xOnTarget = xPid == null || xPid.isOnTarget();
        boolean yOnTarget = yPid == null || yPid.isOnTarget();
        boolean turnOnTarget = turnPid == null || turnPid.isOnTarget();
        boolean onTarget = (xOnTarget && yOnTarget && turnOnTarget);

        // Stop the PID movement if we are stalled, expired, or on target.
        if(stalled || expired || onTarget){
            stopPids();
            driveBase.stop();

            // Some autonomous programs use events to know when we are done driving.
            // If we have been given an event, set it to true.
            if(notifyEvent != null){
                notifyEvent.set(true);
                notifyEvent = null;
            }

        }

        if(driveBase.supportsHolonomicDrive() && xPid != null){
            driveBase.holonomicDrive(xPower, yPower, turnPower);
        }
        else
        {
            driveBase.tankDrive(yPower + turnPower, yPower - turnPower);
        }

    }

    public void stopTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){

        stopPids();

    }

}
