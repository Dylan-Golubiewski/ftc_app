package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

public class Arm {
  private static double Speed = 0.4;

  public static double SecondsPerRev = 0.5 / Speed;

  public static double ParkElbow = 20.0 / 360;
  public static double ParkShoulder = -40.0 / 360;
  public static double HomeElbow = 40.0 / 360;
  public static double HomeShoulder = 0.0 / 360;
  public static double MaxElbow = 90.0 / 360;
  public static double MinElbow = -20.0 / 360;

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;

  private double shoulderPower = 0.0;
  private double elbowPower = 0.0;

  private double shoulderPosRev = 0.0;  // -0.125 (parked)
                                        //  0.0 (up)
					//  0.25 (forward)
  private double elbowPosRev = 0.0;     // 0.0 (level) to 0.25 (down)

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
    { return (int)(rev * ConfigValues.ClicksPerRev + 0.5); }

  private double minShoulder(){
   if (elbowPosRev <= -20 / 360.0)
     return -40.0 / 360.0;
    if (elbowPosRev <= 85.0 / 360)
      return 0.0;
    return 5.0 / 360.0;
  }

  public double moveShoulder(double rev) {
    double pos = Range.clip(shoulderPosRev + rev, minShoulder(), 90.0 / 360.0);
    rev = pos - shoulderPosRev;
    shoulderPosRev = pos;
    int shoulder = Clicks(shoulderPosRev - ParkShoulder);
    shoulderMotor.setTargetPosition(shoulder);
    return rev * SecondsPerRev;
  }

  private double maxElbow() {
    if (shoulderPosRev > 5 / 360.0)
      return 90.0 / 360.0;
    if (shoulderPosRev >= 0)
      return 85.0 / 360.0;
    return 20.0 / 360.0;
  }

  public double moveElbow(double rev) {
    double pos = Range.clip(elbowPosRev + rev, MinElbow, maxElbow());
    rev = pos - elbowPosRev;
    elbowPosRev = pos;
    int elbow = Clicks(elbowPosRev - ParkElbow);
    elbowMotor.setTargetPosition(elbow);
    return rev * SecondsPerRev;
  }

  public double setShoulder(double rev)
    { return moveShoulder(rev - shoulderPosRev); }

  public double getShoulder() { return shoulderPosRev; }

  public double setElbow(double rev)
    { return moveElbow(rev - elbowPosRev); }

  public double getElbow() { return elbowPosRev; }

  public Arm() { }

  public void init(DcMotor shoulder, DcMotor elbow) {
    shoulderMotor = shoulder;
    elbowMotor = elbow;

    shoulderMotor.setDirection(DcMotor.Direction.REVERSE);
    elbowMotor.setDirection(DcMotor.Direction.FORWARD);

    setMotorMode(DcMotorController.RunMode.RESET_ENCODERS);
  }

  public void start() {
    setPower(0.0, 0.0);
    setMotorMode(DcMotorController.RunMode.RUN_TO_POSITION);
    shoulderPosRev = 0.0;
    elbowPosRev = 0.0;
  }

  public void park() { setShoulder(ParkShoulder); setElbow(ParkElbow); }

  public void home() { setShoulder(HomeShoulder); setElbow(HomeElbow); }

  public void stop() { setPower(0.0, 0.0); }

} // Arm
