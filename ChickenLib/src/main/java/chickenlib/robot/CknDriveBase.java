package chickenlib.robot;

import chickenlib.logging.CknDbgTrace;

public class CknDriveBase {

    private static final String moduleName = "CknDriveBase";
    protected static final CknDbgTrace globalTracer = CknDbgTrace.getGlobalTracer();
    protected static final boolean debugEnabled = false;
    private static final boolean tracingEnabled = false;
    private static final boolean useGlobalTracer = false;
    private static final CknDbgTrace.TraceLevel traceLevel = CknDbgTrace.TraceLevel.API;
    private static final CknDbgTrace.MsgLevel msgLevel = CknDbgTrace.MsgLevel.INFO;
    
}
