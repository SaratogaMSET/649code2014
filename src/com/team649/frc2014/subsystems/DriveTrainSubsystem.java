/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.pid_control.PIDVelocitySource;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author Alex
 */
public class DriveTrainSubsystem extends Subsystem implements PIDVelocitySource, PIDOutput {

    private static final double ENCODER_DISTANCE_PER_PULSE = 0.05385587;
    public static int PERIOD = 100;
    private SpeedController[] motors;
    private Encoder[] encoders;
    private PIDController649 pid;
    private Vector lastRates;
    private double accel;

    public DriveTrainSubsystem() {
        motors = new SpeedController[RobotMap.driveTrainMotors.length];
        for (int i = 0; i < RobotMap.driveTrainMotors.length; i++) {
            motors[i] = new Victor(RobotMap.driveTrainMotors[i]);
        }
        pid = new PIDController649(.045, .00, .00, this, this);
        encoders = new Encoder[RobotMap.encoders.length / 2];
        for (int x = 0; x < RobotMap.encoders.length; x += 2) {
            encoders[x / 2] = new Encoder(RobotMap.encoders[x], RobotMap.encoders[x + 1], x == 0, EncodingType.k2X);
            encoders[x / 2].setDistancePerPulse(ENCODER_DISTANCE_PER_PULSE);
        }
        lastRates = new Vector();
        new Timer().schedule(new TimerTask() {
            public void run() {
                updateAccel();
            }
        }, 0);

    }

    public void startEncoders() {
        for (int x = 0; x < encoders.length; x++) {
            encoders[x].start();
        }
        lastRates.removeAllElements();
        accel = 0;
    }

    public void resetEncoders() {
        for (int x = 0; x < encoders.length; x++) {
            encoders[x].reset();
        }
    }
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

    public void driveFwdRot(double fwd, double rot) {
        double left = fwd + rot, right = left;
        double max = Math.max(1, Math.max(Math.abs(left), Math.abs(right)));
        left /= max;
        right /= max;
        rawDrive(left, right);
    }

    public void rawDrive(double left, double right) {
        int i = 0;
        for (; i < motors.length / 2; i++) {
            motors[i].set(left);
        }

        for (; i < motors.length; i++) {
            motors[i].set(-right);
        }
    }

    public void disablePid() {
        pid.disable();
    }

    public double pidGet() {
        int numEncoders = encoders.length;
        double totalVal = 0;
        for (int i = 0; i < numEncoders; i++) {
            totalVal += encoders[i].getDistance();
        }
        return totalVal / numEncoders;
    }

    public double getRate() {
        return accel;
    }

    public void pidWrite(double output) {
        rawDrive(output, output);
    }

    public void startVelocityPid(double accelTime, double holdTime, double velocity) {
        pid.setInputRange(-150, 150);
        pid.setOutputRange(0.2, 1);
        pid.setVelocityPid(true, accelTime, holdTime);
        pid.setSetpoint(velocity);
        pid.setPIDFromDriverStation(1);
        pid.enable();
    }

    public boolean isRegularPidOnTarget() {
        return pid.onTarget();
    }

    public boolean isVelocityPidDone() {
        return pid.isVelocityDone();
    }

    private void updateAccel() {
        PERIOD = (int) SmartDashboard.getNumber("period");
        int numEncoders = encoders.length;
        double totalRate = 0;
        for (int i = 0; i < numEncoders; i++) {
            totalRate += encoders[i].getRate();
        }
        final double rate = totalRate / numEncoders;

        while (lastRates.size() >= SmartDashboard.getNumber("numPoints")) {
            lastRates.removeElementAt(0);
        }
        lastRates.addElement(new Double(rate));
        double avgX = 0;
        for (int i = 0; i < lastRates.size(); i++) {
            avgX += i;
        }
        double avgY = 0;
        for (int i = 0; i < lastRates.size(); i++) {
            avgY += ((Double) lastRates.elementAt(i)).doubleValue();
        }
        double sumTop = 0;
        for (int i = 0; i < lastRates.size(); i++) {
            sumTop += (i - lastRates.size() / 2 + 0.5) * (((Double) lastRates.elementAt(i)).doubleValue() - avgY);
        }

        double sumBot = 0;
        for (int i = 0; i < lastRates.size(); i++) {
            sumBot += (i - avgX) * (i - avgX);
        }

        SmartDashboard.putNumber("veloc", rate);
        SmartDashboard.putNumber("accel", sumTop / sumBot);
        new Timer().schedule(new TimerTask() {
            public void run() {
                updateAccel();
            }
        }, PERIOD);
    }
}