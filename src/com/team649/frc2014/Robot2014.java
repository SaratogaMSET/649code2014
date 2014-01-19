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
import com.team649.frc2014.commands.GetAllDriveSpeedCommands;
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Dashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
    private long lastTime;
    private long period;
    private int lastButton;
    private GetAllDriveSpeedCommands getAllDriveSpeedCommands;
    private SendableChooser autonomousModeChooser;

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
        SmartDashboard.putNumber("accelconst", DriveTrainSubsystem.ACCELERATION);
        SmartDashboard.putNumber("period", 100);
        SmartDashboard.putNumber("numPoints", 10);
        SmartDashboard.putNumber("linrega", 130);
        SmartDashboard.putNumber("linregb", 62);
        SmartDashboard.putNumber("minoutput", .15);
        SmartDashboard.putNumber("speed", DriveTrainSubsystem.DRIVE_SPEED);
        SmartDashboard.putNumber("distance", 300);
        autonomousModeChooser = new SendableChooser();
        autonomousModeChooser.addObject("a", "aob");
        autonomousModeChooser.addObject("b", "bob");
        autonomousModeChooser.addObject("c", "cob");
        autonomousModeChooser.addObject("d", "dob");
        autonomousModeChooser.addObject("e", "eob");
        SmartDashboard.putData("Autonomous", autonomousModeChooser);
    }

    public void disabledInit() {
        CommandBase.driveTrainSubsystem.disablePid();
    }

    public void autonomousInit() {

        System.out.println(autonomousModeChooser.getSelected());
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
        startDriveSetDistanceCommand();
        getAllDriveSpeedCommands = new GetAllDriveSpeedCommands(-0.2, -1, -.1, 2);
        cancelled = false;
        lastTime = 0;
        period = 0;
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        getWatchdog().feed();
        DisplayLCD.clear();
        Scheduler.getInstance().run();
        if (System.currentTimeMillis() > lastTime + period) {
            lastTime = System.currentTimeMillis();
            period = CommandBase.driveTrainSubsystem.updateAccel();
        }
        DisplayLCD.println(0, "1/18/2014;2:25");
        DisplayLCD.println(1, "rate: " + CommandBase.driveTrainSubsystem.getRate());
        DisplayLCD.println(2, "pos: " + CommandBase.driveTrainSubsystem.pidGet());

        if (lastButton != 3 && CommandBase.oi.getButton(3)) {
            lastButton = 3;
            driveSetDistanceCommand.cancel();
            getAllDriveSpeedCommands.start();
        } else if (lastButton != 4 && CommandBase.oi.getButton(4)) {
            lastButton = 4;
            getAllDriveSpeedCommands.cancel();
            driveSetDistanceCommand.start();
        } else if (CommandBase.oi.getTrigger()) {
            cancelled = true;
            driveSetDistanceCommand.cancel();
            CommandBase.driveTrainSubsystem.driveFwdRot(CommandBase.oi.getForward(), 0);
            System.out.println(CommandBase.oi.getForward() + ": " + CommandBase.driveTrainSubsystem.getRate());
        } else if (cancelled) {
//            CommandBase.driveTrainSubsystem.rawDrive(0, 0);
        }
        if (!cancelled && !driveSetDistanceCommand.isRunning()) {
            System.out.println(DriverStation.getInstance().getBatteryVoltage() + ": " + (CommandBase.driveTrainSubsystem.pidGet() - 300));
            startDriveSetDistanceCommand();
        }
        DisplayLCD.update();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }

    private void startDriveSetDistanceCommand() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
//        autonomousCommand.cancel();
        driveSetDistanceCommand = new DriveSetDistanceCommand(SmartDashboard.getNumber("speed"), SmartDashboard.getNumber("distance"));
        driveSetDistanceCommand.start();
    }
}
