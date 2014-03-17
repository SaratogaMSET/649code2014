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
    public static final int FORWARD_SHOOT = 2;
    public static final int PICKUP = 1;
    public static final int  BACKWARD_SHOOT= 0;
    public static final int NO_STATE = 5;
    public static final double[] CLAW_POT_STATES = new double[3];
    public static final String[] CLAW_POT_NAMES= new String[3];

    static {
        CLAW_POT_STATES[PICKUP] = 4.75;
        CLAW_POT_STATES[FORWARD_SHOOT] = 3.2;
        CLAW_POT_STATES[BACKWARD_SHOOT] = 1.83;
        CLAW_POT_NAMES[FORWARD_SHOOT] = "FWD SHOOT";
        CLAW_POT_NAMES[BACKWARD_SHOOT] = "BCK SHOOT";
        CLAW_POT_NAMES[PICKUP] = "PICKUP";
    }
    private PIDController649 clawPID;
    private final SpeedController motor;
    private final AnalogPotentiometer potentiometer;
    private int state;

    // Initialize your subsystem here
    public ClawPivotSubsystem() {
        super("ClawSubsystem");
        motor = new Victor(RobotMap.CLAW_PIVOT.MOTOR);
        potentiometer = new AnalogPotentiometer(RobotMap.CLAW_PIVOT.POTENTIOMETER);
        clawPID = new PIDController649(kP, kI, kD, potentiometer, motor);
        clawPID.setAbsoluteTolerance(0.02);
        clawPID.setOutputRange(-.6, .35);
        state = NO_STATE;
    }

    protected void initDefaultCommand() {
    }

    public PIDController649 getClawPID() {
        return clawPID;
    }

    public void setPower(double power) {
        if (Math.abs(power) > .1) {
            motor.set(power);
        } else {
            motor.set(0);
        }

    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public double getPotValue() {
        return potentiometer.pidGet();
    }
    
    public String getPotStateName() {
        double val = getPotValue();
        for (int i = 0; i < CLAW_POT_STATES.length; i++) {
            double d = CLAW_POT_STATES[i];
            if (Math.abs(val - d) < 0.075)
                return CLAW_POT_NAMES[i];
        }
        return "NO STATE";
    }
}
