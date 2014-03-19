package com.team649.frc2014.commands;

import com.team649.frc2014.OI;
import com.team649.frc2014.RobotMap;
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

/**
 * The base for all commands. All atomic commands should subclass CommandBase.
 * CommandBase stores creates and stores each control system. To access a
 * subsystem elsewhere in your code in your code use
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

    public static Command shootHotGoalAutonomous() {
        CommandGroup driveAndCheckGoal = driveAndPrepareToShoot(false);

        CommandGroup mainAutonomousSequence = new CommandGroup("mainAutoSeq");
        //drive and check goal. When both are done (checking goal and driving), shoot
        mainAutonomousSequence.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
        mainAutonomousSequence.addSequential(new SetClawWinchSolenoid(true));
        mainAutonomousSequence.addSequential(driveAndCheckGoal);
        mainAutonomousSequence.addSequential(new WaitCommand(300));
        mainAutonomousSequence.addSequential(shootBall());
        return mainAutonomousSequence;
    }

    public static Command twoBallAutonomous() {
        CommandGroup mainAutonomousSequence = new CommandGroup("mainAutoSeq");
        //drive and check goal. When both are done (checking goal and driving), shoot
        mainAutonomousSequence.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
        mainAutonomousSequence.addSequential(new SetClawWinchSolenoid(true));
        mainAutonomousSequence.addSequential(driveAndPrepareToShoot(false));
        mainAutonomousSequence.addSequential(new WaitCommand(300));
        mainAutonomousSequence.addSequential(shootBall());
        CommandGroup repositionAndPickup = new CommandGroup();
        repositionAndPickup.addParallel(new DriveSetDistanceWithPIDCommand(-DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE - 12));
        repositionAndPickup.addParallel(new SetClawPosition(ClawPivotSubsystem.PICKUP));
        repositionAndPickup.addParallel(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_INTAKE_SPEED));
        mainAutonomousSequence.addParallel(autoCoilClawWinch());
        mainAutonomousSequence.addSequential(repositionAndPickup);
        mainAutonomousSequence.addSequential(new DriveSetDistanceWithPIDCommand(24));
        mainAutonomousSequence.addSequential(driveAndPrepareToShoot(false));
        mainAutonomousSequence.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_OFF_SPEED));
        mainAutonomousSequence.addSequential(new WaitCommand(300));
        mainAutonomousSequence.addSequential(shootBall());
        return mainAutonomousSequence;
    }

    private static CommandGroup driveAndPrepareToShoot(boolean checkHot) {
        CommandGroup driveAndCheckGoal = new CommandGroup("driveAndCheck");
        driveAndCheckGoal.addParallel(new DriveSetDistanceWithPIDCommand(DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE));
        driveAndCheckGoal.addParallel(new SetClawPosition(ClawPivotSubsystem.BACKWARD_SHOOT));
//        check the hot goal after .5 seconds
        if (checkHot) {
            CommandGroup checkHotGoal = new CommandGroup("checkHotGoal");
            checkHotGoal.addSequential(new WaitCommand(1000));
            checkHotGoal.addSequential(new HotVisionWaitCommand());
            driveAndCheckGoal.addSequential(checkHotGoal);
        }
        return driveAndCheckGoal;
    }

    public static Command waitAndDriveAutonomous() {
        CommandGroup group = new CommandGroup("waitAndDrive");
//        group.addSequential(new WaitCommand(5000));
//        group.addSequential(new DriveSetDistanceByTimeCommand(DriveTrainSubsystem.TimeBasedDriving.DRIVE_SPEED, DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE));

        group.addSequential(new DriveSetDistanceWithPIDCommand(DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE));
        group.addSequential(new DriveSetDistanceWithPIDCommand(-DriveTrainSubsystem.EncoderBasedDriving.AUTONOMOUS_DRIVE_DISTANCE));
        return group;
    }

    public static CommandGroup doNothingAutonomous() {
        return new CommandGroup();
    }

    public static Command shootBall() {
        CommandGroup fireSequence = new CommandGroup();
        //makes sure it is coiled, then fires
        fireSequence.addSequential(setFingerPosition(ClawFingerSubsystem.UP));
        fireSequence.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_SHOOT_SPEED));
        fireSequence.addSequential(new SetClawWinchSolenoid(false));
        fireSequence.addSequential(new WaitCommand(ClawWinchSubsystem.TIME_TO_FIRE));
        //then recoils
        fireSequence.addSequential(setFingerPosition(ClawFingerSubsystem.DOWN));
        fireSequence.addSequential(new WaitCommand(300));
        fireSequence.addSequential(new RunRollers(ClawRollerSubsystem.ROLLER_SPIN_OFF_SPEED));
        return fireSequence;
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
}
