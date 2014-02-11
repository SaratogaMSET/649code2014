package com.team649.frc2014;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

    private Joystick vertical;
    private Joystick horizontal;
    private Joystick shooter;
    private double ROTATION_POWER = 1.5;

    public OI() {
        this.vertical = new Joystick(RobotMap.joystickLeft);
        this.horizontal = new Joystick(RobotMap.joystickRight);
        this.shooter = new Joystick(RobotMap.joystickShooter);
    }

    public double getDriveForward() {
        return -vertical.getY();
    }

    public double getDriveRotation() {
        final double turnVal = horizontal.getX();
        final double sign = Team649Utils.sign(turnVal);
        return MathUtils.pow(Math.abs(turnVal), ROTATION_POWER) * sign;
    }

    public boolean getTrigger() {
        return horizontal.getRawButton(1) || vertical.getRawButton(1);
    }
    
    public boolean isCatchState() {
        return shooter.getRawButton(1);
    }
    
    public boolean isShootState() {
        return shooter.getRawButton(2);
    }
    
    public boolean isPickupState() {
        return shooter.getRawButton(3);
    }
    
    public boolean isStoreState() {
        return shooter.getRawButton(4);
    }
    
   
}
