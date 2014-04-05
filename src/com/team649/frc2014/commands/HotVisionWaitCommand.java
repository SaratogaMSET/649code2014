/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands;

import com.team649.frc2014.Display;
import com.team649.frc2014.autonomous.HotTargetVision;
import edu.wpi.first.wpilibj.DriverStation;
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
public class HotVisionWaitCommand extends Command {

    private boolean hotGoalDetected;

    public HotVisionWaitCommand() {
        hotGoalDetected = false;
    }

    protected void initialize() {
        hotGoalDetected = HotTargetVision.detectHotGoal();
        Display.printToOutputStream("hot goal at " + DriverStation.getInstance().getMatchTime() + ": " + hotGoalDetected);
    }

    protected boolean isFinished() {
        return hotGoalDetected || DriverStation.getInstance().getMatchTime() > 4.8;
    }

    protected void execute() {
    }

    protected void end() {
    }

    protected void interrupted() {
    }

}
