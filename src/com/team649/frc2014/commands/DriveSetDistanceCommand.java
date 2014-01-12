package com.team649.frc2014.commands;

import com.team649.frc2014.DisplayLCD;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 *
 * @author bradmiller
 */
public class DriveSetDistanceCommand extends CommandBase {
    //9 m/s^2 = 354.33 in/s^2

    private static double ACCELERATION = 100;
    private final Vector speedsVector;
    private final double speed;
    private final double distance;

    public DriveSetDistanceCommand(double speed, double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(driveTrainSubsystem);
        this.speed = speed;
        this.distance = distance;
        speedsVector = new Vector();
        ACCELERATION = SmartDashboard.getNumber("accelconst");
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();
        double accelTime = speed / ACCELERATION;
        double holdTime = (distance - ACCELERATION * accelTime * accelTime) / speed;
        driveTrainSubsystem.startVelocityPid(accelTime * 1000, holdTime * 1000, speed);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        SmartDashboard.putNumber("vel", driveTrainSubsystem.getRate());
        speedsVector.addElement(new Double(driveTrainSubsystem.getRate()));
        if (driveTrainSubsystem.pidGet() <= distance / 2) {
            DisplayLCD.queue("VROOMVROOMVROOM");
        } else {
            DisplayLCD.queue("GET GASOLINE");
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return driveTrainSubsystem.isVelocityPidDone();
    }

    // Called once after isFinished returns true
    protected void end() {
        driveTrainSubsystem.disablePid();
        double total = 0;
        for (int i = 0; i < speedsVector.size(); i++) {
            total += ((Double) speedsVector.elementAt(i)).doubleValue();
        }
        System.out.println(total / speedsVector.capacity());
        speedsVector.removeAllElements();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        driveTrainSubsystem.disablePid();
        speedsVector.removeAllElements();
    }
}
