package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class Repark extends OpMode {

  private static double Speed = 0.2;

  private static double GearRatio = 12.0;

  public static double MinShoulder = -33.0 / 360; // angled back

  public static double ParkShoulder = MinShoulder;
  public static double ParkElbow = 20.0 / 360; // slightly down

  private static int Clicks(double rev)
    { return (int)(rev * GearRatio * ConfigValues.ClicksPerRev + 0.5); }

  private DcMotor shoulderMotor;
  private DcMotor elbowMotor;

  private void setPower(double pwr) {
    shoulderMotor.setPower(pwr);
    elbowMotor.setPower(pwr);
  }

  private void setMotorMode(DcMotorController.RunMode mode) {
    shoulderMotor.setMode(mode);
    elbowMotor.setMode(mode);
  }

  public Repark() { }

  @Override
  public void init() {
    shoulderMotor = hardwareMap.dcMotor.get("shoulderMotor");
    elbowMotor = hardwareMap.dcMotor.get("elbowMotor");

    shoulderMotor.setDirection(DcMotor.Direction.REVERSE);
    elbowMotor.setDirection(DcMotor.Direction.REVERSE);

    setMotorMode(DcMotorController.RunMode.RESET_ENCODERS);
  }

  @Override
  public void start() {
    setPower(Speed);
    setMotorMode(DcMotorController.RunMode.RUN_TO_POSITION);
  }

  @Override
  public void loop() {
    int shoulder = Clicks(ParkShoulder);
    shoulderMotor.setTargetPosition(shoulder);

    int elbow = Clicks(ParkElbow);
    elbowMotor.setTargetPosition(elbow);
  }

  @Override
  public void stop() { setPower(0.0); }

} // Repark