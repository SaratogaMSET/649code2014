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
import com.team649.frc2014.commands.fingers.SetFingerPosition;
import com.team649.frc2014.commands.pivot.SetClawPosition;
import com.team649.frc2014.commands.rollers.RunRollers;
import com.team649.frc2014.commands.winch.CoilClawWinch;
import com.team649.frc2014.subsystems.ClawFingerSubsystem;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import com.team649.frc2014.subsystems.ClawWinchSubsystem;
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
    private Command shoot;
    private WaitCommand waitCommand;
    private Command coil;

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

    /*
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Display.clear();
        Scheduler.getInstance().run();
        Display.update();
    }

    public void teleopInit() {
        Display.clearMarquees();
        CommandBase.clawSubsystem.setState(ClawPivotSubsystem.NO_STATE);
        CommandBase.driveTrainSubsystem.startEncoders();
//        Display.marquee(1, "2014 ENABLED", 5, 5, true);
        coil = CommandBase.coilShooter();
        coil.start();
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
        if (CommandBase.oi.getDrivetrainLowGearButtonPressed()) {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.LOW_SPEED);
        } else {
            CommandBase.driveTrainSubsystem.shiftDriveGear(DriveTrainSubsystem.HIGH_SPEED);
        }

        if (CommandBase.oi.isCatchClawPositionButtonPressed()) {
            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.CATCH) {
                setClawPosition.cancel();
            }
            setClawPosition = new SetClawPosition(ClawPivotSubsystem.CATCH);
            setClawPosition.start();
            if (CommandBase.clawFingerSubsystem.getFingerPosition() != ClawFingerSubsystem.UP) {
                CommandBase.setFingerPosition(ClawFingerSubsystem.UP).start();
            }
        }

        if (CommandBase.oi.isStoreClawPositionButtonPressed()) {
            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.STORE) {
                setClawPosition.cancel();
            }
            setClawPosition = new SetClawPosition(ClawPivotSubsystem.STORE);
            setClawPosition.start();
            if (CommandBase.clawFingerSubsystem.getFingerPosition() != ClawFingerSubsystem.DOWN) {
                CommandBase.setFingerPosition(ClawFingerSubsystem.DOWN).start();
            }

        }

        if (CommandBase.oi.isShootClawPositionButtonPressed()) {
            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.SHOOT) {
                setClawPosition.cancel();
            }
            setClawPosition = new SetClawPosition(ClawPivotSubsystem.SHOOT);
            setClawPosition.start();

            if (CommandBase.clawFingerSubsystem.getFingerPosition() != ClawFingerSubsystem.DOWN) {
                CommandBase.setFingerPosition(ClawFingerSubsystem.DOWN).start();
            }

        }
// If joystick button for pickup state is set then change to pickup state (if appropriate) 
        //also change finger state to appropriate level
        if (CommandBase.oi.isPickupClawPositionButtonPressed()) {
            if (setClawPosition != null && setClawPosition.getState() != ClawPivotSubsystem.PICKUP) {
                setClawPosition.cancel();
            }
        }
        setClawPosition = new SetClawPosition(ClawPivotSubsystem.PICKUP);
        setClawPosition.start();
        if (CommandBase.clawFingerSubsystem.getFingerPosition() != ClawFingerSubsystem.DOWN) {
            CommandBase.setFingerPosition(ClawFingerSubsystem.DOWN).start();

        }

        if (CommandBase.oi.getShooterTrigger() && CommandBase.clawSubsystem.getState() == ClawPivotSubsystem.SHOOT) {
            if (shoot == null || !shoot.isRunning()) {
                shoot = CommandBase.shootBall();
                shoot.start();
            }
        }

        if (CommandBase.oi.getCoilButton() && !CommandBase.winchSubsystem.isSwitchPressed()) {
            coil = CommandBase.coilShooter();
            coil.start();
        }

     
        if (CommandBase.oi.getPivotOverrideButton()) {
            if (setClawPosition != null) {
                setClawPosition.cancel();
            }
            CommandBase.clawSubsystem.setPower(CommandBase.oi.getShooterJoystick());
        }

        if (CommandBase.oi.isPurgeButtonPressed()) {
            new RunRollers(1).start();
        }

       else if (CommandBase.oi.isPickupButtonPressed()) {
            new RunRollers(-1).start();
        }
       else 
           new RunRollers(0).start();
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
