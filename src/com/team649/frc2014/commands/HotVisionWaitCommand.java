/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands;

import autonomous.HotTargetVision;

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
    public static final int HOT_GOAL_WAIT_TIME = 0;
    public static final int COLD_GOAL_WAIT_TIME = 5000;

    public HotVisionWaitCommand() {
        super(0);
    }

    protected void initialize() {
        setWaitTime(HotTargetVision.detectHotGoal() ? HOT_GOAL_WAIT_TIME: COLD_GOAL_WAIT_TIME);
        super.initialize();
    }
}
