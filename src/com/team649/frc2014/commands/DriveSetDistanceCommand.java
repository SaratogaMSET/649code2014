package com.team649.frc2014.commands;

import com.sun.squawk.util.MathUtils;
import com.team649.frc2014.Display;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
public class DriveSetDistanceCommand extends CommandBase {

    //constants used to determine the shape of the trapezoid. 
    private static double ACCELERATION = 275;
    //minspeed avoids the motor deadzone, which occurs below roughly 0.2.
    private static double MINSPEED;
    //Stats stuff. I profiled the motor power vs drive speed on Tobiko, and noticed that they are related by
    //  ln(motor power) = A + B*(drivespeed). A and B are calculated using linear regression, with an r^2>99%.
    //  These values vary with battery power and robot, so they're not final and probably never should be.
    public static double linRegA = 131;
    public static double linRegB = 62;
    private final double driveSpeed;
    private final double distance;
    private double accelTime;
    private double holdTime;
    private long startTime;
    private int stage;
    private double accelDist;

    /**
     * Construct a DriveSetDistanceCommand. Immutable, but can safely be reused
     * for multiple executions of the same speed/distance.
     *
     * @param speed The speed to drive at. Always positive.
     * @param distance The distance in inches to drive. Negative to drive
     * backwards.
     */
    public DriveSetDistanceCommand(double speed, double distance) {
        requires(driveTrainSubsystem);

        this.driveSpeed = distance > 0 ? speed : -speed;
        this.distance = Math.abs(distance);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        //set constants fromt the smartdashboard. At some point, acceleration and minspeed should be constant, 
        //  and the linregs should either be constant or set based on battery power
        ACCELERATION = SmartDashboard.getNumber("accelconst");
        MINSPEED = SmartDashboard.getNumber("minoutput");
        linRegA = SmartDashboard.getNumber("linrega");
        linRegB = SmartDashboard.getNumber("linregb");

        //reset encoders. The requires(driveTrainSubsystem) should make this safe, but if there are ever any
        //  issues with encoders being wrong, watch out for this.
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();

        accelTime = Math.abs(driveSpeed) / ACCELERATION;

        //find the time that the max speed should be held (the length of the trapezoid's plateau)
        holdTime = (distance - ACCELERATION * accelTime * accelTime) / Math.abs(driveSpeed);

        //convert the times from seconds to miliseconds
        accelTime *= 1000;
        holdTime *= 1000;
        startTime = System.currentTimeMillis();
        stage = 0;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        //this method is state-based to be as efficient as possible. By usomg the stage int, 
        try {
            final long timeSpent = System.currentTimeMillis() - startTime;
            if (stage == 0) {
                //if stage == 0, then the robot is still accelerating
                if (timeSpent > accelTime) {
                    //if accelerated for the whole time (i.e. has reached max speed), drive at the max speed, and store the distance it took to accel
                    stage = 1;
                    accelDist = Math.abs(driveTrainSubsystem.pidGet());

                    driveTrainSubsystem.driveFwdRot(getDrivePower(driveSpeed), 0);
                } else {
                    //if hasn't accelerated for the whole time, set the output to a fraction of the max, based on time
                    double driveSpeedTarget = driveSpeed * ((double) timeSpent) / accelTime;
                    double output = getDrivePower(driveSpeedTarget);
                    driveTrainSubsystem.driveFwdRot(output, 0);
                }
            } else if (stage == 1) {
                //if stage == 1, the robot is in its max speed mode
                //if pastADist, then the distance remaining to drive is less than 
                //  the distance it takes to accelerate the robot, so starting the deceleration
                //  process here should stop it at the correct distance
                final boolean pastADist = distance - driveTrainSubsystem.pidGet() <= accelDist;

                //if overHTime, then the robot has held the max speed for the appropriate time, 
                //  and should start decelerating as normal
                final boolean overHTime = System.currentTimeMillis() - startTime > accelTime + holdTime;
                if (overHTime || pastADist) {
                    stage = 2;
                }
            }
            //if stage == 2, the robot is decelerating
            if (stage == 2) {
                //set the drive speed to a fraction of the max speed, based on time.
                final double driveSpeedTarget = driveSpeed * ((double) holdTime + 2 * accelTime - timeSpent) / accelTime;
                double output = getDrivePower(driveSpeedTarget);
                driveTrainSubsystem.driveFwdRot(output, 0);
            }
        } catch (Exception e) {
        }
        //print output based on dist travelled
        if (driveTrainSubsystem.pidGet() <= distance / 2) {
            Display.queue("VROOMVROOMVROOM");
        } else {
            Display.queue("GET GASOLINE");
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        //finished only after time, because i'm too lazy to code an adist implementation
        return System.currentTimeMillis() - startTime > holdTime + accelTime * 2;
    }

    // Called once after isFinished returns true
    protected void end() {
        driveTrainSubsystem.disablePid();
        driveTrainSubsystem.driveFwdRot(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }

    private double getDrivePower(double driveSpeedTarget) {
        return MathUtils.pow(Math.E, (Math.abs(driveSpeedTarget) - linRegA) / linRegB) * (driveSpeedTarget > 0 ? 1 : -1) * (1 - MINSPEED) + MINSPEED;
    }
}
