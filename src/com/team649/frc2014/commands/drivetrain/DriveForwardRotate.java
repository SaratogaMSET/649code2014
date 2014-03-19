/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.drivetrain;

import com.team649.frc2014.commands.CommandBase;

/**
 *
 * @author Alex
 */
public class DriveForwardRotate extends CommandBase {

    private final double driveForward;
    private final double driveRotation;

    public DriveForwardRotate(double driveForward, double driveRotation) {
        this.driveForward = driveForward;
        this.driveRotation = driveRotation;
    }

    protected void initialize() {
        driveTrainSubsystem.driveFwdRot(driveForward, driveRotation);
    }

    protected void execute() {
    }

    protected boolean isFinished() {
        return true;
    }

    protected void end() {
    }

    protected void interrupted() {
    }
}
