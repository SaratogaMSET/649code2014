package com.team649.frc2014;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    public static final int joystickLeft = 2;
    public static final int joystickRight = 1;
    public static final int joystickShooter = 3;
    public static final int PRESSURE_SWITCH_CHANNEL = 1;
    public static final int COMPRESSOR_RELAY_CHANNEL = 1;

    public static class DRIVE_TRAIN {
        public static final int FORWARD_SOLENOID_CHANNEL = 1;
        public static final int REVERSE_SOLENOID_CHANNEL = 2;
        public static final int[] MOTORS = new int[]{1, 2, 3, 4};
        public static final int[] ENCODERS = new int[]{2,3,4,5};
    }
}
