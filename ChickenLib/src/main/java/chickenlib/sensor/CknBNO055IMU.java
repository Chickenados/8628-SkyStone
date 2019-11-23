package chickenlib.sensor;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import chickenlib.opmode.CknRobot;
import chickenlib.opmode.CknTaskMgr;
import chickenlib.util.CknUtil;

public class CknBNO055IMU extends CknGyro {

    private class GyroData{
        double timestamp = 0.0;
        double xAngle = 0.0, yAngle = 0.0, zAngle = 0.0;
    }

    private String instanceName;
    private BNO055IMU imu;
    private GyroData gyroData = new GyroData();

    private CknTaskMgr.TaskObject gyroTaskObject;
    private boolean taskEnabled = false;

    public CknBNO055IMU(HardwareMap hwMap, String instanceName){
        this.instanceName = instanceName;
        imu = hwMap.get(BNO055IMU.class, instanceName);
        initialize();

        gyroTaskObject = CknTaskMgr.getInstance().createTask(instanceName, this::gyroTask);
    }

    /**
     * Enable the gyro task
     * @param enabled
     */
    public void setEnabled(boolean enabled){

        if(enabled){
            gyroTaskObject.registerTask(CknTaskMgr.TaskType.INPUT_TASK);
        } else {
            gyroTaskObject.unregisterTask(CknTaskMgr.TaskType.INPUT_TASK);
        }

        taskEnabled = enabled;
    }

    public boolean isEnabled(){
        return taskEnabled;
    }

    /**
     * Initialize the IMU sensor.
     */
    private void initialize(){
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        params.loggingEnabled = true;
        params.calibrationDataFile = "BNO055IMUCalibration.json";
        params.loggingTag = "IMU";
        params.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu.initialize(params);
    }

    private void gyroTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){
        Orientation orientation = null;
        double currTime = CknUtil.getCurrentTime();

        orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

        synchronized (gyroData){
            gyroData.timestamp = currTime;

            if(orientation != null){
                gyroData.xAngle = orientation.firstAngle;
                gyroData.yAngle = orientation.secondAngle;
                gyroData.zAngle = orientation.thirdAngle;
            }
        }
    }

    public double getXHeading(){
        synchronized (gyroData) {
            return gyroData.xAngle;
        }
    }

    public double getYHeading(){
        synchronized (gyroData) {
            return gyroData.yAngle;
        }
    }

    public double getZHeading(){
        synchronized (gyroData) {
            return gyroData.zAngle;
        }
    }
}

