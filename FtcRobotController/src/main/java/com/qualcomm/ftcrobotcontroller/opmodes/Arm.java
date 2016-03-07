package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

public class Arm {
  private static double GearRatio = 12.0;
  private static double TurretGearRatio = 6.0; // estimate

  public static double MinShoulder = -33.0 / 360; // angled back
  public static double MaxShoulder = +80.0 / 360; // almost straight forward

  public static double MinElbow = -35.0 / 360;  // slightly up
  public static double MaxElbow = +90.0 / 360;  // straight down

  private static double MinAngle = 26.0 / 360;
  private static double MaxAngle = 145.0 / 360;

  public static double ParkShoulder = MinShoulder;
  public static double ParkElbow = 16.0 / 360; // slightly down
  public static double ParkTurret = 0.0 / 360; // center
  public static double HomeShoulder = 0.0 / 360; // slightly forward
  public static double HomeElbow = 0.0 / 360; // slightly down
  public static double HomeTurret = 0.0 / 360; // center

  private static double ClawOpenedPos = 0.0;
  private static double ClawClosedPos = 0.5;

  private static double TurretLimit = 30.0 / 360;

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;
  private DcMotor turretMotor;
  private Servo clawServo;

  private double shoulderPower = 0.0;
  private double elbowPower = 0.0;
  private double turretPower = 0.0;

  private double shoulderPosRev = 0.0;  // -0.125 (parked\
                                        //  0.0 (up)
					//  0.25 (forward)
  private double elbowPosRev = 0.0;     //  0.0 (level) to 0.25 (down)

  private double turretPosRev = 0.0;    // left = negative, right = positive

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

  private void setTurretPower(double pwr) {
    turretPower = Range.clip(pwr,  -1, 1);
    turretMotor.setPower(shoulderPower);
  }

  private void setPower(double shoulder, double elbow, double turret) {
    setShoulderPower(shoulder);
    setElbowPower(elbow);
    setTurretPower(turret);
  }

  private void setMotorMode(DcMotorController.RunMode mode) {
    shoulderMotor.setMode(mode);
    elbowMotor.setMode(mode);
    turretMotor.setMode(mode);
  }

  private static int Clicks(double rev)
    { return (int)(rev * GearRatio * ConfigValues.ClicksPerRev + 0.5); }

  private static int TurretClicks(double rev)
    { return (int)(rev * TurretGearRatio * ConfigValues.ClicksPerRev + 0.5); }

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

  public double moveTurret(double rev) {
    double pos = turretPosRev + rev;
    if (!noLimits)
      pos = Range.clip(pos, -TurretLimit, +TurretLimit);
    rev = pos - turretPosRev;
    turretPosRev = pos;
    int turret = TurretClicks(turretPosRev - ParkTurret);
    turretMotor.setTargetPosition(turret);
    return rev * secondsPerRev();
  }

  public double setShoulder(double rev)
    { return moveShoulder(rev - shoulderPosRev); }

  public double getShoulder() { return shoulderPosRev; }

  public double setElbow(double rev)
    { return moveElbow(rev - elbowPosRev); }

  public double getElbow() { return elbowPosRev; }

  public double setTurret(double rev)
    { return moveTurret(rev - turretPosRev); }

  public double getTurret() { return turretPosRev; }

  public void unlimited() {
    noLimits = true;
    speed = 0.2;
  }

  public void openClaw()  { clawServo.setPosition(ClawOpenedPos); }
  public void closeClaw() { clawServo.setPosition(ClawClosedPos); }

  public Arm() { }

  public void init(DcMotor shoulder, DcMotor elbow, DcMotor turret, Servo claw)
  {
    shoulderMotor = shoulder;
    elbowMotor = elbow;
    turretMotor = turret;
    clawServo = claw;

    shoulderMotor.setDirection(DcMotor.Direction.REVERSE);
    elbowMotor.setDirection(DcMotor.Direction.REVERSE);
    turretMotor.setDirection(DcMotor.Direction.FORWARD);
    clawServo.setDirection(Servo.Direction.FORWARD);

    setMotorMode(DcMotorController.RunMode.RESET_ENCODERS);
  }

  public void start() {
    setPower(speed, speed, speed);
    setMotorMode(DcMotorController.RunMode.RUN_TO_POSITION);
    shoulderPosRev = ParkShoulder;
    elbowPosRev = ParkElbow;
    turretPosRev = ParkTurret;
  }

  public void park() {
    if (noLimits)
      return;
    closeClaw();
    setShoulder(ParkShoulder);
    setElbow(ParkElbow);
    setTurret(ParkTurret);

  }

  public void home() {
    if (noLimits)
      return;
    closeClaw();
    setShoulder(HomeShoulder);
    setElbow(HomeElbow);
    setTurret(HomeTurret);
  }

  public void stop() { setPower(0.0, 0.0, 0.0); }

} // Arm
