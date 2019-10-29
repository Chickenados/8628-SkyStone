package chickenlib.vision;

import com.vuforia.CameraDevice;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.HashMap;

public class CknVuforia {

    public static class TargetInfo {
        public final int index;
        public final String name;
        public final boolean isObjectTarget;
        public final OpenGLMatrix fieldLocation;
        public VuforiaTrackables targetList = null;

        public TargetInfo(int index, String name, boolean isObjectTarget, OpenGLMatrix fieldLocation){
            this.index = index;
            this.name = name;
            this.isObjectTarget = isObjectTarget;
            this. fieldLocation = fieldLocation;
        }

        private void setTargetList(VuforiaTrackables targetList)
        {
            this.targetList = targetList;
        }
    }

    private VuforiaLocalizer.CameraDirection cameraDirection;
    private VuforiaLocalizer localizer;
    private boolean usingWebcam = false;
    private ArrayList<VuforiaTrackables> targetLists = new ArrayList<>();
    private HashMap<String, TargetInfo> targetMap = new HashMap<>();
    private int numImageTargets = 0;
    private int numObjectTargets = 0;

    /*
    * Constructor for using Vuforia with built-in phone camera
     */
    public CknVuforia(String licenseKey, int cameraMonitorViewID, VuforiaLocalizer.CameraDirection cameraDirection){

        this.cameraDirection = cameraDirection;

        // If ID is -1, don't active monitor view
        VuforiaLocalizer.Parameters params =
                cameraMonitorViewID == -1? new VuforiaLocalizer.Parameters(): new VuforiaLocalizer.Parameters(cameraMonitorViewID);

        params.vuforiaLicenseKey = licenseKey;
        params.cameraDirection = cameraDirection;
        localizer = ClassFactory.getInstance().createVuforia(params);
    }

    /*
    * Constructor for using Vuforia with webcam
     */

    public CknVuforia(String licenseKey, int cameraMonitorViewID, CameraName webcameName, VuforiaLocalizer.CameraDirection cameraDirection){
        usingWebcam = true;
        this.cameraDirection = cameraDirection;

        VuforiaLocalizer.Parameters params =
                cameraMonitorViewID == -1? new VuforiaLocalizer.Parameters(): new VuforiaLocalizer.Parameters(cameraMonitorViewID);
        params.vuforiaLicenseKey = licenseKey;
        params.cameraName = webcameName;

        localizer = ClassFactory.getInstance().createVuforia(params);
    }

    public VuforiaLocalizer getLocalizer() {
        return localizer;
    }

    /*
    Load a target for the tracking list
     */

    public void addTargetList(String fileName, TargetInfo targets[], OpenGLMatrix phoneLocation){

        VuforiaTrackables targetList = localizer.loadTrackablesFromAsset(fileName);

        targetLists.add(targetList);
        for(TargetInfo targetInfo : targets){

            VuforiaTrackable target = targetList.get(targetInfo.index);

            target.setName(targetInfo.name);
            targetInfo.setTargetList(targetList);

            if(targetInfo.fieldLocation != null){
                target.setLocation(targetInfo.fieldLocation);
            }

            if(phoneLocation != null){
                ((VuforiaTrackableDefaultListener) target.getListener()).setPhoneInformation(phoneLocation, cameraDirection);
            }

            targetMap.put(targetInfo.name, targetInfo);
            if (targetInfo.isObjectTarget)
            {
                numObjectTargets++;
            }
            else
            {
                numImageTargets++;
            }

        }

        // Set max amount of targets that can be tracked to number of targets
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, numImageTargets);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_OBJECT_TARGETS, numObjectTargets);
    }


    public void setTrackingEnabled(boolean trackingEnabled){

        for(VuforiaTrackables targetList : targetLists){

            if(trackingEnabled){
                targetList.activate();
            } else {
                targetList.deactivate();
            }

        }

    }

    /**
     * This method creates a location matrix that can be used to relocate an object to its final location by rotating
     * and translating the object from the origin of the field. It is doing the operation in the order of the
     * parameters. In other words, it will first rotate the object on the X-axis, then rotate on the Y-axis, then
     * rotate on the Z-axis, then translate on the X-axis, then translate on the Y-axis and finally translate on the
     * Z-axis.
     *
     * @param rotateX specifies rotation on the X-axis.
     * @param rotateY specifies rotation on the Y-axis.
     * @param rotateZ specifies rotation on the Z-axis.
     * @param translateX specifies translation on the X-axis.
     * @param translateY specifies translation on the Y-axis.
     * @param translateZ specifies translation on the Z-axis.
     * @return returns the location matrix.
     */
    public OpenGLMatrix locationMatrix(
            float rotateX, float rotateY, float rotateZ, float translateX, float translateY, float translateZ)
    {
        return OpenGLMatrix.translation(translateX, translateY, translateZ)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, rotateX, rotateY, rotateZ));
    }

    public VuforiaTrackable getTarget(String targetName){
        VuforiaTrackable target = null;
        TargetInfo targetInfo = targetMap.get(targetName);

        if(targetInfo != null){
            target = targetInfo.targetList.get(targetInfo.index);
        }

        return target;
    }

    /*
    Return true if the camera recognizes the target
     */
    public boolean isTargetVisible(VuforiaTrackable target){
        VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener)target.getListener();
        return listener.isVisible();
    }

    /**
     * Returns the position matrix of the specified target.
     *
     * @param target specifies the target to get the position matrix.
     * @return position matrix of the specified target.
     */
    public OpenGLMatrix getTargetPose(VuforiaTrackable target)
    {
        VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener)target.getListener();
        return listener.getPose();
    }

    /**
     * This method determines the robot location by the given target.
     *
     * @param target specifies the target to be used to determine robot location.
     * @return robot location matrix.
     */
    public OpenGLMatrix getRobotLocation(VuforiaTrackable target)
    {
        VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener)target.getListener();
        return listener.getRobotLocation();
    }

    /**
     * This methods turns the phone flash light ON or OFF.
     *
     * @param enabled specifies true to turn flashlight on, false to turn off.
     */
    public void setFlashlightEnabled(boolean enabled)
    {
        CameraDevice.getInstance().setFlashTorchMode(enabled);
    }

}
