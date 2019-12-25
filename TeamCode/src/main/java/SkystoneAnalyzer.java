package chickenados.tilerunner;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;


import java.util.List;

import tilerunner.TilerunnerInfo;

public class SkystoneAnalyzer{

    float skystoneLeft;

    public enum SkystoneState{
        LEFT,
        CENTER,
        RIGHT,
        UNKNOWN;
    }

    public SkystoneState analyzeTFOD(List<Recognition> objects)  {
        if (objects != null) {

            for (Recognition rec : objects) {
                if (rec.getLabel() == TilerunnerInfo.LABEL_SECOND_ELEMENT){
                    skystoneLeft = rec.getLeft();

                    //for position comparison, I'm assuming position is measured in pixels, on a 1920x1080 image.
                    if(skystoneLeft > 0 && skystoneLeft < 640){
                        return SkystoneState.LEFT;
                    } else if (skystoneLeft >= 640 && skystoneLeft <= 1280){
                        return SkystoneState.CENTER;
                    } else if (skystoneLeft > 1280 && skystoneLeft < 1920){
                        return SkystoneState.RIGHT;
                    } else {
                        return SkystoneState.UNKNOWN;
                    }
                }
            }
        }

        return SkystoneState.UNKNOWN;
    }
}