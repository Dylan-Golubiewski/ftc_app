package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class AutoWaitRamp extends OpMode {

  private static double SecondsPerFoot = EncoderWheels.SecondsPerFoot;

  private enum State {
    Begin,
    Rest,
    Depart,
    Align,
    Approach,
    Dock
  }

  private static double DepartDist   = 3.8;
  private static double AlignDist    = ConfigValues.FeetPerCircle / 8; // 45
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

  private DcMotor frontLeftWheel;
  private DcMotor frontRightWheel;
  private DcMotor rearLeftWheel;
  private DcMotor rearRightWheel;

  private EncoderWheels wheels;

  private State state = State.Begin;


  public AutoWaitRamp() { wheels = new EncoderWheels(); }

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
        state = State.Rest;
        // fall thru
      case Rest:
        if (timer.time() < RestEnd)
          break;
        wheels.move(DepartDist);
        state = State.Depart;
        // fall thru
      case Depart:
        if (timer.time() < DepartEnd)
          break;
        wheels.turnRight(AlignDist);
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
        wheels.stop();
        state = State.Dock;
        // fall thru
      case Dock:
        break;
    }

    telemetry.addData("state", String.format("state: %s", state));
  }

  @Override
  public void stop() { wheels.stop(); }

} // AutoWaitRamp
