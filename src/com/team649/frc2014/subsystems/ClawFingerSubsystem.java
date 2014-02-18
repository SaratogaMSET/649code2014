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
     public static final int UP = 1;
     public static final int DOWN = 2;
     public static final int NEUTRAL = 3;
     private DoubleSolenoid clawSolenoid;
        
    private int fingerState;

     
     public ClawFingerSubsystem() {
        super("ClawFingerSubsystem");
        clawSolenoid = new DoubleSolenoid(RobotMap.CLAW_FINGER.FORWARD_SOLENOID_CHANNEL, RobotMap.DRIVE_TRAIN.REVERSE_SOLENOID_CHANNEL);
        
     }
     
     protected void initDefaultCommand() {
        
    }
     

    public void setFingerPosition(int state) {
        fingerState = state;
        switch(state){
            case(ClawFingerSubsystem.UP): 
                clawSolenoid.set(DoubleSolenoid.Value.kForward);
            case(ClawFingerSubsystem.DOWN):
                clawSolenoid.set(DoubleSolenoid.Value.kReverse);
            case(ClawFingerSubsystem.NEUTRAL):
                clawSolenoid.set(DoubleSolenoid.Value.kOff);
        }
    }

    public int getFingerPosition() {
        return fingerState;
    }
}
