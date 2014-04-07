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
public class AutoCoilClawWinch extends CommandBase {

    long startTime;

    // Called just before this Command runs the first time
    protected void initialize() {
        if ((!clawWinchSubsystem.isSwitchPressed() || (oi.shooter.isWinchSwitchOverrideButtonPressed() && oi.driver.isWinchSwitchOverrideButtonPressed())) && CommandBase.oi.shooter.isAutoWinchOn()) {
            clawWinchSubsystem.runMotor();
        } else {
            clawWinchSubsystem.stopMotor();
        }
        startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (System.currentTimeMillis() - startTime > 50) {
            new SetClawWinchSolenoid(true).start();
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return (clawWinchSubsystem.isSwitchPressed() && !(oi.shooter.isWinchSwitchOverrideButtonPressed() && oi.driver.isWinchSwitchOverrideButtonPressed())) || !CommandBase.oi.shooter.isAutoWinchOn() || CommandBase.oi.shooter.isWinchWindButtonPressed();
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
