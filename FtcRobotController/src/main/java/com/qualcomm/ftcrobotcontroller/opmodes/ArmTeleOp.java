package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class ArmTeleOp extends OpMode {

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;
  private Arm arm;

  public ArmTeleOp() { arm = new Arm(); }

  @Override
  public void init() {
    shoulderMotor = hardwareMap.dcMotor.get("shoulderMotor");
    elbowMotor = hardwareMap.dcMotor.get("elbowMotor");
    arm.init(shoulderMotor, elbowMotor);
  }

  @Override
  public void start() { arm.start(); }

  @Override
  public void loop() {
    if (gamepad1.a) {
      arm.home();
      return;
    }
    if (gamepad1.b) {
      arm.park();
      return;
    }
    if (gamepad1.y) {
      arm.unlimited();
      return;
    }
    double left  = -gamepad1.left_stick_y;
    double right = -gamepad1.right_stick_y;
    double Factor = 0.5 / 360.0;
    arm.moveShoulder(left * Factor);
    arm.moveElbow(right * Factor);
    telemetry.addData("Text", "*** Robot Data***");
    telemetry.addData("shoulder",
                      String.format("%.2f", arm.getShoulder() * 360));
    telemetry.addData("elbow", String.format("%.2f", arm.getElbow() * 360));
  }

  @Override
  public void stop() { arm.stop(); }

}