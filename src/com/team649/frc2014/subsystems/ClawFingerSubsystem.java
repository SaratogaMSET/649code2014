/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * @author Kabi
 */
public class ClawFingerSubsystem extends Subsystem{
     public static final int Up = 1;
     public static final int Down = 2;
     public static final int Neutral = 3;
     private DoubleSolenoid clawSolenoid;
         private final SpeedController motor;
    private int fingerState;

     
     public ClawFingerSubsystem() {
        super("ClawFingerSubsystem");
        clawSolenoid = new DoubleSolenoid(RobotMap.CLAW_FINGER.FORWARD_SOLENOID_CHANNEL, RobotMap.DRIVE_TRAIN.REVERSE_SOLENOID_CHANNEL);
        motor = new Victor(RobotMap.CLAW_FINGER.MOTOR);

     }
     
     protected void initDefaultCommand() {
        
    }
     
     public void setMotor(double power) {
           motor.set(power);
       }

    public void SetFingerPosition(int state) {
        fingerState = state;
        switch(state){
            case(ClawFingerSubsystem.Up): 
                clawSolenoid.set(DoubleSolenoid.Value.kForward);
            case(ClawFingerSubsystem.Down):
                clawSolenoid.set(DoubleSolenoid.Value.kReverse);
            case(ClawFingerSubsystem.Neutral):
                clawSolenoid.set(DoubleSolenoid.Value.kOff);
        }
    }

    public int GetFingerPosition() {
        return fingerState;
    }
}
