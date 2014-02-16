/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.winch;

import com.team649.frc2014.commands.CommandBase;

/**
 *
 * @author Suneel
 */
public class RunClawWinchMotor extends CommandBase {

    public RunClawWinchMotor() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(winchSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        winchSubsystem.runMotor();
    }

    // Called repeatedly when this Command is scheduled to run
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