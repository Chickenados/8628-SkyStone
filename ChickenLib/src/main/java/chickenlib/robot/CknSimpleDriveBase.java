package chickenlib.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import chickenlib.logging.CknDbgTrace;
import chickenlib.sensor.CknGyro;
import chickenlib.util.CknPose2D;

public class CknSimpleDriveBase extends CknDriveBase {

    protected final DcMotor frontLeft;
    protected final DcMotor frontRight;
    protected final DcMotor backLeft;
    protected final DcMotor backRight;

    public CknSimpleDriveBase(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight, CknGyro gyro){

        super(new DcMotor[] {frontLeft, frontRight, backLeft, backRight}, gyro);

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

    }

    @Override
    public boolean supportsHolonomicDrive(){
        return false;
    }

    public void tankDrive(double leftPower, double rightPower){
        final String funcName = "tankDrive";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, CknDbgTrace.TraceLevel.API,
                    "leftPower=%f,rightPower=%f", leftPower, rightPower);
        }


        // Check if each motor exists, and set power.
        if(frontLeft != null){
            frontLeft.setPower(Range.clip(leftPower * speed, -1, 1));
        }

        if(frontRight != null){
            frontRight.setPower(Range.clip(leftPower * speed, -1, 1));
        }

        if(backLeft != null){
            backLeft.setPower(Range.clip(leftPower * speed, -1, 1));
        }

        if(backRight != null){
            backRight.setPower(Range.clip(leftPower * speed, -1, 1));
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, CknDbgTrace.TraceLevel.API);
        }
    }

    /**
     * Get the pose delta for a simple drive train.
     * @param motorsState
     * @return
     */
    @Override
    protected CknPose2D getPoseDelta(MotorsState motorsState)
    {
        final String funcName = "getPoseDelta";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, CknDbgTrace.TraceLevel.TASK);
        }

        CknPose2D poseDelta = new CknPose2D();

        // Calculate heading and turn rate using positional info in case we don't have a gyro.
        // Get the average of all left and right motors separately, since this drivebase may have between 2-4 motors
        double lPos = 0.0, rPos = 0.0;

        for (int i = 0; i < motorsState.motorPosDiffs.length; i++)
        {
            double posDiff = motorsState.motorPosDiffs[i];

            if (i % 2 == 0)
            {
                lPos += posDiff;
            }
            else
            {
                rPos += posDiff;
            }
        }

        double motorsPerSide = getNumMotors() / 2.0;
        lPos /= motorsPerSide;
        rPos /= motorsPerSide;

        poseDelta.x = 0;
        poseDelta.y = (lPos + rPos)/2 * yScale;

        poseDelta.xVel = 0;

        poseDelta.heading = Math.toDegrees((lPos - rPos) * rotScale);

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, CknDbgTrace.TraceLevel.TASK);
        }

        return poseDelta;
    }   //getPoseDelta
}
