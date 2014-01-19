package com.team649.frc2014.commands;

import com.sun.squawk.util.MathUtils;
import com.team649.frc2014.DisplayLCD;
import static com.team649.frc2014.pid_control.PIDController649.LINREG_A;
import static com.team649.frc2014.pid_control.PIDController649.LINREG_B;
import com.team649.frc2014.pid_control.PIDVelocitySource;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 *
 * @author bradmiller
 */
public class DriveSetDistanceCommand extends CommandBase {
    //9 m/s^2 = 354.33 in/s^2

    private static double ACCELERATION = 100;
    private final double speed;
    private final double distance;

    public DriveSetDistanceCommand(double speed, double distance) {
        requires(driveTrainSubsystem);
        this.speed = distance > 0 ? speed : -speed;
        this.distance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
//        System.out.println("--------------------------");
        ACCELERATION = SmartDashboard.getNumber("accelconst");
        driveTrainSubsystem.resetEncoders();
        driveTrainSubsystem.startEncoders();
        double accelTime = Math.abs(speed) / ACCELERATION;
        double holdTime = (Math.abs(distance) - ACCELERATION * accelTime * accelTime) / Math.abs(speed);
        driveTrainSubsystem.startVelocityPid(accelTime * 1000, holdTime * 1000, speed, distance);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        try {

            input = ((PIDVelocitySource) pidInput).getRate();
            final long timeSpent = System.currentTimeMillis() - m_startTime;
            if (m_stage == 0) {
                if (timeSpent > m_accelTime) {
                    m_stage = 1;
                    accelDist = Math.abs(pidInput.pidGet());
                    setPIDFromDriverStation(2);

                    double speed = MathUtils.pow(Math.E, (Math.abs(m_speed) - LINREG_A) / LINREG_B) * (m_speed > 0 ? 1 : -1);
                    setOutputRange(speed, speed);
                } else {
                    double velocityTarget = m_speed * ((double) timeSpent) / m_accelTime;
                    double speed = MathUtils.pow(Math.E, (Math.abs(velocityTarget) - LINREG_A) / LINREG_B) * (m_speed > 0 ? 1 : -1);
                    setSetpoint(m_speed * ((double) timeSpent) / m_accelTime);
                }
            } else if (m_stage == 1) {
                final boolean pastADist = m_distGoal - pidInput.pidGet() <= accelDist;
                final boolean overHTime = System.currentTimeMillis() - m_startTime > m_accelTime + m_holdTime;
                if (overHTime || pastADist) {
                    m_stage = 2;
                    if (m_speed > 0) {
                        setOutputRange(SmartDashboard.getNumber("minoutput"), 1);
                    } else {
                        setOutputRange(-1, -SmartDashboard.getNumber("minoutput"));
                    }
                }
            }
            if (stage == 2) {

                final double velocityTarget = speed * ((double) holdTime + 2 * accelTime - timeSpent) / accelTime;
                double speed = MathUtils.pow(Math.E, (Math.abs(velocityTarget) - LINREG_A) / LINREG_B) * (speed > 0 ? 1 : -1);
                driveTrainSubsystem.driveFwdRot(speed, 0);
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
