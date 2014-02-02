package com.team649.frc2014.commands;

import edu.wpi.first.wpilibj.command.Command;
import com.team649.frc2014.OI;
import com.team649.frc2014.RobotMap;
import com.team649.frc2014.subsystems.CameraSubsystem;
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * The base for all commands. All atomic commands should subclass CommandBase.
 * CommandBase stores creates and stores each control system. To access a
 * subsystem elsewhere in your code in your code use CommandBase.exampleSubsystem
 * @author Author
 */
public abstract class CommandBase extends Command {

    public static OI oi;
    // Create a single static instance of all of your subsystems
    public static DriveTrainSubsystem driveTrainSubsystem = new DriveTrainSubsystem();
    public static CameraSubsystem cameraSubsystem = new CameraSubsystem();
    
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
    public static Command getAutonomousCommand() {
        //drive forward to shooting position
        //after 0.5 seconds, see if goal is hot
        //if goal is hot, shoot
        //if goal is not hot, wait then shoot
        CommandGroup AutonomousCommand = new CommandGroup();
        AutonomousCommand.addSequential(new DriveSetDistanceCommand(DriveTrainSubsystem.DRIVE_SPEED, 300));
        CommandGroup WaitAndVision = new CommandGroup();
        WaitAndVision.addSequential(new WaitCommand(500));
        WaitAndVision.addSequential(new VisionCommand());
        AutonomousCommand.addParallel(WaitAndVision);
        //shoot
        return AutonomousCommand;
        
}
}
