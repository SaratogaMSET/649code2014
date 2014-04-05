package com.team649.frc2014.commands.drivetrain;

import com.team649.frc2014.Display;
import com.team649.frc2014.commands.ChangeableBoolean;
import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.subsystems.DriveTrainSubsystem;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * DriveSetDistanceCommand
 *
 * Attempts to drive the robot using a trapezoidal motion profile. This means
 * that it accelerates at a constant rate, holds that speed for a set time, and
 * then decelerates at a constant rate. This should ideally provide a much more
 * consistent drive (in both time and distance) than a motion profile that
 * accelerates as fast as possible to start, and has trouble finishing (i.e. a
 * PID loop).
 *
 * @author alex@renda.org
 */
public class DriveSetDistanceWithPIDCommand extends CommandBase {

    public static final int ON_TARGET_TIME = 250;
    private final double distance;
    private PIDController649 pid;
    private long onTargetStartTime;
    private ChangeableBoolean finishedChecker;
    private double minDriveSpeed;

    /**
     * Construct a DriveSetDistanceCommand. Immutable, but can safely be reused
     * for multiple executions of the same speed/distance.
     *
     * @param speed The speed to drive at. Always positive.
     * @param distance The distance in inches to drive. Negative to drive
     * backwards.
     */
    public DriveSetDistanceWithPIDCommand(double distance) {
        this.distance = distance;
        this.minDriveSpeed = 0.25;
    }

    public DriveSetDistanceWithPIDCommand(double distance, double minDriveSpeed) {
        this.distance = distance;
        this.minDriveSpeed = minDriveSpeed;
    }

    public DriveSetDistanceWithPIDCommand(double distance, double minDriveSpeed, ChangeableBoolean finishedChecker) {
        this.distance = distance;
        this.finishedChecker = finishedChecker;
        this.minDriveSpeed = minDriveSpeed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Display.printToOutputStream("starting drive PID: " + DriverStation.getInstance().getMatchTime() + ", dist: " + distance);
        DriveTrainSubsystem.EncoderBasedDriving.MIN_MOTOR_POWER = minDriveSpeed;
        this.pid = driveTrainSubsystem.getPID();
        pid.setPID(DriveTrainSubsystem.EncoderBasedDriving.AUTO_DRIVE_P, DriveTrainSubsystem.EncoderBasedDriving.AUTO_DRIVE_I, DriveTrainSubsystem.EncoderBasedDriving.AUTO_DRIVE_D);
        pid.setSetpoint(distance);
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();
        pid.enable();
        onTargetStartTime = -1;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
//        if (pid.onTarget()) {
//            Display.printToOutputStream("On Target");
//            if (onTargetStartTime == -1) {
//                Display.printToOutputStream("Set to curr time");
//                onTargetStartTime = System.currentTimeMillis();
//            } else if (System.currentTimeMillis() - onTargetStartTime > ON_TARGET_TIME) {
//                return true;
//            }
//        } else {
//            if (onTargetStartTime != -1) {
//                Display.printToOutputStream("Off target");
//            }
//            onTargetStartTime = -1;
//        }
//
//        return false;
        return Math.abs(driveTrainSubsystem.getDistance()) > Math.abs(distance);
    }

// Called once after isFinished returns true
    protected void end() {
        Display.printToOutputStream("finished drive PID: " + DriverStation.getInstance().getMatchTime() + ", dist: " + driveTrainSubsystem.getDistance());
        pid.disable();
        driveTrainSubsystem.driveFwdRot(0, 0);
        if (finishedChecker != null) {
            finishedChecker.bool = true;
        }
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
