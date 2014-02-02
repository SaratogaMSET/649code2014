package com.team649.frc2014;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    public static final int joystick = 1;
    public static final int PRESSURE_SWITCH_CHANNEL = 1;
    public static final int COMPRESSOR_RELAY_CHANNEL = 10;

    public static class DRIVE_TRAIN {
        public static final int SOLENOID_CHANNEL = 1;
        public static final int[] MOTORS = new int[]{1, 2, 3, 4};
        public static final int[] ENCODERS = new int[]{1, 2, 3, 4};
    }
}
