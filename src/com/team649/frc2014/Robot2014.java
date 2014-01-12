/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.team649.frc2014;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.commands.DriveSetDistanceCommand;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot2014 extends IterativeRobot {

    private DriveSetDistanceCommand driveSetDistanceCommand;
    private boolean cancelled;

//    Command autonomousCommand;
//    private SupaHotFire supaHotFire;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        // instantiate the command used for the autonomous period
//        autonomousCommand = new DriveSetDistanceCommand();

        // Initialize all subsystems
        CommandBase.init();
        SmartDashboard.putNumber("accelconst", 100);
        SmartDashboard.putNumber("p1", .05);
        SmartDashboard.putNumber("i1", 0);
        SmartDashboard.putNumber("d1", 0);
        SmartDashboard.putNumber("p2", .05);
        SmartDashboard.putNumber("i2", 0);
        SmartDashboard.putNumber("d2", 0);
        SmartDashboard.putNumber("numcounts", 10);
        SmartDashboard.putNumber("period", 100);
        SmartDashboard.putNumber("numPoints", 10);
        
    }

    public void disabledInit() {
        CommandBase.driveTrainSubsystem.disablePid();
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
//        autonomousCommand.start();
//        supaHotFire = new SupaHotFire();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

//        supaHotFire.getTargets();
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
//        autonomousCommand.cancel();
        driveSetDistanceCommand = new DriveSetDistanceCommand(DriverStation.getInstance().getAnalogIn(3)*100, DriverStation.getInstance().getAnalogIn(4)*100);

        driveSetDistanceCommand.start();
        cancelled = false;
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        DisplayLCD.clear();
        Scheduler.getInstance().run();
        DisplayLCD.println(0, "1/11/2014;2:20");
        DisplayLCD.println(1, "rate: " + CommandBase.driveTrainSubsystem.getRate());
        DisplayLCD.println(2, "pos: " + CommandBase.driveTrainSubsystem.pidGet());
        if (CommandBase.oi.getTrigger()) {
            cancelled = true;
            driveSetDistanceCommand.cancel();
            CommandBase.driveTrainSubsystem.driveFwdRot(CommandBase.oi.getForward(), CommandBase.oi.getRot());
        } else if (cancelled) {
            CommandBase.driveTrainSubsystem.rawDrive(0, 0);
        }
        DisplayLCD.update();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
