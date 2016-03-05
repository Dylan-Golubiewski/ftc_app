package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutoWaitRampRight extends OpMode {

  private static double Speed = 0.4;
  private static double InnerSpeed = Speed * ConfigValues.InsideRatio;

  private static double SecondsPerFoot = 0.5 / Speed;

  private enum State {
    Begin,
    Rest,
    Depart,
    Align,
    Approach,
    Dock
  }

  private static double DepartDist   = 3.8;
  private static double AlignDist    =  ConfigValues.FeetPerCircle / 8; // 45
  // degrees
  private static double ApproachDist = 1.5;

  private static double DepartDur   =  DepartDist   * SecondsPerFoot;
  private static double RestDur     =  12.0; // seconds
  private static double AlignDur    =  AlignDist    * SecondsPerFoot;
  private static double ApproachDur =  ApproachDist * SecondsPerFoot;

  private static double RestEnd     = RestDur;
  private static double DepartEnd   = RestEnd   + DepartDur;
  private static double AlignEnd    = DepartEnd + AlignDur;
  private static double ApproachEnd = AlignEnd  + ApproachDur;

  private ElapsedTime timer = new ElapsedTime();

  private DcMotor frontRightWheel;
  private DcMotor frontLeftWheel;
  private DcMotor rearRightWheel;
  private DcMotor rearLeftWheel;

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
    frontLeftWheel.setMode(mode);
    frontRightWheel.setMode(mode);
    rearLeftWheel.setMode(mode);
    rearRightWheel.setMode(mode);
  }

  private static int Clicks(double ft)
    { return (int)(ft / ConfigValues.FeetPerWheelRev * ConfigValues.ClicksPerRev
            + 0.5); }

  private void setTarget(double rightFt, double leftFt) {
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

  public AutoWaitRampRight() { }

  @Override
  public void init() {
    frontLeftWheel  = hardwareMap.dcMotor.get("FrontLeftDrive");
    frontRightWheel = hardwareMap.dcMotor.get("FrontRightDrive");
    rearLeftWheel  = hardwareMap.dcMotor.get("BackLeftDrive");
    rearRightWheel = hardwareMap.dcMotor.get("BackRightDrive");

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
        state = State.Rest;
        // fall thru
      case Rest:
        if (timer.time() < RestEnd)
          break;
        move(DepartDist);
        state = State.Depart;
        // fall thru
      case Depart:
        if (timer.time() < DepartEnd)
          break;
        turnRight(AlignDist);
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
        setPower(0.0, 0.0);
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

} // AutoWaitRamp
