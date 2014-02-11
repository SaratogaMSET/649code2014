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
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
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

    private int lastButton;
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
        Display.clearMarquees();
        Display.marquee(1, "DISABLED MODE", 0, 5, true);
        CommandBase.driveTrainSubsystem.disablePid();
    }

    public void disabledPeriodic() {
        Display.clear();
        Display.update();
    }

    public void autonomousInit() {
        Display.clearMarquees();
        Display.marquee(1, "AUTONOMOUS MODE", 0, 5, true);
        System.out.println(autonomousModeChooser.getSelected());
        CommandBase.shootHotGoalAutonomous().start();
        // schedule the autonomous command (example)
//        autonomousCommand.start();
//        supaHotFire = new SupaHotFire();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Display.clear();
        Scheduler.getInstance().run();
        Display.update();
    }

    public void teleopInit() {
        Display.clearMarquees();
        CommandBase.driveTrainSubsystem.startEncoders();
//        Display.marquee(1, "2014 ENABLED", 5, 5, true);
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        getWatchdog().feed();
        Display.clear();
        Scheduler.getInstance().run();
//        Display.println(0, "1/18/2014;2:25");
//        Display.println(1, "rate: " + CommandBase.driveTrainSubsystem.getRate());
//        Display.println(2, "pos: " + CommandBase.driveTrainSubsystem.pidGet());

        CommandBase.driveTrainSubsystem.driveFwdRot(CommandBase.oi.getDriveForward(), CommandBase.oi.getDriveRotation());
        if (CommandBase.oi.getTrigger()) {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.LOW_SPEED);
        } else {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.HIGH_SPEED);
        }
        CommandBase.driveTrainSubsystem.printEncoders();
//        Display.println(2, "dis: " + CommandBase.driveTrainSubsystem.getDistance());
//        Display.println(4, "spd: " + CommandBase.driveTrainSubsystem.getRate());
//        Display.println(3, "pos: " + CommandBase.driveTrainSubsystem.getPosition());
        
        Display.update();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
