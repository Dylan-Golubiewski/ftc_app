package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class TeleOpWithArm extends OpMode {

  private DcMotor motorLeftFront;
  private DcMotor motorRightFront;
  private DcMotor motorLeftBack;
  private DcMotor motorRightBack;
  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;
  private DcMotor turretMotor;
  private Servo  clawServo;

  private Wheels wheels;
  private Arm arm;

  public TeleOpWithArm() {
    wheels = new Wheels();
    arm = new Arm();
  }

  @Override
  public void init() {
    motorLeftFront  = hardwareMap.dcMotor.get("FrontLeftDrive");
    motorLeftBack   = hardwareMap.dcMotor.get("BackLeftDrive");
    motorRightFront = hardwareMap.dcMotor.get("FrontRightDrive");
    motorRightBack  = hardwareMap.dcMotor.get("BackRightDrive");
    shoulderMotor   = hardwareMap.dcMotor.get("shoulderMotor");
    elbowMotor      = hardwareMap.dcMotor.get("elbowMotor");
    turretMotor     = hardwareMap.dcMotor.get("turretMotor");
    clawServo       = hardwareMap.servo.get("ClawServo");

    wheels.init(motorLeftFront, motorRightFront, motorLeftBack, motorRightBack);
    arm.init(shoulderMotor, elbowMotor, turretMotor, clawServo);
  }


  @Override
  public void start() {
    wheels.start();
    arm.start();
  }

  @Override
  public void loop() {
    // Gamepad sticks are inverted.
    wheels.move(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

    if (gamepad2.a) {
      arm.home();
      return;
    }
    if (gamepad2.b) {
      arm.park();
      return;
    }
    if (gamepad1.y && gamepad2.y) {
      arm.unlimited();
      return;
    }

    double left  = -gamepad2.left_stick_y;
    double right = -gamepad2.right_stick_y;
    double spin  = gamepad2.left_trigger - gamepad2.right_trigger;
    double Factor = 0.5 / 360.0;

    arm.moveShoulder(left * Factor);
    arm.moveElbow(right * Factor);
    arm.moveTurret(spin * Factor);

    boolean openClaw  = gamepad2.left_bumper;
    boolean closeClaw = gamepad2.right_bumper;
    if (openClaw != closeClaw) {
      if (openClaw)
        arm.openClaw();
      else
	arm.closeClaw();
    }

    telemetry.addData("Text", "*** Robot Data***");
    telemetry.addData("shoulder", String.format("%.2f", arm.getShoulder() * 360));
    telemetry.addData("elbow", String.format("%.2f", arm.getElbow() * 360));
    telemetry.addData("turret", String.format("%.2f", arm.getTurret() * 360));
  }

  @Override
  public void stop() {
    arm.stop();
    wheels.stop();
  }

}
