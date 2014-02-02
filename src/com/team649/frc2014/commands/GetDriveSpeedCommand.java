package com.team649.frc2014.commands;

import com.team649.frc2014.Display;
import java.util.Vector;

/**
 *
 * @author bradmiller
 */
public class GetDriveSpeedCommand extends CommandBase {

    private static final long RUN_TIME = 2000;
    private static final long DELAY = 500;
    //9 m/s^2 = 354.33 in/s^2
    private Vector speedsVector;
    private long startTime;
    private final double speed;

    public GetDriveSpeedCommand(double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(driveTrainSubsystem);
        speedsVector = new Vector();
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();
        startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        driveTrainSubsystem.driveFwdRot(speed, 0);
        if (System.currentTimeMillis() - startTime > DELAY) {
            speedsVector.addElement(new Double(driveTrainSubsystem.getRate()));
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return System.currentTimeMillis() - startTime > RUN_TIME;
    }

    // Called once after isFinished returns true
    protected void end() {
        double avg = 0;
        for (int i = 0; i < speedsVector.size(); i++) {
            avg += ((Double) speedsVector.elementAt(i)).doubleValue();
        }
        System.out.println(speed + ": " + avg / speedsVector.size());
        driveTrainSubsystem.driveFwdRot(0, 0);
        speedsVector.removeAllElements();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        driveTrainSubsystem.driveFwdRot(0, 0);
        speedsVector.removeAllElements();
    }
}
