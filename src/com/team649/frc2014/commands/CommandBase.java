package com.team649.frc2014.commands;

import com.sun.squawk.debugger.sda.SDA;
import com.team649.frc2014.OI;
import com.team649.frc2014.RobotMap;
//Why are we importing something from this class into this class
//import com.team649.frc2014.commands.CommandBase.shootBall;
import com.team649.frc2014.commands.drivetrain.DriveForwardRotate;
import com.team649.frc2014.commands.drivetrain.DriveSetDistanceWithPIDCommand;
import com.team649.frc2014.commands.fingers.SetFingerPosition;
import com.team649.frc2014.commands.pivot.ManualDriveClawPivot;
import com.team649.frc2014.commands.pivot.SetClawPosition;
import com.team649.frc2014.commands.rollers.RunRollers;
import com.team649.frc2014.commands.winch.AutoCoilClawWinch;
import com.team649.frc2014.commands.winch.ManualCoilClawWinch;
import com.team649.frc2014.commands.winch.SetClawWinchSolenoid;
import com.team649.frc2014.subsystems.CameraSubsystem;
import com.team649.frc2014.subsystems.ClawFingerSubsystem;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import com.team649.frc2014.subsystems.ClawRollerSubsystem;
import com.team649.frc2014.subsystems.ClawWinchSubsystem;
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The base for all commands. All atomic commands should subclass CommandBase. CommandBase stores creates and stores each control system. To access a subsystem elsewhere in your code in your code use
 * CommandBase.exampleSubsystem
 *
 * @author Author
 */

public abstract class CommandBase extends Command {

    public static OI oi;
    // Create a single static instance of all of your subsystems
    public static DriveTrainSubsystem driveTrainSubsystem = new DriveTrainSubsystem();
    public static CameraSubsystem cameraSubsystem = new CameraSubsystem();
    public static ClawPivotSubsystem clawPivotSubsystem = new ClawPivotSubsystem();
    public static ClawWinchSubsystem clawWinchSubsystem = new ClawWinchSubsystem();
    public static ClawFingerSubsystem clawFingerSubsystem = new ClawFingerSubsystem();
    public static ClawRollerSubsystem clawRollerSubsystem = new ClawRollerSubsystem();
    private static Compressor compressor;

    public static void init() {
        compressor = new Compressor(RobotMap.PRESSURE_SWITCH_CHANNEL, RobotMap.COMPRESSOR_RELAY_CHANNEL);
        compressor.start();
        oi = new OI();
    }

    public static Command driveForwardRotate(double driveForward, double driveRotation) {
        return new DriveForwardRotate(driveForward, driveRotation);
    }

    public static Command manualCoilClawWinch() {
        return new ManualCoilClawWinch();
    }

    public static Command autoCoilClawWinch() {
        return new AutoCoilClawWinch();
    }

    public static boolean isCompressorRunning() {
        return !compressor.getPressureSwitchValue();
    }

    public CommandBase(String name) {
        super(name);
    }

    public CommandBase() {
        super();

    }
    public static Command shootHotGoalShortDriveAutonomous() {
        CommandGroup driveAndCheckGoal = driveAndPrepareToShoot(true, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT, true, 2.5);
        CommandGroup mainAutonomousSequence = new CommandGroup("mainAutoSeq");
        //drive and check goal. When both are done (checking goal and driving), shoot
        mainAutonomousSequence.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
        mainAutonomousSequence.addSequential(new SetClawWinchSolenoid(true));
        mainAutonomousSequence.addSequential(driveAndCheckGoal);
        mainAutonomousSequence.addSequential(shootBall());
        return mainAutonomousSequence;
    }

   public static Command twoBallShortDriveAutonomous() {
        CommandGroup mainAutonomousSequence = new CommandGroup("mainAutoSeq");
        mainAutonomousSequence.addSequential(driveAndPrepareToShoot(false, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT, true, 1.6));
        mainAutonomousSequence.addSequential(shootBall(false));
        mainAutonomousSequence.addParallel(autoCoilClawWinch(), ClawWinchSubsystem.MAX_COIL_TIME);
        mainAutonomousSequence.addSequential(repositionAndPickup(DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT));
        mainAutonomousSequence.addParallel(realignBall());
        mainAutonomousSequence.addSequential(driveAndPrepareToShoot(false, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT - 12, true, 10));
        mainAutonomousSequence.addSequential(shootBall());
        return mainAutonomousSequence;
    }

    private static CommandGroup repositionAndPickup(double driveDistance) {
        CommandGroup repositionAndPickup = new CommandGroup();
        //Extra 12 inches might be unnesscary, check that
        repositionAndPickup.addParallel(new SetClawPosition(ClawPivotSubsystem.PICKUP));
        repositionAndPickup.addSequential(new WaitCommand(300));

        repositionAndPickup.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_INTAKE_SPEED));
        repositionAndPickup.addSequential(new DriveSetDistanceWithPIDCommand(-driveDistance + 24, 0.33));

        return repositionAndPickup;
    }
    private static CommandGroup realignBall() {
        CommandGroup realign = new CommandGroup();
        realign.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_REALIGN_SPEED));
        realign.addSequential(new WaitCommand(2000));
        realign.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_OFF_SPEED));
        return realign;
    }
    
    private static CommandGroup driveAndPrepareToShoot(boolean checkHot, double driveDistance, boolean keepPidRunning, double timeout) {
        CommandGroup driveAndCheckGoal = new CommandGroup("driveAndCheck");
        
        driveAndCheckGoal.addParallel(setFingerPosition(ClawFingerSubsystem.DOWN));
        driveAndCheckGoal.addParallel(new SetClawWinchSolenoid(true));
        
//        check the hot goal after .5 seconds
        if (checkHot) {
            CommandGroup checkHotGoal = new CommandGroup("checkHotGoal");
            checkHotGoal.addSequential(new WaitCommand(1000));
            checkHotGoal.addSequential(new HotVisionWaitCommand());
            driveAndCheckGoal.addSequential(checkHotGoal);
        }
        final double minDriveSpeed = .7;
        
        if (!keepPidRunning) {
            driveAndCheckGoal.addParallel(new DriveSetDistanceWithPIDCommand(driveDistance, minDriveSpeed));
            driveAndCheckGoal.addParallel(new SetClawPosition(ClawPivotSubsystem.BACKWARD_SHOOT));
        } else {
            ChangeableBoolean driveFinishedChecker = new ChangeableBoolean(false);
            driveAndCheckGoal.addParallel(new DriveSetDistanceWithPIDCommand(driveDistance, minDriveSpeed, driveFinishedChecker));
            driveAndCheckGoal.addParallel(new SetClawPosition(ClawPivotSubsystem.BACKWARD_SHOOT, driveFinishedChecker), timeout);
        }
        return driveAndCheckGoal;
    }

//    private static CommandGroup driveAndPrepareToShoot(boolean checkHot, double driveDistance) {
//        return driveAndPrepareToShoot(checkHot, driveDistance, false);
//    }
//
//    private static CommandGroup driveAndFireBackward(boolean checkHot, double driveDistance) {
//        CommandGroup driveAndCheckGoal = new CommandGroup("driveAndCheck");
//        //        check the hot goal after 1 second
//        if (checkHot) {
//            CommandGroup checkHotGoal = new CommandGroup("checkHotGoal");
//            checkHotGoal.addSequential(new WaitCommand(1000));
//            checkHotGoal.addSequential(new HotVisionWaitCommand());
//            driveAndCheckGoal.addSequential(checkHotGoal);
//        }
//        driveAndCheckGoal.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
//        driveAndCheckGoal.addParallel(new SetClawWinchSolenoid(true));
//        driveAndCheckGoal.addSequential(new SetClawPosition(ClawPivotSubsystem.BACKWARD_SHOOT));
//        driveAndCheckGoal.addParallel(new DriveSetDistanceWithPIDCommand(driveDistance, DriveTrainSubsystem.EncoderBasedDriving.DRIVING_SHOT_MOTOR_POWER));
//        driveAndCheckGoal.addSequential(new WaitCommand((int) SmartDashboard.getNumber("waitTime")));
//        //as soon as reaches forward position, fire
//        driveAndCheckGoal.addSequential(shootBall());
//
//        return driveAndCheckGoal;
//    }

    public static Command waitAndDriveAutonomous() {
        CommandGroup group = new CommandGroup("waitAndDrive");
//        group.addSequential(new WaitCommand(5000));
//        group.addSequential(new DriveSetDistanceByTimeCommand(DriveTrainSubsystem.TimeBasedDriving.DRIVE_SPEED, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE));

        group.addSequential(new DriveSetDistanceWithPIDCommand(DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_LONG));
        group.addSequential(new DriveSetDistanceWithPIDCommand(-DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_LONG));
        return group;
    }

    public static CommandGroup doNothingAutonomous() {
        return new CommandGroup();
    }

    public static Command shootBall(boolean auto) {
        CommandGroup fireSequence = new CommandGroup();
        fireSequence.addParallel(setFingerPosition(ClawFingerSubsystem.UP));
        fireSequence.addParallel(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_SHOOT_SPEED));
        fireSequence.addSequential(new SetClawWinchSolenoid(false));
        fireSequence.addSequential(new WaitCommand(ClawWinchSubsystem.TIME_TO_FIRE));
        //then recoils
        fireSequence.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
        fireSequence.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_OFF_SPEED));
        if (auto) {
            fireSequence.addSequential(autoCoilClawWinch(), ClawWinchSubsystem.MAX_COIL_TIME);
        }
        return fireSequence;

    }

    public static Command shootBall() {
        return shootBall(true);
    }

    public static Command setFingerPosition(int state) {
        return new SetFingerPosition(state);
    }

    public static Command runRollers(double choosenSpeed) {
        return new RunRollers(choosenSpeed);
    }

    public static Command manualDriveClaw(double power) {
        return new ManualDriveClawPivot(power);
    }
    
//    
//     
//    public static Command shootHotGoalDrivingFireAutonomous() {
//        //CommandGroup driveAndCheckGoal = driveAndPrepareToShoot(true, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_LONG);
//        CommandGroup mainAutonomousSequence = new CommandGroup("mainAutoSeq");
//        //drive and check goal. When both are done (checking goal and driving), shoot
//        mainAutonomousSequence.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
//        mainAutonomousSequence.addSequential(new SetClawWinchSolenoid(true));
//
//        mainAutonomousSequence.addSequential(driveAndFireBackward(true, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT));
//        return mainAutonomousSequence;
//    }
//    public static Command twoBallDrivingFireAutonomous() {
//        CommandGroup mainAutonomousSequence = new CommandGroup("mainAutoSeq");
//        mainAutonomousSequence.addSequential(driveAndFireBackward(false, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT));
//        mainAutonomousSequence.addSequential(repositionAndPickup(DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT));
//        //next two commands may be unnesscary with fixed gearbox
//        mainAutonomousSequence.addSequential(new DriveSetDistanceWithPIDCommand(24));
//        mainAutonomousSequence.addSequential(new DriveSetDistanceWithPIDCommand(-12));
//        mainAutonomousSequence.addSequential(driveAndFireBackward(false, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE_SHORT));
//        mainAutonomousSequence.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_OFF_SPEED));
//        return mainAutonomousSequence;
//    }
//  

}

