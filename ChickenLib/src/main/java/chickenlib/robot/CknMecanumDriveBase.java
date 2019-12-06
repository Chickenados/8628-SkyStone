package chickenlib.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import chickenlib.logging.CknDbgTrace;
import chickenlib.sensor.CknGyro;
import chickenlib.util.CknPose2D;
import chickenlib.util.CknUtil;

public class CknMecanumDriveBase extends CknSimpleDriveBase {

    public enum MotorType{
        FRONT_LEFT(0),
        FRONT_RIGHT(1),
        BACK_LEFT(2),
        BACK_RIGHT(3);

        public final int value;

        MotorType(int value){
            this.value = value;
        }
    }

    public CknMecanumDriveBase(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight, CknGyro gyro){

        super(frontLeft, frontRight, backLeft, backRight, gyro);

    }

    @Override
    public boolean supportsHolonomicDrive(){
        return true;
    }

    @Override
    protected void holonomicDrive(double x, double y, double rotation, boolean inverted, double gyroAngle)
    {
        final String funcName = "holonomicDrive";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, CknDbgTrace.TraceLevel.API, "x=%f,y=%f,rot=%f,inverted=%s,angle=%f",
                    x, y, rotation, Boolean.toString(inverted), gyroAngle);
        }
        
            x = CknUtil.clipRange(x);
            y = CknUtil.clipRange(y);
            rotation = CknUtil.clipRange(rotation);

            if (inverted)
            {
                x = -x;
                y = -y;
            }

            double cosA = Math.cos(Math.toRadians(gyroAngle));
            double sinA = Math.sin(Math.toRadians(gyroAngle));
            double x1 = x*cosA - y*sinA;
            double y1 = x*sinA + y*cosA;

            //TODO: Try to get some kind of gyro assist going.
            /*if (isGyroAssistEnabled())
            {
                rotation += getGyroAssistPower(rotation);
            }*/

            double[] wheelPowers = new double[4];
            wheelPowers[MotorType.FRONT_LEFT.value] = x1 + y1 + rotation;
            wheelPowers[MotorType.FRONT_RIGHT.value] = -x1 + y1 - rotation;
            wheelPowers[MotorType.BACK_LEFT.value] = -x1 + y1 + rotation;
            wheelPowers[MotorType.BACK_RIGHT.value] = x1 + y1 - rotation;
            CknUtil.normalizeInPlace(wheelPowers);

            double wheelPower;

            wheelPower = wheelPowers[MotorType.FRONT_LEFT.value] * speed;
            frontLeft.setPower(Range.clip(wheelPower, -1, 1));

            wheelPower = wheelPowers[MotorType.FRONT_RIGHT.value] * speed;
            frontRight.setPower(Range.clip(wheelPower, -1, 1));

            wheelPower = wheelPowers[MotorType.BACK_LEFT.value] * speed;
            backLeft.setPower(Range.clip(wheelPower, -1, 1));

            wheelPower = wheelPowers[MotorType.BACK_RIGHT.value] * speed;
            backRight.setPower(Range.clip(wheelPower, -1, 1));


        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, CknDbgTrace.TraceLevel.API);
        }
    }   //holonomicDrive

    @Override
    public CknPose2D getPoseDelta(MotorsState motorsState){
        //
        // Call super class to get Y and turn data.
        //
        CknPose2D poseDelta = super.getPoseDelta(motorsState);

        poseDelta.x = xScale * CknUtil.average(
                motorsState.motorPosDiffs[MotorType.FRONT_LEFT.value],
                motorsState.motorPosDiffs[MotorType.FRONT_RIGHT.value],
                -motorsState.motorPosDiffs[MotorType.BACK_LEFT.value],
                -motorsState.motorPosDiffs[MotorType.BACK_RIGHT.value]);

        return poseDelta;
    }
}
