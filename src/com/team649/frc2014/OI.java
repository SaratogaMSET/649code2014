package com.team649.frc2014;

import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

    private Joystick vertical;
    private Joystick horizontal;
    private Joystick shooter;
    

    public OI() {
        this.vertical = new Joystick(RobotMap.joystickLeft);
        this.horizontal = new Joystick(RobotMap.joystickRight);
        this.shooter = new Joystick(RobotMap.joystickShooter);
    }

    public double getDriveForward() {
        return -vertical.getY();
    }

    public double getDriveRotation() {
        return horizontal.getX();
    }

    public boolean getTrigger() {
        return horizontal.getRawButton(1) || vertical.getRawButton(1);
    }
}
