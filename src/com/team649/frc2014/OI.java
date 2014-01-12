package com.team649.frc2014;

import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

    private Joystick joystick;

    public OI() {
        this.joystick = new Joystick(RobotMap.joystick);
    }

    public double getForward() {
        return -joystick.getY();
    }

    public double getRot() {
        return joystick.getX();
    }

    public boolean getTrigger() {
        return joystick.getRawButton(1);
    }
}
