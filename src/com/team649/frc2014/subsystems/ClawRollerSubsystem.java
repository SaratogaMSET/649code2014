/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * @author Kabi
 */
public class ClawRollerSubsystem extends Subsystem {

    public static final int OFF = 0;
    public static final int FORWARD = 1;
    public static final int REVERSE = -1;
    private final SpeedController motor;
    private double MOTOR_SPEED = 0.7;

    public ClawRollerSubsystem() {
        motor = new Victor(RobotMap.CLAW_ROLLER.MOTOR);
    }

    protected void initDefaultCommand() {
    }

    public void runMotor(int direction) {
        switch (direction) {
            case FORWARD:
                motor.set(MOTOR_SPEED);
                break;
            case REVERSE:
                motor.set(-MOTOR_SPEED);
                break;
            default:
                motor.set(0);
        }
    }
}
