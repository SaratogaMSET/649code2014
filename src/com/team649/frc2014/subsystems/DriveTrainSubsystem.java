/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * @author Alex
 */
public class DriveTrainSubsystem extends Subsystem {
    
    private SpeedController[] motors;
    
    public DriveTrainSubsystem() {
        motors = new SpeedController[RobotMap.driveTrainMotors.length];
        for (int i = 0; i < RobotMap.driveTrainMotors.length; i++) {
            motors[i] = new Victor(RobotMap.driveTrainMotors[i]);
        }
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void driveFwdRot(double fwd, double rot) {
        double left = fwd + rot;
        double right = fwd - rot;
        double max = Math.max(1, Math.max(left, right));
        left /= max;
        right /= max;
        rawDrive(left, right);
    }
    
    public void rawDrive(double left, double right) {
        int i = 0;
        for (; i < motors.length / 2; i++) {
            motors[i].set(left);
        }
        
        for (; i < motors.length; i++) {
            motors[i].set(right);
        }
    }
}