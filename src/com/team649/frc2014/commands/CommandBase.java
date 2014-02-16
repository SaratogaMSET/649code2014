package com.team649.frc2014.commands;

import com.team649.frc2014.commands.winch.EngageClawWinchSolenoid;
import com.team649.frc2014.commands.winch.FireClawWinch;
import com.team649.frc2014.commands.winch.RunClawWinchMotor;
import com.team649.frc2014.commands.winch.CoilClawWinch;
import com.team649.frc2014.commands.drivetrain.DriveSetDistanceCommand;
import edu.wpi.first.wpilibj.command.Command;
import com.team649.frc2014.OI;
import com.team649.frc2014.RobotMap;
import com.team649.frc2014.commands.fingers.SetFingerPosition;
import com.team649.frc2014.subsystems.CameraSubsystem;
import com.team649.frc2014.subsystems.ClawFingerSubsystem;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import com.team649.frc2014.subsystems.ClawWinchSubsystem;
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
import edu.wpi.first.wpilibj.Compressor;
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
    public static ClawPivotSubsystem clawSubsystem = new ClawPivotSubsystem();
    public static ClawWinchSubsystem winchSubsystem = new ClawWinchSubsystem();
    public static ClawFingerSubsystem clawFingerSubsystem = new ClawFingerSubsystem();

    public static void init() {
        new Compressor(RobotMap.PRESSURE_SWITCH_CHANNEL, RobotMap.COMPRESSOR_RELAY_CHANNEL).start();
        // This MUST be here. If the OI creates Commands (which it very likely
        // will), constructing it during the construction of CommandBase (from
        // which commands extend), subsystems are not guaranteed to be
        // yet. Thus, their requires() statements may grab null pointers. Bad
        // news. Don't move it.
        oi = new OI();
    }

    

    public CommandBase(String name) {
        super(name);
    }

    public CommandBase() {
        super();

    }

    public static Command shootHotGoalAutonomous() {
        //drive forward to shooting position
        //after 0.5 seconds, see if goal is hot
        //if goal is hot, shoot
        //if goal is not hot, wait then shoot

        CommandGroup driveAndCheckGoal = new CommandGroup();
        //drive while checking hot goal
        driveAndCheckGoal.addParallel(new DriveSetDistanceCommand(DriveTrainSubsystem.DRIVE_SPEED, 300));
        //check the hot goal after .5 seconds
        CommandGroup checkHotGoal = new CommandGroup();
        checkHotGoal.addSequential(new WaitCommand(500));
        checkHotGoal.addSequential(new HotVisionWaitCommand());
        driveAndCheckGoal.addSequential(checkHotGoal);

        CommandGroup mainAutonomousSequence = new CommandGroup();
        //drive and check goal. When both are done (checking goal and driving), shoot
        mainAutonomousSequence.addSequential(driveAndCheckGoal);
        mainAutonomousSequence.addSequential(shootBall());
        return mainAutonomousSequence;
    }

    public static Command engageClawSolenoid(){
        CommandGroup engageSequence = new CommandGroup();
        if (!winchSubsystem.isSwitchPressed()){
            engageSequence.addSequential(new WaitCommand(ClawWinchSubsystem.TIME_TO_ENGAGE_SOLENOID));
            engageSequence.addSequential(new EngageClawWinchSolenoid());
        }
        return engageSequence;
    }

    public static Command shootBall() {
        CommandGroup fireSequence = new CommandGroup();
        //makes sure it is coiled, then fires
        //fireSequence.addSequential(coilShooter());
        fireSequence.addSequential(new FireClawWinch());
        //allow for half a second for firing
        fireSequence.addSequential(new WaitCommand(ClawWinchSubsystem.TIME_TO_FIRE));
        //then recoils
        fireSequence.addSequential(coilShooter());
        return fireSequence;
    }

    public static Command coilShooter() {
        CommandGroup coilSequence = new CommandGroup();
        coilSequence.addParallel(new CoilClawWinch());
        coilSequence.addSequential(engageClawSolenoid());
        return coilSequence;
    }
    public static Command setFingerPosition(int state) {
        return new SetFingerPosition(state);
    }
}
