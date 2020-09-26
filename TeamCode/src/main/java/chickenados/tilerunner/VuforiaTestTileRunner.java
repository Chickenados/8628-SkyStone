package chickenados.tilerunner;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import chickenlib.util.CknEvent;
import chickenlib.CknTaskManager;
import chickenlib.location.CknPose;
import chickenlib.util.CknUtil;

@Autonomous(name = "TileRunner Test Vuforia", group = "TileRunner")
public class VuforiaTestTileRunner extends LinearOpMode{

        CknTaskManager mgr = new CknTaskManager();
        TileRunner robot;

        CknPose pose;

        @Override
        public void runOpMode(){

            robot = new TileRunner(hardwareMap, telemetry, true);

            waitForStart();

           robot.vuforiaVision.setEnabled(true);


            while(opModeIsActive()){
                CknUtil.CknLoopCounter.getInstance().loop++;
                CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

                pose = robot.getSkystonePose();

                if(pose != null) {
                    robot.dashboard.setLine(1, "X: " + pose.x + " Y: " + pose.y);
                    //robot.pidDrive.driveDistanceTank(-4, 0, 2, null);
                    //robot.pidDrive.driveDistanceTank(-4,0,2, );
                    robot.pidDrive.setTarget(-4,0,0);
                    
                } //else robot.pidDrive.driveDistanceTank(-4, 90, 2, null);
                else robot.pidDrive.setTarget(-4,0, 90);


                CknTaskManager.getInstance().executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
            }

            robot.vuforiaVision.setEnabled(false);

        }

    }


