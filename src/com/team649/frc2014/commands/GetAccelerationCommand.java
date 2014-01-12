package com.team649.frc2014.commands;

import com.team649.frc2014.DisplayLCD;

/**
 *
 * @author bradmiller
 */
public class GetAccelerationCommand extends CommandBase {
    //9 m/s^2 = 354.33 in/s^2

    private static final double ACCELERATION = 354.33;
    private final double speed;
    private final double distance;

    public GetAccelerationCommand(double speed, double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(driveTrainSubsystem);
        this.speed = speed;
        this.distance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();
        double accelTime = speed / ACCELERATION;
        double holdTime = (distance - ACCELERATION*accelTime*accelTime)/speed;
        driveTrainSubsystem.startVelocityPid(accelTime, holdTime, speed);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (driveTrainSubsystem.pidGet()<=distance/2)
            DisplayLCD.queue("VROOMVROOMVROOM");
        else
            DisplayLCD.queue("GET GASOLINE");
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return driveTrainSubsystem.isVelocityPidDone();
    }

    // Called once after isFinished returns true
    protected void end() {
        driveTrainSubsystem.disablePid();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        driveTrainSubsystem.disablePid();
    }
}
