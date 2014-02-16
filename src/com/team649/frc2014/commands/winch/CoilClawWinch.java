/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.winch;

import com.team649.frc2014.commands.CommandBase;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * @author Suneel
 */
public class CoilClawWinch extends CommandBase {

    public CoilClawWinch() {
        // Use requires() here to declare subsystem dependencies
        requires(winchSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (!winchSubsystem.isSwitchPressed()) {
            winchSubsystem.runMotor();
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return winchSubsystem.isSwitchPressed();
    }

    // Called once after isFinished returns true
    protected void end() {
        interrupted();
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run

    protected void interrupted() {
        winchSubsystem.stopMotor();
    }
}