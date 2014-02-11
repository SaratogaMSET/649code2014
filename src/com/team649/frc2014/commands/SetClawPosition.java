/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands;

import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.subsystems.ClawSubsystem;

/**
 *
 * @author Kabi
 */
public class SetClawPosition extends CommandBase {

    private final PIDController649 clawPID;
    private final int state;

    public SetClawPosition(int state) {
        clawPID = clawSubsystem.getClawPID();
        requires(clawSubsystem);
        this.state = state;
    }

    protected void initialize() {
        clawPID.enable();
        clawPID.setSetpoint(ClawSubsystem.clawState[state]);

    }

    protected void execute() {
    }

    protected boolean isFinished() {
        //TODO make sure things get finished (time to be on target)
        return clawPID.onTarget();
    }

    protected void end() {
        killCommand();
    }

    protected void interrupted() {
        killCommand();
    }

    private void killCommand() {
        try {
            clawPID.disable();
        } catch (NullPointerException e) {
        }
        clawSubsystem.setPower(0);
    }

    public int getState() {
       return state;
    }
}
