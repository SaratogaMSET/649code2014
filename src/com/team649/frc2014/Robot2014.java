/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.team649.frc2014;

import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.commands.drivetrain.DriveSetDistanceByTimeCommand;
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
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot2014 extends IterativeRobot {

    public static final String DO_NOTHING_AUTO_NAME = "doNothingAuto";
    public static final String WAIT_AND_DRIVE_AUTO_NAME = "waitAndDriveAuto";
    public static final String DRIVE_AND_SHOOT_AUTO_NAME = "driveAndShootAuto";
    private SendableChooser autonomousModeChooser;
    private SetClawPosition setClawPositionCommand;
    private Command shootCommand;
    private Command autonomousCommand;
    private Command coilClawWinchCommand;

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
        autonomousModeChooser = new SendableChooser();
        autonomousModeChooser.addDefault("Drive, Check Hot Goal, and Shoot Autonomous", DRIVE_AND_SHOOT_AUTO_NAME);
        autonomousModeChooser.addObject("Wait and Drive Autonomous", WAIT_AND_DRIVE_AUTO_NAME);
        autonomousModeChooser.addObject("Do Nothing Autonomous", DO_NOTHING_AUTO_NAME);
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
        Display.marquee(2, "WOOOOOO", 0, 10, true);
        Display.marquee(3, "GO FIISHH", 0, 2, true);
        Display.marquee(4, "YEEAAHHHH", 0, 7, true);
        Display.marquee(5, "AUTONOMOOSE MODE", 2, 5, true);
        Display.marquee(6, "YOU CAN DO IT!!!!", 5, 5, true);
        final String selectedAuto = (String) autonomousModeChooser.getSelected();
        Display.printToOutputStream("selected auto: " + selectedAuto);
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        if (selectedAuto.equals(DRIVE_AND_SHOOT_AUTO_NAME)) {
            autonomousCommand = CommandBase.shootHotGoalAutonomous();
        } else if (selectedAuto.equals(WAIT_AND_DRIVE_AUTO_NAME)) {
            autonomousCommand = CommandBase.waitAndDriveAutonomous();
        } else if (selectedAuto.equals(DO_NOTHING_AUTO_NAME)) {
            autonomousCommand = CommandBase.doNothingAutonomous();
        } else {
            autonomousCommand = CommandBase.shootHotGoalAutonomous();
        }
        autonomousCommand.start();
        setSolenoidsToDefault();
    }

    /*
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Display.clear();
        Scheduler.getInstance().run();
        Display.queue(CommandBase.clawPivotSubsystem.getPotValue() + "");
//        CommandBase.driveTrainSubsystem.printEncoders();
        Display.update();
    }

    public void teleopInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        if (setClawPositionCommand != null) {
            setClawPositionCommand.cancel();
        }
        Display.clearMarquees();
        CommandBase.clawPivotSubsystem.setState(ClawPivotSubsystem.NO_STATE);
        CommandBase.driveTrainSubsystem.startEncoders();
//        Display.marquee(1, "2014 ENABLED", 5, 5, true);
        setSolenoidsToDefault();
    }

    private void setSolenoidsToDefault() {
        CommandBase.setFingerPosition(ClawFingerSubsystem.DOWN).start();
        new SetClawWinchSolenoid(true).start();
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
//            CommandBase.driveTrainSubsystem.resetEncoders();
        } else {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.HIGH_SPEED);
        }
//        CommandBase.driveTrainSubsystem.printEncoders();
//        if (CommandBase.oi.shooter.isCatchClawPositionButtonPressed()) {
//            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.CATCH) {
//                setClawPosition.cancel();
//            }
//            setClawPosition = new SetClawPosition(ClawPivotSubsystem.CATCH);
//            setClawPosition.start();
//
//        } else 
        if (CommandBase.oi.shooter.isShootClawPositionButtonPressed()) {
//            if (setClawPositionCommand != null && setClawPositionCommand.getState() != ClawPivotSubsystem.SHOOT) {
//                setClawPositionCommand.cancel();
//            }
            if (setClawPositionCommand != null && !setClawPositionCommand.isRunning()) {
                setClawPositionCommand = new SetClawPosition(ClawPivotSubsystem.SHOOT);
                setClawPositionCommand.start();
            }

        } // If joystick button for pickup state is set then change to pickup state (if appropriate) 
        //        //also change finger state to appropriate level
        //        else if (CommandBase.oi.shooter.isPickupClawPositionButtonPressed()) {
        //            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.PICKUP) {
        //                setClawPosition.cancel();
        //            }
        //            setClawPosition = new SetClawPosition(ClawPivotSubsystem.PICKUP);
        //            setClawPosition.start();
        //        } else 
        else if (CommandBase.oi.shooter.isPivotManualOverrideButtonPressed()) {
            if (setClawPositionCommand != null && setClawPositionCommand.isRunning()) {
                setClawPositionCommand.cancel();
            }
            CommandBase.manualDriveClaw(CommandBase.oi.shooter.getShooterJoystickY()).start();
        } else if (setClawPositionCommand == null || !setClawPositionCommand.isRunning()) {
            CommandBase.manualDriveClaw(0).start();
        }

        if (CommandBase.oi.shooter.isPurgeButtonPressed()) {
            CommandBase.runRollers(ClawRollerSubsystem.FORWARD).start();
        } else if (CommandBase.oi.shooter.isPickupButtonPressed()) {
            CommandBase.runRollers(ClawRollerSubsystem.REVERSE).start();
        } else {
            CommandBase.runRollers(ClawRollerSubsystem.OFF).start();
        }
        if (CommandBase.oi.shooter.isWinchWindButtonPressed() && (coilClawWinchCommand == null || !coilClawWinchCommand.isRunning())) {
            coilClawWinchCommand = CommandBase.coilClawWinch();
            coilClawWinchCommand.start();
        }

        if (CommandBase.oi.shooter.isShooterTriggerButtonPressed() && CommandBase.oi.shooter.isWinchSafetyButtonPressed()) {
//                && CommandBase.clawPivotSubsystem.getState() == ClawPivotSubsystem.SHOOT) {
            if (shootCommand == null || !shootCommand.isRunning()) {
                shootCommand = CommandBase.shootBall();
                shootCommand.start();
            }
        }
//        CommandBase.driveTrainSubsystem.printEncoders();
        Display.queue("WINCH: " + (CommandBase.clawWinchSubsystem.isSwitchPressed() ? "CHARGED" : "UNWOUND"));
        Display.queue("POT: " + CommandBase.clawPivotSubsystem.getPotValue());
        Display.queue(CommandBase.clawPivotSubsystem.getPotStateName());
        if (CommandBase.isCompressorRunning()) {
            Display.queue("COMPRESSOR RUNNING");
        }
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
