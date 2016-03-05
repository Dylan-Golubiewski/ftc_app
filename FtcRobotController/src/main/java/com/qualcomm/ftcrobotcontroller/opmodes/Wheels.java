package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Wheels class.
 * Enables control of the robot movement via the game_pad.
 */
public class Wheels {

  private static double[] ScaleArray = {
    0.00, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
    0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00
  };

  private static double ScaleInput(double dVal)  {
    dVal = Range.clip(dVal, -1, 1);

    // get the corresponding index for the scaleInput array.
    int index = (int) (Math.abs(dVal) * 16.0);
    if (index > 15)
      index = 15;


    // get value from the array.
    double dScale = ScaleArray[index];
    return (dVal >= 0) ? dScale : -dScale;
  }

  private DcMotor motorFrontLeft;
  private DcMotor motorFrontRight;
  private DcMotor motorRearLeft;
  private DcMotor motorRearRight;

  private void setMotorMode(DcMotorController.RunMode mode) {
    motorFrontRight.setMode(mode);
    motorFrontLeft.setMode(mode);
    motorRearRight.setMode(mode);
    motorRearLeft.setMode(mode);
  }

  private void setPower(double left, double right) {
    motorFrontLeft.setPower(left);
    motorFrontRight.setPower(right);
    motorRearLeft.setPower(left);
    motorRearRight.setPower(right);
  }

  public Wheels() { }

  public void init(DcMotor frontLeft, DcMotor frontRight,
		   DcMotor rearLeft, DcMotor rearRight)
  {
    motorFrontLeft  = frontLeft;
    motorFrontRight = frontRight;
    motorRearLeft   = rearLeft;
    motorRearRight  = rearRight;

    motorFrontLeft.setDirection(DcMotor.Direction.FORWARD);
    motorFrontRight.setDirection(DcMotor.Direction.REVERSE);
    motorRearLeft.setDirection(DcMotor.Direction.FORWARD);
    motorRearRight.setDirection(DcMotor.Direction.REVERSE);

    setMotorMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
  }


  public void start() { setPower(0.0, 0.0); }

  // Movement inputs: -1 = full reverse, 1 = full forward.
  public void move(double left, double right) {
    double scaled_left   = ScaleInput(left);
    double scaled_right  = ScaleInput(right);
    setPower(scaled_left, scaled_right);
  }

  public void stop() { setPower(0.0, 0.0); }

}; // Wheels
