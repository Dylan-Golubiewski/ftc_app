package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutoRamp extends OpMode {

  private static double SecondsPerFoot = EncoderWheels.SecondsPerFoot;

  private enum State {
    Begin,
    Depart,
    Align,
    Approach,
    Ramp,
    Dock,
  }

  private static double DepartDist   =  3.0;
  private static double AlignDist    =  ConfigValues.FeetPerCircle / 8; // 45
  // degrees
  private static double ApproachDist =  5.8;
  private static double RampDist     =  0.0;

  private static double DepartDur   =  DepartDist   * SecondsPerFoot;
  private static double AlignDur    =  AlignDist    * SecondsPerFoot;
  private static double ApproachDur =  ApproachDist * SecondsPerFoot;
  private static double RampDur     =  RampDist     * SecondsPerFoot;

  private static double DepartEnd   = DepartDur;
  private static double AlignEnd    = DepartEnd   + AlignDur;
  private static double ApproachEnd = AlignEnd    + ApproachDur;
  private static double RampEnd     = ApproachEnd + RampDur;

  private ElapsedTime timer = new ElapsedTime();

  private DcMotor frontLeftWheel;
  private DcMotor frontRightWheel;
  private DcMotor rearLeftWheel;
  private DcMotor rearRightWheel;

  private EncoderWheels wheels;

  private State state = State.Begin;

  public AutoRamp() { wheels = new EncoderWheels(); }

  @Override
  public void init() {
    frontLeftWheel  = hardwareMap.dcMotor.get("FrontLeftDrive");
    frontRightWheel = hardwareMap.dcMotor.get("FrontRightDrive");
    rearLeftWheel   = hardwareMap.dcMotor.get("BackLeftDrive");
    rearRightWheel  = hardwareMap.dcMotor.get("BackRightDrive");

    wheels.init(frontLeftWheel, frontRightWheel, rearLeftWheel, rearRightWheel);
  }

  @Override
  public void start() {
    wheels.start();
    state = State.Begin;
  }

  @Override
  public void loop() {
    switch (state) {
      case Begin:
        timer.reset();
        wheels.move(-DepartDist);
        state = State.Depart;
        // fall thru
      case Depart:
        if (timer.time() < DepartEnd)
          break;
        wheels.turnRight(-AlignDist);
        state = State.Align;
        // fall thru
      case Align:
        if (timer.time() < AlignEnd)
          break;
        wheels.move(ApproachDist);
        state = State.Approach;
        // fall thru
      case Approach:
        if (timer.time() < ApproachEnd)
          break;
        wheels.move(RampDist);
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
  }

  @Override
  public void stop() { wheels.stop(); }

} // AutoRamp
