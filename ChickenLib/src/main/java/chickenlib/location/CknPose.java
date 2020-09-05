package chickenlib.location;

public class CknPose {
    public double x;
    public double y;
    public double heading;
    public double xVel;
    public double yVel;
    public double turnRate;

    public CknPose(double x, double y, double heading, double xVel, double yVel, double turnRate)
    {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.xVel = xVel;
        this.yVel = yVel;
        this.turnRate = turnRate;
    }

    public CknPose(double x, double y, double heading)
    {
        this(x, y, heading, 0.0, 0.0, 0.0);
    }

    public CknPose(double x, double y)
    {
        this(x, y, 0.0, 0.0, 0.0, 0.0);
    }

    public CknPose poseDistance(CknPose pose)
    {
        return new CknPose(x - pose.x, y - pose.y, heading, xVel, yVel, turnRate);
    }


}
