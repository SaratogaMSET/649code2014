/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.rollers;

import com.team649.frc2014.commands.CommandBase;

/**
 *
 * @author Kabi
 */
public class RunRollers extends CommandBase {

    private final double speed;

    public RunRollers(double choosenSpeed) {
        speed = choosenSpeed;
    }

    protected void initialize() {
        clawRollerSubsystem.runMotor(speed);
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
