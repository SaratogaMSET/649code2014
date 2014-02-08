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
/**
 * Detects if there's a hot goal. If there is, wait for the wait time. If there
 * isn't, finish immediately
 *
 * @author Alex
 */
class HotVisionWaitCommand extends WaitCommand {

    public HotVisionWaitCommand() {
        super(0);
    }

    protected void initialize() {
        setWaitTime(HotTargetVision.detectHotGoal() ? 5000 : 0);
        super.initialize();
    }
}
