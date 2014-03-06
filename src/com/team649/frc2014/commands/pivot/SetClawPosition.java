/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.pivot;

import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Kabi
 */
public class SetClawPosition extends CommandBase {

    private final PIDController649 clawPID;
    private final int state;
    private long startTime;

    public SetClawPosition(int state) {
        clawPID = clawPivotSubsystem.getClawPID();
        this.state = state;
    }

    protected void initialize() {
        clawPID.enable();
        final double setpoint = ClawPivotSubsystem.CLAW_POT_STATES[state];
        clawPID.setSetpoint(setpoint);
        clawPID.setPID(SmartDashboard.getNumber("p"), SmartDashboard.getNumber("i"), SmartDashboard.getNumber("d"));
        startTime = System.currentTimeMillis();
        System.out.println("s: " + setpoint + ", p: " + clawPID.getP() + ", i: " + clawPID.getI() + ", d: " + clawPID.getD());
    }

    protected void execute() {
    }

    protected boolean isFinished() {
        final long timeDiff = System.currentTimeMillis() - startTime;
        //TODO make sure things get finished (time to be on target)
        return clawPID.onTarget()&&timeDiff > 2000||timeDiff>5000;
    }

    protected void end() {
        killCommand();
        clawPivotSubsystem.setState(state);
    }

    protected void interrupted() {
        killCommand();
        clawPivotSubsystem.setState(ClawPivotSubsystem.NO_STATE);
    }

    private void killCommand() {
        try {
            System.out.println("end pivot: " + DriverStation.getInstance().getMatchTime());
            clawPID.disable();
        } catch (NullPointerException e) {
        }
        clawPivotSubsystem.setPower(0);
    }

    public int getState() {
        return state;
    }
}
