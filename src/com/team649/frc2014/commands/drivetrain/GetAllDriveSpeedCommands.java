/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 * @author Alex
 */
public class GetAllDriveSpeedCommands extends CommandGroup {

    public GetAllDriveSpeedCommands(double min, double max, double interval, int numTrials) {
        while (max > 0 ? (min < max + 0.001) : min > max - .001) {
            for (int i = 0; i < numTrials; i++) {
                addSequential(new GetDriveSpeedCommand(min));
            }
            min += interval;
        }
    }
}