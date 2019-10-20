package chickenados.skybot;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;

import chickenlib.vision.CknVuforia;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

public class VuforiaVision {

    private CknVuforia vuforia;
    private VuforiaTrackable[] imageTargets;
    private OpenGLMatrix lastRobotLocation = null;

    // Height of the center of the target image above the floor.
    private static final float mmTargetHeight = 6.0f * CknSkyBotInfo.mmPerInch;

    // Constant for Stone Target.
    private static final float stoneZ = 2.0f * CknSkyBotInfo.mmPerInch;

    // Constants for the center support targets.
    private static final float bridgeZ = 6.42f * CknSkyBotInfo.mmPerInch;
    private static final float bridgeY = 23.0f * CknSkyBotInfo.mmPerInch;
    private static final float bridgeX = 5.18f * CknSkyBotInfo.mmPerInch;
    private static final float bridgeRotY = 59.0f;  // Units are degrees
    private static final float bridgeRotZ = 180.0f;

    // Constants for perimeter targets
    private static final float halfField = 72.0f * CknSkyBotInfo.mmPerInch;
    private static final float quadField = 36.0f * CknSkyBotInfo.mmPerInch;

    public VuforiaVision(CknVuforia vuforia, OpenGLMatrix phoneLocation){
        this.vuforia = vuforia;

        // Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
        // Rotated it to to face forward, and raised it to sit on the ground correctly.
        // This can be used for generic target-centric approach algorithms
        OpenGLMatrix stoneTargetLocation = OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));

        //Set the position of the bridge support targets with relation to origin (center of field)
        OpenGLMatrix blueFrontBridgeLocation = OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, bridgeRotZ));

        OpenGLMatrix blueRearBridgeLocation = OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, bridgeRotZ));

        OpenGLMatrix redFrontBridgeLocation = OpenGLMatrix
                .translation(-bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, 0));

        OpenGLMatrix redRearBridgeLocation = OpenGLMatrix
                .translation(bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, 0));

        //Set the position of the perimeter targets with relation to origin (center of field)
        OpenGLMatrix redPerimeter1Location = OpenGLMatrix
                .translation(quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));

        OpenGLMatrix redPerimeter2Location = OpenGLMatrix
                .translation(-quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));

        OpenGLMatrix frontPerimeter1Location = OpenGLMatrix
                .translation(-halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90));

        OpenGLMatrix frontPerimeter2Location = OpenGLMatrix
                .translation(-halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90));

        OpenGLMatrix bluePerimeter1Location = OpenGLMatrix
                .translation(-quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));

        OpenGLMatrix bluePerimeter2Location = OpenGLMatrix
                .translation(quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));

        OpenGLMatrix rearPerimeter1Location = OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90));

        OpenGLMatrix rearPerimeter2Location = OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));

        CknVuforia.TargetInfo[] imageTargetsInfo =
                {
                        new CknVuforia.TargetInfo(0, "Stone Target", false, stoneTargetLocation),
                        new CknVuforia.TargetInfo(1, "Blue Rear Bridge", false, blueRearBridgeLocation),
                        new CknVuforia.TargetInfo(2, "Red Rear Bridge", false, redRearBridgeLocation),
                        new CknVuforia.TargetInfo(3, "Red Front Bridge", false, redFrontBridgeLocation),
                        new CknVuforia.TargetInfo(4, "Blue Front Bridge", false, blueFrontBridgeLocation),
                        new CknVuforia.TargetInfo(5, "Red Perimeter 1", false, redPerimeter1Location),
                        new CknVuforia.TargetInfo(6, "Red Perimeter 2", false, redPerimeter2Location),
                        new CknVuforia.TargetInfo(7, "Front Perimeter 1", false, frontPerimeter1Location),
                        new CknVuforia.TargetInfo(8, "Front Perimeter 2", false, frontPerimeter2Location),
                        new CknVuforia.TargetInfo(9, "Blue Perimeter 1", false, bluePerimeter1Location),
                        new CknVuforia.TargetInfo(10, "Blue Perimeter 2", false, bluePerimeter2Location),
                        new CknVuforia.TargetInfo(11, "Rear Perimeter 1", false, rearPerimeter1Location),
                        new CknVuforia.TargetInfo(12, "Rear Perimeter 2", false, rearPerimeter2Location)
                };

        vuforia.addTargetList(CknSkyBotInfo.TRACKABLES_FILE_NAME, imageTargetsInfo, phoneLocation);
        imageTargets = new VuforiaTrackable[imageTargetsInfo.length];
        for (int i = 0; i < imageTargets.length; i++)
        {
            imageTargets[i] = vuforia.getTarget(imageTargetsInfo[i].name);
        }
    }

    public void setEnabled(boolean enabled, boolean useFlashLight)
    {
        if (useFlashLight)
        {
            vuforia.setFlashlightEnabled(enabled);
        }
        vuforia.setTrackingEnabled(enabled);
    }

    public OpenGLMatrix getRobotLocation()
    {
        OpenGLMatrix robotLocation = null;
        boolean targetVisible = false;

        for (VuforiaTrackable target: imageTargets)
        {
            if (vuforia.isTargetVisible(target))
            {
                targetVisible = true;
                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix location = vuforia.getRobotLocation(target);
                if (location != null)
                {
                    lastRobotLocation = location;
                }
                break;
            }
        }

        if (targetVisible)
        {
            robotLocation = lastRobotLocation;
        }

        return robotLocation;
    }

    public VectorF getLocationTranslation(OpenGLMatrix location)
    {
        return location.getTranslation();
    }

    public Orientation getLocationOrientation(OpenGLMatrix location)
    {
        return Orientation.getOrientation(location, EXTRINSIC, XYZ, DEGREES);
    }
}
