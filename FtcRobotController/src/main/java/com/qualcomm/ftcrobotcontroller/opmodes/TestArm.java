package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class TestArm extends OpMode {

  private enum State {
    Begin,
    Raise,
    Extend,
    Lower,
    Drop,
    Raise2,
    Retract,
    Repark,
    Dock
  }

  private ElapsedTime timer = new ElapsedTime();

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;
  private Arm arm;

  private State state = State.Begin;

  private static double RaiseElbowAngle = 20.0 / 360;
  private static double ExtendShoulderAngle = 30.0 / 360;
  private static double LowerElbowAngle = 10.0 / 360;

  private static double Shoulder;
  private static double RaiseElbowRotate = Arm.ParkElbow;

  private static double RaiseDur   =  0.0;
  private static double ExtendDur  =  0.0;
  private static double LowerDur   =  0.0;
  private static double DropDur    =  0.0;
  private static double Raise2Dur  =  0.0;
  private static double RetractDur =  0.0;
  private static double ReparkDur  =  0.0;

  private static double RaiseEnd   =  RaiseDur;
  private static double ExtendEnd  =  RaiseEnd   + ExtendDur;
  private static double LowerEnd   =  ExtendEnd  + LowerDur;
  private static double DropEnd    =  LowerEnd   + DropDur;
  private static double Raise2End  =  DropEnd    + Raise2Dur;
  private static double RetractEnd =  Raise2End  + RetractDur;
  private static double ReparkEnd  =  RetractEnd + ReparkDur;

  public TestArm() { }

  @Override
  public void init() {
    shoulderMotor = hardwareMap.dcMotor.get("shoulderMotor");
    elbowMotor = hardwareMap.dcMotor.get("elbowMotor");
    arm.init(shoulderMotor, elbowMotor);
  }

  @Override
  public void start() {
    arm.start();
    state = State.Begin;
  }

  @Override
  public void loop() {
    switch (state) {
      case Begin:
        timer.reset();
        state = State.Raise;
        // fall thru
      case Raise:
        if (timer.time() < RaiseEnd)
          break;
        state = State.Extend;
        // fall thru
      case Extend:
        if (timer.time() < ExtendEnd)
          break;
        state = State.Lower;
        // fall thru
      case Lower:
        if (timer.time() < LowerEnd)
          break;
        state = State.Drop;
        // fall thru
      case Drop:
        if (timer.time() < DropEnd)
          break;
        state = State.Raise2;
        // fall thru
      case Raise2:
        if (timer.time() < Raise2End)
          break;
        state = State.Dock;
        // fall thru
      case Retract:
        if (timer.time() < RetractEnd)
          break;
        state = State.Dock;
        // fall thru
      case Repark:
        if (timer.time() < ReparkEnd)
          break;
        state = State.Dock;
        // fall thru
      case Dock:
        break;
    }

    telemetry.addData("state", String.format("state: %s", state));
  }

  @Override
  public void stop() { arm.park(); arm.stop(); }

} // TestArm
