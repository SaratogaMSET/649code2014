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
public class ClawPivotSubsystem extends Subsystem {

    private static final double kP = 0.01;
    private static final double kI = 0.0;
    private static final double kD = 0.0;
    public static final int SHOOT = 2;
    public static final int STORE = 0;
    public static final int PICKUP = 1;
    public static final int CATCH = 3;
    public static final int NO_STATE = 5;
    public static final int[] CLAW_POT_STATES = new int[]{0, 0, 0, 0};
    private PIDController649 clawPID;
    private final SpeedController motor;
    private final Potentiometer potentiometer;
    private int state;

    // Initialize your subsystem here
    public ClawPivotSubsystem() {
        super("ClawSubsystem");
        motor = new Victor(RobotMap.CLAW_PIVOT.MOTOR);
        potentiometer = new AnalogPotentiometer(RobotMap.CLAW_PIVOT.POTENTIOMETER);
        clawPID = new PIDController649(kP, kI, kD, potentiometer, motor);
        clawPID.setAbsoluteTolerance(10);
        state = NO_STATE;
    }

    protected void initDefaultCommand() {
    }

    public PIDController649 getClawPID() {
        return clawPID;
    }

    public void setPower(double power) {
            if (power < -.1) 
                motor.set(power - .2);
            
            else if (power > .1) 
                motor.set(power + .2);
                      
             else 
                motor.set(0);
            
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
