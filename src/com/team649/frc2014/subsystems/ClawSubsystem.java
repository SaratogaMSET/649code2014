/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import com.team649.frc2014.pid_control.PIDController649;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

/**
 *
 * @author Kabi
 */
public class ClawSubsystem extends Subsystem {

    private static final double kP = 0.01;
    private static final double kI = 0.0;
    private static final double kD = 0.0;
    private PIDController649 clawPID;
    public static final int Shoot = 2;
    public static final int Store = 0;
    public static final int Pickup = 1;
    public static final int Catch = 3;
    public static final int[] clawState = new int[] {0,0,0,0};
    private final SpeedController motor;
    private final Potentiometer potentiometer;

    // Initialize your subsystem here
    public ClawSubsystem() {
        super("ClawSubsystem");
        motor = new Victor(RobotMap.CLAW.MOTOR);
        potentiometer = new AnalogPotentiometer(RobotMap.CLAW.POTENTIOMETER);
        clawPID = new PIDController649(kP, kI, kD, potentiometer, motor);

    }

    protected void initDefaultCommand() {
        
    }
    
       public PIDController649 getClawPID() {
        return clawPID;
    }
       public void setPower(double power) {
           motor.set(power);
       }
  
}
