package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;


public class TeleOp extends OpMode {

  private DcMotor rightWheel;
  private DcMotor leftWheel;

  private double leftPower  = 0.0;
  private double rightPower = 0.0;

  private void setLeftPower(double pwr) {
    leftPower = Range.clip(pwr,  -1, 1);
    leftWheel.setPower(leftPower);
  }

  private void setRightPower(double pwr) {
    rightPower = Range.clip(pwr,  -1, 1);
    rightWheel.setPower(rightPower);
  }

  private void setPower(double left, double right) {
    setLeftPower(left);
    setRightPower(right);
  }

  public TeleOp() { }

  @Override
  public void init() {
    rightWheel = hardwareMap.dcMotor.get("wheel_right");
    leftWheel  = hardwareMap.dcMotor.get("wheel_left");
    rightWheel.setDirection(DcMotor.Direction.REVERSE);
    leftWheel.setDirection(DcMotor.Direction.FORWARD);
  }

  @Override
  public void start() { setPower(0.0, 0.0); }

  @Override
  public void loop() {
    double left  = gamepad1.left_stick_y;
    double right = gamepad1.right_stick_y;
    setPower(left, right);
    telemetry.addData("Text", "*** Robot Data***");
    telemetry.addData("left pwr",  String.format("%.2f", leftPower));
    telemetry.addData("right pwr", String.format("%.2f", rightPower));
  }

  @Override
  public void stop() { setPower(0.0, 0.0); }

} // TeleOp
