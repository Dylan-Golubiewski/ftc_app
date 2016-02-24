package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ArmTester extends OpMode {
  private static double UpSpeed = 0.15;
  private static double DownSpeed = 0.15;

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;

  public ArmTester() { }

  @Override
  public void init() {
    shoulderMotor = hardwareMap.dcMotor.get("shoulderMotor");
    elbowMotor = hardwareMap.dcMotor.get("elbowMotor");
    shoulderMotor.setDirection(DcMotor.Direction.REVERSE);
    elbowMotor.setDirection(DcMotor.Direction.FORWARD);
  }

  @Override
  public void start() {
    shoulderMotor.setPower(0.0);
    elbowMotor.setPower(0.0);
  }

  @Override
  public void loop() {
    if (gamepad1.left_bumper) {
      shoulderMotor.setPower(UpSpeed);
    }
    else if (gamepad1.right_bumper) {
      shoulderMotor.setPower(-DownSpeed);
    }
    else if (gamepad1.a) {
      elbowMotor.setPower(UpSpeed);
    }
    else if (gamepad1.b) {
      elbowMotor.setPower(-DownSpeed);
    }
    else if (gamepad1.x || gamepad1.y) {
      shoulderMotor.setPower(0.0);
      elbowMotor.setPower(0.0);
    }
   // telemetry.addData("state", String.format("state: %s", state));
  }

  @Override
  public void stop() {
    shoulderMotor.setPower(0.0);
    elbowMotor.setPower(0.0);
  }

} // TestArm
