package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class AutoShortLeft extends OpMode {

  private static double SecondsPerFoot = EncoderWheels.SecondsPerFoot;

  private enum State {
    Begin,
    Depart,
    Turn1,
    Straight,
    Turn2,
    Approach,
    Dock
  }

  private static double DepartDist   =  2.0;
  private static double Turn1Dist    =  ConfigValues.FeetPerCircle / 8; // 45
  // degrees
  private static double StraightDist =  2.0;
  private static double Turn2Dist    =  ConfigValues.FeetPerCircle / 8; // 45
  // degrees
  private static double ApproachDist =  0.3;

  private static double DepartDur   =  DepartDist   * SecondsPerFoot;
  private static double Turn1Dur    =  Turn1Dist    * SecondsPerFoot;
  private static double StraightDur =  StraightDist * SecondsPerFoot;
  private static double Turn2Dur    =  Turn2Dist    * SecondsPerFoot;
  private static double ApproachDur =  ApproachDist * SecondsPerFoot;

  private static double DepartEnd   = DepartDur;
  private static double Turn1End    = DepartEnd   + Turn1Dur;
  private static double StraightEnd = Turn1End    + StraightDur;
  private static double Turn2End    = StraightEnd + Turn2Dur;
  private static double ApproachEnd = Turn2End    + ApproachDur;

  private ElapsedTime timer = new ElapsedTime();

  private DcMotor frontLeftWheel;
  private DcMotor frontRightWheel;
  private DcMotor rearLeftWheel;
  private DcMotor rearRightWheel;

  private EncoderWheels wheels;

  private State state = State.Begin;

  public AutoShortLeft() { wheels = new EncoderWheels(); }

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
        wheels.move(DepartDist);
        state = State.Depart;
        // fall thru
      case Depart:
        if (timer.time() < DepartEnd)
          break;
        wheels.turnLeft(Turn1Dist);
        state = State.Turn1;
        // fall thru
      case Turn1:
        if (timer.time() < Turn1End)
          break;
        wheels.move(StraightDist);
        state = State.Straight;
        // fall thru
      case Straight:
        if (timer.time() < StraightEnd)
          break;
        wheels.turnLeft(Turn2Dist);
        state = State.Turn2;
        // fall thru
      case Turn2:
        if (timer.time() < Turn2End)
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

} // AutoShortLeft
