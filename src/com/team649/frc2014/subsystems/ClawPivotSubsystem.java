/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import com.team649.frc2014.pid_control.PIDController649;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Kabi
 */
public class ClawPivotSubsystem extends Subsystem implements PIDOutput {

    public static final double kP = -1;
    public static final double kI = -.1;
    public static final double kD = 0.0;
    public static final double MAX_BACKWARD_SPEED = .4;
    public static final double MAX_FORWARD_SPEED = -.6;
    public static final double FULL_BACKWARD_POSITION = .9;
    public static final double FULL_FORWARD_POSITION = 5.2;
    public static final int STORE = 3;
    public static final int FORWARD_SHOOT = 2;
    public static final int PICKUP = 1;
    public static final int BACKWARD_SHOOT = 0;
    public static final int GOAL_SHOOT = 4;
    public static final int NO_STATE = 5;
    public static final double[] CLAW_POT_STATES = new double[5];
    public static final String[] CLAW_POT_NAMES = new String[5];

    static {
        CLAW_POT_STATES[PICKUP] = 4.85;
        //42.5 degrees
        CLAW_POT_STATES[FORWARD_SHOOT] = 3.27;
        CLAW_POT_STATES[BACKWARD_SHOOT] = 1.47;
        CLAW_POT_STATES[STORE] = 2.2;
        CLAW_POT_STATES[GOAL_SHOOT] = 1.63;
        CLAW_POT_NAMES[FORWARD_SHOOT] = "FWD SHOOT";
        CLAW_POT_NAMES[BACKWARD_SHOOT] = "BCK SHOOT";
        CLAW_POT_NAMES[PICKUP] = "PICKUP";
        CLAW_POT_NAMES[GOAL_SHOOT] = "GOAL SHOOT";
        CLAW_POT_NAMES[STORE] = "STORE";
    }
    private final PIDController649 clawPID;
    private final SpeedController motor;
    private final AnalogPotentiometer potentiometer;

    // Initialize your subsystem here
    public ClawPivotSubsystem() {
        super("ClawSubsystem");
        motor = new Victor(RobotMap.CLAW_PIVOT.MOTOR);
        potentiometer = new AnalogPotentiometer(RobotMap.CLAW_PIVOT.POTENTIOMETER);
        clawPID = new PIDController649(kP, kI, kD, potentiometer, this);
        clawPID.setAbsoluteTolerance(0.01);
        clawPID.setOutputRange(MAX_FORWARD_SPEED, MAX_BACKWARD_SPEED);
    }

    protected void initDefaultCommand() {
    }

    public PIDController649 getClawPID() {
        return clawPID;
    }

    public void setPower(double power) {
        if (power < 0 && getPotValue() >= FULL_FORWARD_POSITION || power > 0 && getPotValue() < FULL_BACKWARD_POSITION || Math.abs(power) < .1) {
            power = 0;
        }
        motor.set(power);

    }

    public double getPotValue() {
        return potentiometer.pidGet();
    }

    public String getPotStateName() {
        double val = getPotValue();
        for (int i = 0; i < CLAW_POT_STATES.length; i++) {
            double d = CLAW_POT_STATES[i];
            if (Math.abs(val - d) < 0.075) {
                return CLAW_POT_NAMES[i];
            }
        }
        return "NO STATE";
    }

    public void pidWrite(double d) {
        setPower(d);
    }
}
