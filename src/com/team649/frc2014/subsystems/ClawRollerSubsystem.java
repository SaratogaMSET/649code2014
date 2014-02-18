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
public class ClawRollerSubsystem extends Subsystem{
    private final SpeedController motor;
    private double MOTOR_SPEED = 1;
    
    public ClawRollerSubsystem() {
        motor = new Victor(RobotMap.CLAW_ROLLER.MOTOR);
    }

    protected void initDefaultCommand() {
    }
     public void runMotor(int direction) {
        motor.set(direction * MOTOR_SPEED);
    }

    public void stopMotor() {
        motor.set(0);
    }
}
