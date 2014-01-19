package com.team649.frc2014.commands;

import com.sun.squawk.util.MathUtils;
import com.team649.frc2014.DisplayLCD;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author bradmiller
 */
public class DriveSetDistanceCommand extends CommandBase {
    //9 m/s^2 = 354.33 in/s^2

    private static double ACCELERATION = 275;
    public static double LINREG_A = 131;
    public static double LINREG_B = 62;
    private final double driveSpeed;
    private final double distance;
    private double accelTime;
    private double holdTime;
    private long startTime;
    private int stage;
    private double accelDist;
    private String minoutput;
    private double minSpeed;
    
    public DriveSetDistanceCommand(double speed, double distance) {
        requires(driveTrainSubsystem);
        this.driveSpeed = distance > 0 ? speed : -speed;
        this.distance = Math.abs(distance);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
//        System.out.println("--------------------------");
        ACCELERATION = SmartDashboard.getNumber("accelconst");
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();
        accelTime = Math.abs(driveSpeed) / ACCELERATION;
        holdTime = (distance - ACCELERATION * accelTime * accelTime) / Math.abs(driveSpeed);
        accelTime *= 1000;
        holdTime *= 1000;
        startTime = System.currentTimeMillis();
        stage = 0;
        minSpeed = SmartDashboard.getNumber(minoutput);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        try {
            
            final long timeSpent = System.currentTimeMillis() - startTime;
            if (stage == 0) {
                if (timeSpent > accelTime) {
                    stage = 1;
                    accelDist = Math.abs(driveTrainSubsystem.pidGet());
                    double output = MathUtils.pow(Math.E, (Math.abs(driveSpeed) - LINREG_A) / LINREG_B) * (driveSpeed > 0 ? 1 : -1);
                    driveTrainSubsystem.driveFwdRot(output, 0);
                } else {
                    double driveSpeedTarget = driveSpeed * ((double) timeSpent) / accelTime;
                    double output = getDrivePower(driveSpeedTarget);
                    driveTrainSubsystem.driveFwdRot(output, 0);
                }
            } else if (stage == 1) {
                final boolean pastADist = distance - driveTrainSubsystem.pidGet() <= accelDist;
                final boolean overHTime = System.currentTimeMillis() - startTime > accelTime + holdTime;
                if (overHTime || pastADist) {
                    stage = 2;
                }
            }
            if (stage == 2) {
                final double driveSpeedTarget = driveSpeed * ((double) holdTime + 2 * accelTime - timeSpent) / accelTime;
                double output = getDrivePower(driveSpeedTarget);
                driveTrainSubsystem.driveFwdRot(output, 0);
            }
        } catch (Exception e) {
        }
        if (driveTrainSubsystem.pidGet() <= distance / 2) {
            DisplayLCD.queue("VROOMVROOMVROOM");
        } else {
            DisplayLCD.queue("GET GASOLINE");
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return System.currentTimeMillis() - startTime > holdTime + accelTime * 2;
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
    
    private double getDrivePower(double driveSpeedTarget) {
        return MathUtils.pow(Math.E, (Math.abs(driveSpeedTarget) - LINREG_A) / LINREG_B) * (driveSpeedTarget > 0 ? 1 : -1)*(1-minSpeed)+minSpeed;
    }
}
