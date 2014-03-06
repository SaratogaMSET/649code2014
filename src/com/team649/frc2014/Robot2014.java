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
import com.team649.frc2014.commands.drivetrain.DriveSetDistanceCommand;
import com.team649.frc2014.commands.drivetrain.GetAllDriveSpeedCommands;
import com.team649.frc2014.commands.pivot.SetClawPosition;
import com.team649.frc2014.commands.rollers.RunRollers;
import com.team649.frc2014.commands.winch.CoilClawWinch;
import com.team649.frc2014.commands.winch.SetClawWinchSolenoid;
import com.team649.frc2014.subsystems.ClawFingerSubsystem;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import com.team649.frc2014.subsystems.ClawRollerSubsystem;
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.WaitCommand;
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

    private SendableChooser autonomousModeChooser;
    private SetClawPosition setClawPosition;
    private Command shootCommand;
    private Command autonomousCommand;
    private Command coilClawWinch;

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
        SmartDashboard.putNumber("pickup", .8);
        SmartDashboard.putNumber("shoot", 2.35);
        SmartDashboard.putNumber("catch", 3);
        autonomousModeChooser = new SendableChooser();
        autonomousModeChooser.addObject("a", "aob");
        autonomousModeChooser.addObject("b", "bob");
        autonomousModeChooser.addObject("c", "cob");
        autonomousModeChooser.addObject("d", "dob");
        autonomousModeChooser.addObject("e", "eob");
        SmartDashboard.putData("Autonomous", autonomousModeChooser);
        SmartDashboard.putNumber("p", .3);
        SmartDashboard.putNumber("i", .03);
        SmartDashboard.putNumber("d", .00);
        SmartDashboard.putBoolean("skipHot", false);
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
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        System.out.println("auto init");
        autonomousCommand = CommandBase.shootHotGoalAutonomous();
        autonomousCommand.start();

    }

    /*
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Display.clear();
        Scheduler.getInstance().run();
        Display.queue(CommandBase.clawPivotSubsystem.getPotValue()+"");
        CommandBase.driveTrainSubsystem.printEncoders();
        Display.update();
    }

    public void teleopInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        Display.clearMarquees();
        CommandBase.clawPivotSubsystem.setState(ClawPivotSubsystem.NO_STATE);
        CommandBase.driveTrainSubsystem.startEncoders();
        Display.marquee(1, "2014 ENABLED", 5, 5, true);
        CommandBase.setFingerPosition(ClawFingerSubsystem.DOWN).start();
        new SetClawWinchSolenoid(true).start();
        if (setClawPosition != null) {
            setClawPosition.cancel();
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        getWatchdog().feed();
        Display.clear();
        Scheduler.getInstance().run();

        CommandBase.driveForwardRotate(CommandBase.oi.driver.getDriveForward(), CommandBase.oi.driver.getDriveRotation()).start();
        if (CommandBase.oi.driver.isDrivetrainLowGearButtonPressed()) {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.LOW_SPEED);
        } else {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.HIGH_SPEED);
        }

//        if (CommandBase.oi.shooter.isCatchClawPositionButtonPressed()) {
//            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.CATCH) {
//                setClawPosition.cancel();
//            }
//            setClawPosition = new SetClawPosition(ClawPivotSubsystem.CATCH);
//            setClawPosition.start();
//
//        } else if (CommandBase.oi.shooter.isShootClawPositionButtonPressed()) {
//            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.SHOOT) {
//                setClawPosition.cancel();
//            }
//            setClawPosition = new SetClawPosition(ClawPivotSubsystem.SHOOT);
//            setClawPosition.start();
//
//        } // If joystick button for pickup state is set then change to pickup state (if appropriate) 
//        //also change finger state to appropriate level
//        else if (CommandBase.oi.shooter.isPickupClawPositionButtonPressed()) {
//            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.PICKUP) {
//                setClawPosition.cancel();
//            }
//            setClawPosition = new SetClawPosition(ClawPivotSubsystem.PICKUP);
//            setClawPosition.start();
//        } else 
        if (CommandBase.oi.shooter.isPivotManualOverrideButtonPressed()) {
            if (setClawPosition != null) {
                setClawPosition.cancel();
            }
            CommandBase.manualDriveClaw(CommandBase.oi.shooter.getShooterJoystickY()).start();
        } else if (setClawPosition == null || !setClawPosition.isRunning()) {
            CommandBase.manualDriveClaw(0).start();
        }

        if (CommandBase.oi.shooter.isPurgeButtonPressed()) {
            CommandBase.runRollers(ClawRollerSubsystem.FORWARD).start();
        } else if (CommandBase.oi.shooter.isPickupButtonPressed()) {
            CommandBase.runRollers(ClawRollerSubsystem.REVERSE).start();
        } else {
            CommandBase.runRollers(ClawRollerSubsystem.OFF).start();
        }
        if (CommandBase.oi.shooter.isWinchWindButtonPressed() && (coilClawWinch == null || !coilClawWinch.isRunning())) {
            coilClawWinch = CommandBase.coilClawWinch();
            coilClawWinch.start();
        }

        if (CommandBase.oi.shooter.isShooterTriggerButtonPressed() && CommandBase.oi.shooter.isWinchSafetyButtonPressed()) {
//                && CommandBase.clawPivotSubsystem.getState() == ClawPivotSubsystem.SHOOT) {
            if (shootCommand == null || !shootCommand.isRunning()) {
                shootCommand = CommandBase.shootBall();
                shootCommand.start();
            }
        }
//        CommandBase.driveTrainSubsystem.printEncoders();
        Display.queue(CommandBase.clawPivotSubsystem.getPotValue() + "");
        Display.queue(CommandBase.winchSubsystem.isSwitchPressed() + "");
        Display.update();

        sleep();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
