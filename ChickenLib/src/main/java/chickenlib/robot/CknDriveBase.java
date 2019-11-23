package chickenlib.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import chickenlib.logging.CknDbgTrace;
import chickenlib.opmode.CknRobot;
import chickenlib.opmode.CknTaskMgr;
import chickenlib.sensor.CknGyro;
import chickenlib.util.CknPose2D;
import chickenlib.util.CknUtil;

public abstract class CknDriveBase {

    private static final String moduleName = "CknDriveBase";
    protected static final CknDbgTrace globalTracer = CknDbgTrace.getGlobalTracer();
    protected static final boolean debugEnabled = false;
    private static final boolean tracingEnabled = false;
    private static final boolean useGlobalTracer = false;
    private static final CknDbgTrace.TraceLevel traceLevel = CknDbgTrace.TraceLevel.API;
    private static final CknDbgTrace.MsgLevel msgLevel = CknDbgTrace.MsgLevel.INFO;

    protected CknDbgTrace dbgTrace = null;

    private CknTaskMgr.TaskObject odometryTaskObj;

    private CknPose2D odometry;
    private CknPose2D poseDelta;
    protected double xScale, yScale, rotScale = 1.0;

    private DcMotor[] motors;
    private CknGyro gyro;

    protected class MotorsState
    {
        public double prevTimestamp;
        public double currTimestamp;
        public double[] currPositions;
        public double[] prevPositions;
        public double[] stallStartTimes;
        public double[] motorPosDiffs;
    }   //class MotorsState

    private MotorsState motorsState;

    public CknDriveBase(DcMotor[] motors, CknGyro gyro){

        odometry = new CknPose2D();

        this.motors = motors;

        motorsState.currPositions = new double[motors.length];
        motorsState.prevPositions = new double[motors.length];
        motorsState.stallStartTimes = new double[motors.length];
        motorsState.motorPosDiffs = new double[motors.length];

        if(gyro != null){
            this.gyro = gyro;
        }

        CknTaskMgr taskMgr = CknTaskMgr.getInstance();
        odometryTaskObj = taskMgr.createTask(moduleName + ".odometryTask", this::odometryTask);

    }

    /**
     * Returns the number of registered motors.
     * @return
     */
    public int getNumMotors(){
        final String funcName = "getNumMotors";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, CknDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, CknDbgTrace.TraceLevel.API, "=%d", motors.length);
        }
        return motors.length;
    }

    public abstract boolean supportsHolonomicDrive();

    protected void holonomicDrive(double x, double y, double rotation, boolean inverted, double gyroAngle)
    {
        throw new UnsupportedOperationException("Holonomic drive is not supported by this drive base!");
    }   //holonomicDrive


    /**
     * Enables/Disables the odometry task.
     * @param enabled
     */
    public void setOdometryEnabled(boolean enabled) {
        final String funcName = "setOdometryEnabled";

        if (debugEnabled) {
            dbgTrace.traceEnter(funcName, CknDbgTrace.TraceLevel.API, "enabled=%s", enabled);
        }

        if (enabled) {
            resetOdometry();
            odometryTaskObj.registerTask(CknTaskMgr.TaskType.STANDALONE_TASK, CknTaskMgr.INPUT_THREAD_INTERVAL);
        } else {
            odometryTaskObj.unregisterTask(CknTaskMgr.TaskType.STANDALONE_TASK);
        }

        if (debugEnabled) {
            dbgTrace.traceExit(funcName, CknDbgTrace.TraceLevel.API);
        }
    }

    private void resetOdometry(){

    }

    protected abstract CknPose2D getPoseDelta(MotorsState motorsState);

    /**
     * The main task method that tracks the location of the robot on the field.
     * @param taskType
     * @param runMode
     */
    private void odometryTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){

        synchronized (odometry){

            //Update Timestamps
            motorsState.prevTimestamp = motorsState.currTimestamp;
            motorsState.currTimestamp = CknUtil.getCurrentTime();

            //Loop through each motor, retreive odometry information
            for(int i = 0; i < motors.length; i++){

                motorsState.prevPositions[i] = motorsState.currPositions[i];

                //Get current position from encoder
                motorsState.currPositions[i] = motors[i].getCurrentPosition();

                //Stall protection
                // A motor is deemed stalling if it's encoder position hasn't changed and the power is not zero.
                if (motorsState.currPositions[i] != motorsState.prevPositions[i] || motors[i].getPower() == 0.0)
                {
                    motorsState.stallStartTimes[i] = motorsState.currTimestamp;
                }

            }

            //Get change in pose
            poseDelta = getPoseDelta(motorsState);

            //If we have a gyro, use it to set heading values for the pose.
            //This is much more accurate than dead reckoning.
            if(gyro != null){
                odometry.heading = gyro.getZHeading();
            }

            // Transform delta x and delta y to a vector to calculate the actual change in x and y with heading.
            RealVector pos = MatrixUtils.createRealVector(new double[] { poseDelta.x, poseDelta.y });

            pos = CknUtil.rotateCW(pos, odometry.heading);

            //Update the odometry values.
            odometry.heading += poseDelta.heading;
            odometry.x += pos.getEntry(0);
            odometry.y += pos.getEntry(1);

        }

    }

    /**
     * Called when the opmode is about to stop
     * @param taskType
     * @param runMode
     */
    private void stopTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){
        //TODO: Stop the drivebase
    }
    
}
