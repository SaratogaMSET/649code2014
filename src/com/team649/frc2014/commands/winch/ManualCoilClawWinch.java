/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.winch;

import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.subsystems.ClawWinchSubsystem;

/**
 *
 * @author Suneel
 */
public class ManualCoilClawWinch extends CommandBase {

    long startTime;

    // Called just before this Command runs the first time
    protected void initialize() {
        if (!clawWinchSubsystem.isSwitchPressed() || (oi.shooter.isWinchSwitchOverrideButtonPressed() && oi.driver.isWinchSwitchOverrideButtonPressed())) {
            clawWinchSubsystem.runMotor();
        } else {
            clawWinchSubsystem.stopMotor();
        }
        startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (System.currentTimeMillis() - startTime > ClawWinchSubsystem.TIME_TO_ENGAGE_SOLENOID) {
            new SetClawWinchSolenoid(true).start();
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !oi.shooter.isWinchWindButtonPressed() || (clawWinchSubsystem.isSwitchPressed() && !(oi.shooter.isWinchSwitchOverrideButtonPressed() && oi.driver.isWinchSwitchOverrideButtonPressed()));
    }

    // Called once after isFinished returns true
    protected void end() {
        clawWinchSubsystem.stopMotor();
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run

    protected void interrupted() {
        clawWinchSubsystem.stopMotor();
    }
}
