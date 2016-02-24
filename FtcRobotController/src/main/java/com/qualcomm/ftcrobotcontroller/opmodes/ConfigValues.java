package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Single place for configuration parameters.
 */
public class ConfigValues {
  private static double Pi = 3.14159;
  public static int MotorClicksPerRev = 1120;
  public static double GearRatio = 1;
  public static double ClicksPerRev = MotorClicksPerRev * GearRatio;
  public static double FeetPerWheelRev = 1.9;
  public static double FeetPerSpin = 4.5;
  public static double FeetPerCircle = 23;

  public static double InsideRatio = 0.5; // 4' outer-radius turn.





} // ConfigValues
