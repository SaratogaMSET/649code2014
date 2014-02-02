/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands;

import autonomous.HotTargetVision;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 * @author JJ
 */
class VisionCommand extends Command {

    public VisionCommand() {
    }

    protected void initialize() {
    }

    protected void execute() {
        new HotTargetVision().getTargets();
    }

    protected boolean isFinished() {
        return true;
    }

    protected void end() {
    }

    protected void interrupted() {
    }
    
}
