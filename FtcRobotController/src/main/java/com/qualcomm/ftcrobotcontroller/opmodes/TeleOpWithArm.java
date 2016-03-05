package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class TeleOpWithArm extends OpMode {

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;
  private Servo ClawServo;
  private Claw claw;
  private Arm arm;
  DcMotor motorRightFront;
  DcMotor motorRightBack;
  DcMotor motorLeftFront;
  DcMotor motorLeftBack;

/*
  double A = 0;
  boolean isEven;
  boolean buttonAlreadyPressed;

  double throttle;
  double direction;
*/

  double left_power;
  double right_power;

  private ElapsedTime runtime = new ElapsedTime();

  double scaleInput(double dVal) {
    double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.16, 0.18, 0.24, 0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

    int index = (int) (dVal * 16.0);
    if (index < 0) {
      index = -index;

      if (index > 16)
        index = 16;
    }

    double dScale = scaleArray[index];
    if (dVal < 0)
      dScale = -dScale;

    return dScale;
  }

  public TeleOpWithArm() { arm = new Arm(); }

  @Override
  public void init() {
  //  buttonAlreadyPressed = false;

    motorLeftFront  = hardwareMap.dcMotor.get("FrontLeftDrive");
    motorLeftBack   = hardwareMap.dcMotor.get("BackLeftDrive");
    motorRightFront = hardwareMap.dcMotor.get("FrontRightDrive");
    motorRightBack  = hardwareMap.dcMotor.get("BackRightDrive");
    shoulderMotor   = hardwareMap.dcMotor.get("shoulderMotor");
    elbowMotor      = hardwareMap.dcMotor.get("elbowMotor");
    arm.init(shoulderMotor, elbowMotor);

    motorLeftFront.setDirection(DcMotor.Direction.FORWARD);
    motorLeftBack.setDirection(DcMotor.Direction.FORWARD);
    motorRightFront.setDirection(DcMotor.Direction.REVERSE);
    motorRightBack.setDirection(DcMotor.Direction.REVERSE);
  }

  //@Override
  //public void init_loop() {telemetry.addData("TeleOp Init Loop", runtime.toString());}

  @Override
  public void start() { arm.start(); }

  @Override
  public void loop() {
    /*
    boolean startIsPressed = gamepad1.guide;
    if (startIsPressed&& buttonAlreadyPressed) {
      // do nothing
    }
    else if (startIsPressed && !buttonAlreadyPressed){
      A = A+1;
      buttonAlreadyPressed = true;
    }
    else if (!startIsPressed && buttonAlreadyPressed){
      buttonAlreadyPressed = false;
    }

    if ((A % 2) == 0) {
      isEven = true;
    }
    else {
      isEven = false;
    }
    if (isEven) {
      double throttle = -gamepad1.left_stick_y;
      double direction = -gamepad1.right_stick_x;

      double right_power = throttle + direction;
      double left_power = throttle - direction;

      right_power = Range.clip(right_power, -1, 1);
      left_power = Range.clip(left_power, -1, 1);

      right_power = scaleInput(right_power);
      left_power = scaleInput(left_power);

      motorRightFront.setPower(right_power);
      motorRightBack.setPower(right_power);
      motorLeftFront.setPower(left_power);
      motorLeftBack.setPower(left_power);
    }
    else {
      double left_power = -gamepad1.left_stick_y;
      double right_power = -gamepad1.left_stick_y;

      right_power = Range.clip(right_power,-1,1);
      left_power = Range.clip(left_power,-1,1);

      right_power = scaleInput(right_power);
      left_power = scaleInput(left_power);

      motorLeftFront.setPower(left_power);
      motorLeftBack.setPower(left_power);
      motorRightBack.setPower(right_power);
      motorRightFront.setPower(right_power);
    }
*/
    double left_power = -gamepad1.left_stick_y;
    double right_power = -gamepad1.right_stick_y;

    right_power = Range.clip(right_power, -1, 1);
    left_power  = Range.clip(left_power,  -1, 1);

    right_power = scaleInput(right_power);
    left_power = scaleInput(left_power);

    motorLeftFront.setPower(left_power);
    motorLeftBack.setPower(left_power);
    motorRightBack.setPower(right_power);
    motorRightFront.setPower(right_power);

    if (gamepad2.a) {
      arm.home();
      return;
    }
    if (gamepad2.b) {
      arm.park();
      return;
    }
    if (gamepad1.y && gamepad2.y) {
      arm.unlimited();
      return;
    }
    double left  = -gamepad2.left_stick_y;
    double right = -gamepad2.right_stick_y;
    double Factor = 0.5 / 360.0;
    arm.moveShoulder(left * Factor);
    arm.moveElbow(right * Factor);
   // telemetry.addData("Text", "*** Robot Data***");
   // telemetry.addData("shoulder",
                   //   String.format("%.2f", arm.getShoulder() * 360));
    //telemetry.addData("elbow", String.format("%.2f", arm.getElbow() * 360));
   // telemetry.addData("1 Text", "*** Robot Data ***");
    //telemetry.addData("3 right tgt pwr", "right pwr: " + String.format("%0.2f",right_power));
    //telemetry.addData("2 left tgt pwr", "left tgt:" + String.format("%0.2f" , left_power));
    //telemetry.addData("4 Right_Front", motorRightFront.getPower());
    //telemetry.addData("5 Right_Back", motorLeftFront.getPower());
    //telemetry.addData("6 Left_Front", motorLeftBack.getPower());
   // telemetry.addData("7 Left_Back", motorLeftBack.getPower());
   // telemetry.addData("8 Throttle", throttle);
   // telemetry.addData("9 Direction", direction);
  }

  @Override
  public void stop() {
    arm.stop();
    motorLeftFront.setPower(0.0);
    motorLeftBack.setPower(0.0);
    motorRightBack.setPower(0.0);
    motorRightFront.setPower(0.0);
  }

}