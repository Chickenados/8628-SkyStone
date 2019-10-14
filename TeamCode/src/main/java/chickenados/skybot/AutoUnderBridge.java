package chickenados.skybot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import chickenlib.CknTaskManager;

@Autonomous(name = "Test Auto Under Bridge")
public abstract class AutoUnderBridge extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();
    CknSkyBot robot;

    enum State {
        AWAY_FROM_WALL,
        AUTO_SLIDE_TO_STONE,
        UNDER_BRIDGE,
        END;
    }
}