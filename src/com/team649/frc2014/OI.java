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
        this.vertical = new Joystick(RobotMap.JOYSTICK_DRIVER_LEFT);
        this.horizontal = new Joystick(RobotMap.JOYSTICK_DRIVER_RIGHT);
        this.shooter = new Joystick(RobotMap.JOYSTICK_SHOOTER);
    }

    public double getDriveForward() {
        return -vertical.getY();
    }

    public double getDriveRotation() {
        final double turnVal = horizontal.getX();
        final double sign = Team649Utils.sign(turnVal);
        return MathUtils.pow(Math.abs(turnVal), ROTATION_POWER) * sign;
    }

    public boolean getDrivetrainLowGearButtonPressed() {
        return horizontal.getRawButton(1) || vertical.getRawButton(1);
    }

    public boolean getShooterTrigger() {
        return shooter.getRawButton(1);
    }

    public boolean isCatchClawPositionButtonPressed() {
        return shooter.getRawButton(11);
    }

    public boolean isShootClawPositionButtonPressed() {
        return shooter.getRawButton(12);
    }

    public boolean isPickupClawPositionButtonPressed() {
        return shooter.getRawButton(9);
    }

    public boolean isStoreClawPositionButtonPressed() {
        return shooter.getRawButton(10);
    }

    public boolean getCoilButton() {
         return shooter.getRawButton(5);
    }
    
    public double getShooterJoystick() {
        return shooter.getY();
    }

    public boolean getPivotOverrideButton() {
        return shooter.getRawButton(2);
    }

    public boolean getWinchOverrideButton() {
      return shooter.getRawButton(3);
    }
    
    public boolean isPickupButtonPressed() {
        return shooter.getRawButton(4);
    }
    
    public boolean isPurgeButtonPressed() {
        return shooter.getRawButton(6);
    }
}
