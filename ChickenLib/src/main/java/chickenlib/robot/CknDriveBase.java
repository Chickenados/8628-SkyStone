package chickenlib.robot;

import chickenlib.logging.CknDbgTrace;
import chickenlib.opmode.CknRobot;
import chickenlib.opmode.CknTaskMgr;

public class CknDriveBase {

    private static final String moduleName = "CknDriveBase";
    protected static final CknDbgTrace globalTracer = CknDbgTrace.getGlobalTracer();
    protected static final boolean debugEnabled = false;
    private static final boolean tracingEnabled = false;
    private static final boolean useGlobalTracer = false;
    private static final CknDbgTrace.TraceLevel traceLevel = CknDbgTrace.TraceLevel.API;
    private static final CknDbgTrace.MsgLevel msgLevel = CknDbgTrace.MsgLevel.INFO;

    protected CknDbgTrace dbgTrace = null;

    private CknTaskMgr.TaskObject odometryTaskObj;

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

    private void odometryTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){

    }
    
}
