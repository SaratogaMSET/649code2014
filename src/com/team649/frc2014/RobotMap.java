package com.team649.frc2014;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    public static final int[] driveTrainMotors = new int[]{1,10};
    public static final int[] encoders = new int[]{1,2,3,4};
    public static final int joystick = 1;
}
