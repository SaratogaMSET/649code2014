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

    public static class CLAWPIVOT {

        public static final int POTENTIOMETER = -1;
        public static final int MOTOR = -1;
        
    }
    
    public static class CLAWWINCH {
        //all need to be changed
        public static final int MOTOR = -1;
        public static final int LIMIT_SWITCH_INPUT = -1;
        public static final int ENGAGED_SOLENOID_CHANNEL = 1;
        public static final int LOOSE_SOLENOID_CHANNEL = 2;
        public static final int TIME_TO_FIRE = 500;
        public static final int TIME_TO_ENGAGE_SOLENOID = 300;
    }
}
