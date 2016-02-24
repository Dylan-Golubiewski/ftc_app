package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the game_pad
 */

public class TeleOp_Bot extends OpMode {

    DcMotor LeftDrive;
    DcMotor RightDrive;
    //DcMotor LeftArm;
    //DcMotor RightArm;
    //DcMotor Sweeper;

    //Servo LeftBrake;
    //Servo RightBrake;
    Servo LeftZip;
    Servo RightZip;

    /**
     * Constructor
     */
    public TeleOp_Bot() {
    }

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */

    @Override
    public void init() {
		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

        LeftDrive = hardwareMap.dcMotor.get("wheel_left");
        LeftDrive.setDirection(DcMotor.Direction.REVERSE);
        RightDrive = hardwareMap.dcMotor.get("wheel_right");
        RightDrive.setDirection(DcMotor.Direction.FORWARD);

        //LeftArm = hardwareMap.dcMotor.get("LeftArm");
        //LeftArm.setDirection(DcMotor.Direction.FORWARD);
        //RightArm = hardwareMap.dcMotor.get("RightArm");
        //.setDirection(DcMotor.Direction.REVERSE);

        //Sweeper = hardwareMap.dcMotor.get("sweeper");
        //Sweeper.setDirection(DcMotor.Direction.REVERSE);

        //LeftBrake = hardwareMap.servo.get("LeftBrake");
        //LeftBrake.setDirection(Servo.Direction.FORWARD);
        //RightBrake = hardwareMap.servo.get("RightBrake");
        //RightBrake.setDirection(Servo.Direction.REVERSE);

        LeftZip = hardwareMap.servo.get("LeftZip");
        LeftZip.setDirection(Servo.Direction.FORWARD);
        RightZip = hardwareMap.servo.get("RightZip");
        RightZip.setDirection(Servo.Direction.REVERSE);

        //LeftBrake.setPosition(1);
        //RightBrake.setPosition(1);

        LeftZip.setPosition(1);
        RightZip.setPosition(1);


    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        //                          Driver one
        // tank drive
        // note that if y equal -1 then joystick is pushed all of the way forward.
        double leftDrivePower = gamepad1.left_stick_y;
        double rightDrivePower = gamepad1.right_stick_y;
        // clip the right/left values so that the values never exceed +/- 1
        rightDrivePower = Range.clip(rightDrivePower, -1, 1);
        leftDrivePower = Range.clip(leftDrivePower, -1, 1);
        if (gamepad1.left_bumper) {
            leftDrivePower /= 2;
            rightDrivePower /= 2;
        }

        else {
            // scale the joystick value to make it easier to control
            // the robot more precisely at slower speeds.
            rightDrivePower = scaleInput(rightDrivePower);
            leftDrivePower = scaleInput(leftDrivePower);
        }

        //boolean BrakeUp = gamepad1.left_bumper;
        //boolean BrakeDown = gamepad1.right_bumper;

        // write the values to the motors
        LeftDrive.setPower(leftDrivePower);
        RightDrive.setPower(rightDrivePower);

        //Note this next part is for the servos in the front of the robot to hold on to the first bar.
        // this is used by driver one.
        /*if (BrakeUp) {

            LeftBrake.setPosition(1);
            RightBrake.setPosition(1);

        }

        else if (BrakeDown) {
            LeftBrake.setPosition(-1);
            RightBrake.setPosition(-1);

        }

        *///                      Driver two

        //boolean sweeperPower = gamepad2.left_bumper;
        //boolean sweeperPowerBackwards = gamepad2.right_bumper;

        //boolean armpoweup = gamepad2.dpad_up;
       // boolean armpowerdown = gamepad2.dpad_down;

        boolean zippowerleftup = gamepad2.x;
        boolean zippowerleftdown = gamepad2.y;
        boolean zippowerrightup = gamepad2.a;
        boolean zippowerrightdown = gamepad2.b;

        /*if (sweeperPower ){

            Sweeper.setPower(1);

        }
        else if (sweeperPowerBackwards){

            Sweeper.setPower(-1);

        }
        else {

            Sweeper.setPower(0.0);

        }


        if (armpoweup){

            LeftArm.setPower(0.75);
            RightArm.setPower(0.75);
        }

        else if (armpowerdown){

            LeftArm.setPower(-0.75);
            RightArm.setPower(-0.75);

        }

        else {
            LeftArm.setPower(0.0);
            RightArm.setPower(0.0);
        }


        */
        if (zippowerleftup){
            LeftZip.setPosition(0);

        }

        else if (zippowerleftdown){
            LeftZip.setPosition(1);

        }

        else if (zippowerrightup){
            RightZip.setPosition(0);
        }

        else if (zippowerrightdown){
            RightZip.setPosition(1);
        }

		/*
		 * Send telemetry data back to driver station.
		 */

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", leftDrivePower));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", rightDrivePower));
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {
    }

    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        return dScale;
    }

}







