/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.winch;

import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.subsystems.ClawFingerSubsystem;
import com.team649.frc2014.subsystems.ClawWinchSubsystem;

/**
 *
 * @author Suneel
 */
public class CoilClawWinch extends CommandBase {
long startTime;

    // Called just before this Command runs the first time
    protected void initialize() {
        if (!winchSubsystem.isSwitchPressed()) {
            winchSubsystem.runMotor();
        }
        else
            winchSubsystem.stopMotor();
        startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (System.currentTimeMillis()- startTime > ClawWinchSubsystem.TIME_TO_ENGAGE_SOLENOID)
            new SetClawWinchSolenoid(true).start();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !oi.shooter.isWinchWindButtonPressed()||winchSubsystem.isSwitchPressed();
    }

    // Called once after isFinished returns true
    protected void end() {
        winchSubsystem.stopMotor();
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run

    protected void interrupted() {
        winchSubsystem.stopMotor();
    }
}
