/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.fingers;

import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.subsystems.ClawFingerSubsystem;

/**
 *
 * @author Kabi
 */
public class SetFingerPosition extends CommandBase {

    private int fingerState;

    public SetFingerPosition(int state) {
        fingerState = state;
        requires(clawFingerSubsystem);
    }

    protected void initialize() {
        clawFingerSubsystem.setFingerPosition(fingerState);
    }

    protected void execute() {
    }
// Make this return true when this Command no longer needs to run execute()

    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
