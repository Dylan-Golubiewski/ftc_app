package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutoRampBack extends OpMode {

  private static double Speed = 0.4;
  private static double InnerSpeed = Speed * ConfigValues.InsideRatio;

  private static double SecondsPerFoot = 0.4 / Speed;

  private enum State {
    Begin,
    Depart,
    Spin,
    Align,
    Approach,
    Ramp,
    Dock,
  }

/*
  private static double DepartDist   = 6.5;
  private static double DepartDist   = 6.5;
  private static double SpinRev      = 0.5;
  private static double SpinDist     = SpinRev * FeetPerSpin;
  private static double AlignDist    = FeetPerCircle / 8;
  private static double ApproachDist = 0.0;
  private static double RampDist     = 0.0;
*/

  private static double DepartDist   = ConfigValues.FeetPerCircle;
  private static double SpinRev      = 1.0;
  private static double SpinDist     = SpinRev * ConfigValues.FeetPerSpin;
  private static double AlignDist    = 0.0;
  private static double ApproachDist = 0.0;
  private static double RampDist     = 0.0;

  private static double DepartDur   =  DepartDist   * SecondsPerFoot;
  private static double SpinDur     =  SpinDist     * SecondsPerFoot;
  private static double AlignDur    =  AlignDist    * SecondsPerFoot;
  private static double ApproachDur =  ApproachDist * SecondsPerFoot;
  private static double RampDur     =  RampDist     * SecondsPerFoot;

  private static double DepartEnd   = DepartDur;
  private static double SpinEnd     = DepartEnd   + SpinDur;
  private static double AlignEnd    = SpinEnd     + AlignDur;
  private static double ApproachEnd = AlignEnd    + ApproachDur;
  private static double RampEnd     = ApproachEnd + RampDur;

  private ElapsedTime timer = new ElapsedTime();

  private DcMotor frontLeftWheel;
  private DcMotor frontRightWheel;
  private DcMotor rearLeftWheel;
  private DcMotor rearRightWheel;

  private State state = State.Begin;

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

  private static int Clicks(double ft)
    { return (int)(ft / ConfigValues.FeetPerWheelRev * ConfigValues.ClicksPerRev
            + 0.5); }

  private void setTarget(double leftFt, double rightFt) {
    int left  = Clicks(leftFt);
    int right = Clicks(rightFt);
    frontLeftWheel.setTargetPosition(left);
    frontRightWheel.setTargetPosition(right);
    rearLeftWheel.setTargetPosition(left);
    rearRightWheel.setTargetPosition(right);
  }

  private void move(double ft) {
    leftPosFt  += ft;
    rightPosFt += ft;
    setPower(Speed, Speed);
    setTarget(leftPosFt, rightPosFt);
  }

  private void turnLeft(double ft) {
    leftPosFt  += ft * ConfigValues.InsideRatio;
    rightPosFt += ft;
    setPower(InnerSpeed, Speed);
    setTarget(leftPosFt, rightPosFt);
  }

  private void turnRight(double ft) {
    leftPosFt  += ft;
    rightPosFt += ft * ConfigValues.InsideRatio;
    setPower(Speed, InnerSpeed);
    setTarget(leftPosFt, rightPosFt);
  }

  private void spin(double rev) {
    double d = rev * ConfigValues.FeetPerSpin;
    leftPosFt  += d;
    rightPosFt -= d;
    setPower(Speed, Speed);
    setTarget(leftPosFt, rightPosFt);
  }

  public AutoRampBack() { }

  @Override
  public void init() {
    frontRightWheel = hardwareMap.dcMotor.get("FrontRightDrive");
    frontLeftWheel  = hardwareMap.dcMotor.get("FrontLeftDrive");
    rearRightWheel = hardwareMap.dcMotor.get("BackRightDrive");
    rearLeftWheel  = hardwareMap.dcMotor.get("BackLeftDrive");

    frontLeftWheel.setDirection(DcMotor.Direction.FORWARD);
    rearLeftWheel.setDirection(DcMotor.Direction.FORWARD);
    frontRightWheel.setDirection(DcMotor.Direction.REVERSE);
    rearRightWheel.setDirection(DcMotor.Direction.REVERSE);

    setWheelMode(DcMotorController.RunMode.RESET_ENCODERS);
  }

  @Override
  public void start() {
    setPower(0.0, 0.0);
    setWheelMode(DcMotorController.RunMode.RUN_TO_POSITION);
    leftPosFt  = 0.0;
    rightPosFt = 0.0;
    state = State.Begin;
  }

  @Override
  public void loop() {
    switch (state) {
      case Begin:
        timer.reset();
        turnLeft(DepartDist);
        //move(-DepartDist);
        state = State.Depart;
        // fall thru
      case Depart:
        if (timer.time() < DepartEnd)
          break;
        spin(SpinRev);
        state = State.Spin;
        // fall thru
      case Spin:
        if (timer.time() < SpinEnd)
          break;
        turnLeft(AlignDist);
        state = State.Align;
        // fall thru
      case Align:
        if (timer.time() < AlignEnd)
          break;
        move(ApproachDist);
        state = State.Approach;
        // fall thru
      case Approach:
        if (timer.time() < ApproachEnd)
          break;
        move(RampDist);
        state = State.Ramp;
        // fall thru
      case Ramp:
        if (timer.time() < RampEnd)
          break;
        state = State.Dock;
        // fall thru
      case Dock:
        break;
    }

    telemetry.addData("state", String.format("state: %s", state));
    telemetry.addData("left",  String.format("%.2f", leftPosFt));
    telemetry.addData("right", String.format("%.2f", rightPosFt));
  }

  @Override
  public void stop() { setPower(0.0, 0.0); }

} // AutoRampBack
