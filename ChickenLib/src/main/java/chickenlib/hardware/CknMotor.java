package chickenlib.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import chickenlib.opmode.CknOpMode;
import chickenlib.opmode.CknRobot;
import chickenlib.opmode.CknTaskMgr;
import chickenlib.util.CknUtil;

public class CknMotor {

    private String instanceName;
    private DcMotor motor;
    private boolean odometryEnabled = false;
    private CknTaskMgr.TaskObject motorTaskObj;

    private int motorPosition;
    private int motorPositionOffset = 0;
    private double stalledStartTime;

    public CknMotor(String instanceName){
        this.instanceName = instanceName;
        this.motor = CknOpMode.getInstance().hardwareMap.dcMotor.get(instanceName);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        motorTaskObj = CknTaskMgr.getInstance().createTask(instanceName + ".motorTaskObj", this::motorOdometryTask);
    }

    public void enableMotorOdometry(boolean enabled){
        odometryEnabled = enabled;

        if(enabled){
            motorTaskObj.registerTask(CknTaskMgr.TaskType.INPUT_TASK);
        } else {
            motorTaskObj.unregisterTask(CknTaskMgr.TaskType.INPUT_TASK);
        }
    }

    /**
     * Resets the motor's encoder odometry either hardware or software wise.
     * Hardware should be done in init() since it takes longer,
     * otherwise use software.
     * @param hardware
     */
    public void resetOdometry(boolean hardware){
        if(hardware){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else {
            motorPositionOffset = motorPosition;
            motorPosition = 0;
        }
    }

    /**
     * Sets the power of the DC motor
     * @param power
     */
    public void setPower(double power){
        motor.setPower(power);
    }

    /**
     * Retreives the power of the DC motor
     * @return
     */
    public double getPower(){
        return motor.getPower();
    }

    public int getPosition(){
        return motorPosition;
    }

    public boolean isStalled(double stalledTimeout){
        return CknUtil.getCurrentTime() - stalledStartTime > stalledTimeout;
    }

    public void motorOdometryTask(CknTaskMgr.TaskType taskType, CknRobot.RunMode runMode){
        double prevMotorPosition = motorPosition;
        motorPosition = motor.getCurrentPosition() - motorPositionOffset;

        // Check if a motor is stalling. A motor is deemed stalling if it has a non-zero power and no encoder change.
        if(motorPosition != prevMotorPosition || motor.getPower() == 0.0){
            stalledStartTime = CknUtil.getCurrentTime();
        }
    }

}
