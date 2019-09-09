package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@Disabled
@TeleOp(name = "Blank Cknlib TeleOp")
public class BlankChickenlibTeleop extends LinearOpMode {

    CknTaskManager mgr = new CknTaskManager();

    @Override
    public void runOpMode(){


        waitForStart();

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            // Put teleop code below this line





            // Put Teleop code above this line
            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }

}
