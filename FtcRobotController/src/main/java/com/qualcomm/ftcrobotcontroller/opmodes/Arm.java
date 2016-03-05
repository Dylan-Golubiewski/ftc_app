package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

public class Arm {
  private static double GearRatio = 12.0;

  public static double MinShoulder = -33.0 / 360; // angled back
  public static double MaxShoulder = +80.0 / 360; // almost straight forward

  public static double MinElbow = -35.0 / 360;  // slightly up
  public static double MaxElbow = +90.0 / 360;  // straight down

  private static double MinAngle = 26.0 / 360;
  private static double MaxAngle = 145.0 / 360;

  public static double ParkShoulder = MinShoulder;
  public static double ParkElbow = 16.0 / 360; // slightly down
  public static double HomeShoulder = 0.0 / 360; // slightly forward
  public static double HomeElbow = 0.0 / 360; // slightly down

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;

  private double shoulderPower = 0.0;
  private double elbowPower = 0.0;

  private double shoulderPosRev = 0.0;  // -0.125 (parked\
                                        //  0.0 (up)
					//  0.25 (forward)
  private double elbowPosRev = 0.0;     //  0.0 (level) to 0.25 (down)

  private static double speed = 0.3;
  private boolean noLimits = false;

  public double secondsPerRev() { return 0.5 / speed; }

  private void setElbowPower(double pwr) {
    elbowPower = Range.clip(pwr,  -1, 1);
    elbowMotor.setPower(elbowPower);
  }

  private void setShoulderPower(double pwr) {
    shoulderPower = Range.clip(pwr,  -1, 1);
    shoulderMotor.setPower(shoulderPower);
  }

  private void setPower(double shoulder, double elbow) {
    setShoulderPower(shoulder);
    setElbowPower(elbow);
  }

  private void setMotorMode(DcMotorController.RunMode mode) {
    shoulderMotor.setMode(mode);
    elbowMotor.setMode(mode);
  }

  private static int Clicks(double rev)
    { return (int)(rev * GearRatio * ConfigValues.ClicksPerRev + 0.5); }

  public double moveShoulder(double rev) {
    double pos = shoulderPosRev + rev;
    if (!noLimits) {
      pos = Range.clip(pos, MinShoulder, MaxShoulder);
      double angle = pos - elbowPosRev + 0.25;
      if (angle < MinAngle)
        pos = MinAngle + elbowPosRev - 0.25;
      else if (angle > MaxAngle)
        pos = MaxAngle + elbowPosRev - 0.25;
    }
    rev = pos - shoulderPosRev;
    shoulderPosRev = pos;
    int shoulder = Clicks(shoulderPosRev - ParkShoulder);
    shoulderMotor.setTargetPosition(shoulder);
    return rev * secondsPerRev();
  }

  public double moveElbow(double rev) {
    double pos = elbowPosRev + rev;
    if (!noLimits) {
      pos = Range.clip(pos, MinElbow, MaxElbow);
      double angle = shoulderPosRev - pos + 0.25;
      if (angle < MinAngle)
        pos = shoulderPosRev - MinAngle + 0.25;
      else if (angle > MaxAngle)
        pos = shoulderPosRev - MaxAngle + 0.25;
    }
    rev = pos - elbowPosRev;
    elbowPosRev = pos;
    int elbow = Clicks(elbowPosRev - ParkElbow);
    elbowMotor.setTargetPosition(elbow);
    return rev * secondsPerRev();
  }

  public double setShoulder(double rev)
    { return moveShoulder(rev - shoulderPosRev); }

  public double getShoulder() { return shoulderPosRev; }

  public double setElbow(double rev)
    { return moveElbow(rev - elbowPosRev); }

  public double getElbow() { return elbowPosRev; }

  public void unlimited() {
    noLimits = true;
    speed = 0.2;
  }

  public Arm() { }

  public void init(DcMotor shoulder, DcMotor elbow) {
    shoulderMotor = shoulder;
    elbowMotor = elbow;

    shoulderMotor.setDirection(DcMotor.Direction.REVERSE);
    elbowMotor.setDirection(DcMotor.Direction.REVERSE);

    setMotorMode(DcMotorController.RunMode.RESET_ENCODERS);
  }

  public void start() {
    setPower(speed, speed);
    setMotorMode(DcMotorController.RunMode.RUN_TO_POSITION);
    shoulderPosRev = ParkShoulder;
    elbowPosRev = ParkElbow;
  }

  public void park() {
    if (noLimits)
      return;
    setShoulder(ParkShoulder);
    setElbow(ParkElbow);
  }

  public void home() {
    if (noLimits)
      return;
    setShoulder(HomeShoulder);
    setElbow(HomeElbow); }

  public void stop() { setPower(0.0, 0.0); }

} // Arm
