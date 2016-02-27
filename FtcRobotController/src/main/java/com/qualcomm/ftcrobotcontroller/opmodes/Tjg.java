package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class Tjg extends OpMode {

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;
  private Arm arm;

  public Tjg() { arm = new Arm(); }

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

} // Tjg
/*
  public double moveShoulder(double rev);
  public double moveElbow(double rev);
  public double setShoulder(double rev);
  public double getShoulder();
  public double setElbow(double rev);
  public double getElbow();

  public void init(DcMotor shoulder, DcMotor elbow);

  public void start();
  public void park();
  public void home();
  public void stop();
*/
