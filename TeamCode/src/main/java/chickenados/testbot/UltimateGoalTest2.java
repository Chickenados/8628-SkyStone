package chickenados.testbot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp
public class UltimateGoalTest2 extends OpMode {

    @Override
    public void init() {
        String name = "Claire";
        telemetry.addData("Hello", name );
    }
    @Override
    public void loop() {

    }

}
