/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.pivot;

import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import com.team649.frc2014.subsystems.ClawWinchSubsystem;

/**
 *
 * @author Kabi
 */
public class SetClawPosition extends CommandBase {

    private final PIDController649 clawPID;
    private final int state;

    public SetClawPosition(int state) {
        clawPID = clawPivotSubsystem.getClawPID();
        requires(clawPivotSubsystem);
        this.state = state;
    }

    protected void initialize() {
        clawPID.enable();
        clawPID.setSetpoint(ClawPivotSubsystem.CLAW_POT_STATES[state]);

    }

    protected void execute() {
    }

    protected boolean isFinished() {
        //TODO make sure things get finished (time to be on target)
        return clawPID.onTarget();
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
            clawPID.disable();
        } catch (NullPointerException e) {
        }
        clawPivotSubsystem.setPower(0);
    }

    public int getState() {
        return state;
    }
}
