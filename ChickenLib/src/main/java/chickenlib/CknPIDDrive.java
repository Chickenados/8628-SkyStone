package chickenlib;

import chickenlib.util.CknEvent;
import chickenlib.util.CknUtil;
import chickenlib.util.CknWraparound;

public class CknPIDDrive implements CknTaskManager.Task{

    CknDriveBase driveBase;
    CknPIDController yPid;
    CknPIDController xPid;
    CknPIDController turnPid;
    CknEvent event;

    double startTime;
    double timeout;
    double targetScale = 1;

    public CknPIDDrive(CknDriveBase driveBase, CknPIDController yPid, CknPIDController turnPid){
        this.driveBase = driveBase;
        this.yPid = yPid;
        this.turnPid = turnPid;
    }

    public void setTargetScale(double scale){
        this.targetScale = scale;
    }

    public void driveStraightTankLoop(double distance, double heading, double timeout, CknEvent event){

        if(event != null) {
            event.reset();
        }

        double leftPower, rightPower;
        double startTime = CknUtil.getCurrentTime();

        //Convert the distance from inches to encoder ticks.
        double target = distance * targetScale;

        yPid.setSetPoint(target, true);
        //turnPid.setSetPoint(heading);

        while(!yPid.onTarget() && CknUtil.getCurrentTime() < timeout + startTime){

            //CknSmartDashboard.getInstance().setLine(5, "WHILE");


            leftPower = yPid.getOutput();
            rightPower = yPid.getOutput();

            driveBase.tankDrive(leftPower, rightPower);

        }

        if(event != null){
            event.set(true);
        }

    }

    public void driveDistanceTank(double distance, double heading, double timeout, CknEvent event){

        if(event != null) {
            event.reset();
            this.event = event;
        }

        this.timeout = timeout;

        startTime = CknUtil.getCurrentTime();

        //Convert the distance from inches to encoder ticks.
        double target = distance;

        yPid.setSetPoint(target, true);
        turnPid.setSetPoint(heading, false);

        setTaskEnabled(true);

    }

    public void driveStraightTankLoop(double distance, double heading){
        driveStraightTankLoop(distance, heading, 0, null);
    }

    public void setTarget(double target){
        yPid.setSetPoint(target, true);
    }

    public void setTarget(double xTarget, double yTarget, double turnTarget){

        if(xPid != null){
            xPid.setSetPoint(xTarget, true);
        }

        if(yPid != null){
            yPid.setSetPoint(yTarget, true);
        }

        if(turnPid != null){
            turnPid.setSetPoint(turnTarget, true);
        }
    }

    public void stop(){

        setTaskEnabled(false);

        if(turnPid != null){
            turnPid.reset();
        }
        if(yPid != null){
            yPid.reset();
        }
        if(xPid != null){
            xPid.reset();
        }

        driveBase.stopMotors();
    }

    public void setTaskEnabled(boolean enabled){
        if(enabled) {
            CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
        } else {
            CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
    
    @Override
    public void preContinuous(){

    }

    @Override
    public void postContinuous(){
        double leftPower, rightPower;

        leftPower = yPid.getOutput() - turnPid.getOutput();
        rightPower = yPid.getOutput() + turnPid.getOutput();

        driveBase.tankDrive(leftPower, rightPower);

        //Check if the robot has reached the target or timed out
        if((yPid.onTarget() && turnPid.onTarget()) || CknUtil.getCurrentTime() > startTime + timeout) {
            stop();
            if (event != null) {
                event.set(true);
            }
        }

    }

}
