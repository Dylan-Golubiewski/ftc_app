package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

public class EncoderWheels {

  private static double Speed = 0.4;
  private static double InnerSpeed = Speed * ConfigValues.InsideRatio;

  public static double SecondsPerFoot = 0.4 / Speed;

  private DcMotor frontLeftWheel;
  private DcMotor frontRightWheel;
  private DcMotor rearLeftWheel;
  private DcMotor rearRightWheel;

  private double leftPower  = 0.0;
  private double rightPower = 0.0;

  private double leftPosFt  = 0.0;
  private double rightPosFt = 0.0;

  private void setLeftPower(double pwr) {
    leftPower = Range.clip(pwr,  -1, 1);
    frontLeftWheel.setPower(leftPower);
    rearLeftWheel.setPower(leftPower);
  }

  private void setRightPower(double pwr) {
    rightPower = Range.clip(pwr,  -1, 1);
    frontRightWheel.setPower(rightPower);
    rearRightWheel.setPower(rightPower);
  }

  private void setPower(double left, double right) {
    setLeftPower(left);
    setRightPower(right);
  }

  private void setWheelMode(DcMotorController.RunMode mode) {
    frontRightWheel.setMode(mode);
    frontLeftWheel.setMode(mode);
    rearRightWheel.setMode(mode);
    rearLeftWheel.setMode(mode);
  }

  private static int Clicks(double ft) {
    return (int)
        (ft / ConfigValues.FeetPerWheelRev * ConfigValues.ClicksPerRev + 0.5);
  }

  public void setTarget(double leftFt, double rightFt) {
    int left  = Clicks(leftFt);
    int right = Clicks(rightFt);
    frontLeftWheel.setTargetPosition(left);
    frontRightWheel.setTargetPosition(right);
    rearLeftWheel.setTargetPosition(left);
    rearRightWheel.setTargetPosition(right);
  }

  public void move(double ft) {
    leftPosFt  += ft;
    rightPosFt += ft;
    setPower(Speed, Speed);
    setTarget(leftPosFt, rightPosFt);
  }

  public void turnLeft(double ft) {
    leftPosFt  += ft * ConfigValues.InsideRatio;
    rightPosFt += ft;
    setPower(InnerSpeed, Speed);
    setTarget(leftPosFt, rightPosFt);
  }

  public void turnRight(double ft) {
    leftPosFt  += ft;
    rightPosFt += ft * ConfigValues.InsideRatio;
    setPower(Speed, InnerSpeed);
    setTarget(leftPosFt, rightPosFt);
  }

  public void spin(double rev) {
    double d = rev * ConfigValues.FeetPerSpin;
    leftPosFt  += d;
    rightPosFt -= d;
    setPower(Speed, Speed);
    setTarget(leftPosFt, rightPosFt);
  }

  public EncoderWheels() { }

  public void init(DcMotor frontLeft, DcMotor frontRight,
                   DcMotor rearLeft, DcMotor rearRight)
  {
    frontLeftWheel  = frontLeft;
    frontRightWheel = frontRight;
    rearLeftWheel   = rearLeft;
    rearRightWheel  = rearRight;

    frontLeftWheel.setDirection(DcMotor.Direction.FORWARD);
    rearLeftWheel.setDirection(DcMotor.Direction.FORWARD);
    frontRightWheel.setDirection(DcMotor.Direction.REVERSE);
    rearRightWheel.setDirection(DcMotor.Direction.REVERSE);

    setWheelMode(DcMotorController.RunMode.RESET_ENCODERS);
    setPower(0.0, 0.0);
  }

  public void start() {
    setPower(0.0, 0.0);
    setWheelMode(DcMotorController.RunMode.RUN_TO_POSITION);
    leftPosFt  = 0.0;
    rightPosFt = 0.0;
  }

  public void stop() { setPower(0.0, 0.0); }

} // EncoderWheels
