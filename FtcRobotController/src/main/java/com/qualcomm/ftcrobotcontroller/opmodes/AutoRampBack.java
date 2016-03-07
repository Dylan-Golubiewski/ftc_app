package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class AutoRampBack extends OpMode {

  private static double SecondsPerFoot = EncoderWheels.SecondsPerFoot;

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

  private EncoderWheels wheels;

  private State state = State.Begin;

  public AutoRampBack() { wheels = new EncoderWheels(); }

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
        wheels.turnLeft(DepartDist);
        state = State.Depart;
        // fall thru
      case Depart:
        if (timer.time() < DepartEnd)
          break;
        wheels.spin(SpinRev);
        state = State.Spin;
        // fall thru
      case Spin:
        if (timer.time() < SpinEnd)
          break;
        wheels.turnLeft(AlignDist);
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

} // AutoRampBack
