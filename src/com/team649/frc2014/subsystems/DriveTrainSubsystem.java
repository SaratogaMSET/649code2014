/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.Display;
import com.team649.frc2014.RobotMap;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.pid_control.PIDVelocitySource;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.image.RGBImage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 *
 * @author Alex
 */
public class DriveTrainSubsystem extends Subsystem implements PIDVelocitySource, PIDOutput {

    public static final boolean HIGH_SPEED = false;
    public static final boolean LOW_SPEED = true;
    private static final double ENCODER_DISTANCE_PER_PULSE = 4 * Math.PI / 128;
    public static int PERIOD = 100;
    public static final int MAX_DRIVETRAIN_VELOCITY = 135;
    public static int ACCELERATION = 275;
    public static int DRIVE_SPEED = 80;
    private SpeedController[] motors;
    private Encoder[] encoders;
    private PIDController649 pid;
    private Vector lastRates;
    private double accel;
    private DoubleSolenoid shifterSolenoid;

    public DriveTrainSubsystem() {
        motors = new SpeedController[RobotMap.DRIVE_TRAIN.MOTORS.length];
        for (int i = 0; i < RobotMap.DRIVE_TRAIN.MOTORS.length; i++) {
            motors[i] = new Victor(RobotMap.DRIVE_TRAIN.MOTORS[i]);
        }
        pid = new PIDController649(.045, .00, .00, this, this);
        encoders = new Encoder[RobotMap.DRIVE_TRAIN.ENCODERS.length / 2];
        for (int x = 0; x < RobotMap.DRIVE_TRAIN.ENCODERS.length; x += 2) {
            encoders[x / 2] = new Encoder(RobotMap.DRIVE_TRAIN.ENCODERS[x], RobotMap.DRIVE_TRAIN.ENCODERS[x + 1], x == 0, EncodingType.k2X);
            encoders[x / 2].setDistancePerPulse(ENCODER_DISTANCE_PER_PULSE);
        }
        lastRates = new Vector();
        shifterSolenoid = new DoubleSolenoid(RobotMap.DRIVE_TRAIN.FORWARD_SOLENOID_CHANNEL, RobotMap.DRIVE_TRAIN.REVERSE_SOLENOID_CHANNEL);
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
        double left = fwd + rot, right = fwd - rot;
        double max = Math.max(1, Math.max(Math.abs(left), Math.abs(right)));
        left /= max;
        right /= max;
        rawDrive(left, right);
    }

    public void shiftDriveGear(boolean lowSpeed) {
        shifterSolenoid.set(lowSpeed ? Value.kForward : Value.kReverse);
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
        return getDistance();
    }

    public double getDistance() {
        int numEncoders = encoders.length;
        double totalVal = 0;
        for (int i = 0; i < numEncoders; i++) {
            totalVal += encoders[i].getDistance();
        }
        return totalVal / numEncoders;
    }

    public double getPosition() {
        int numEncoders = encoders.length;
        double totalVal = 0;
        for (int i = 0; i < numEncoders; i++) {
            totalVal += encoders[i].get();
        }
        return totalVal / numEncoders;
    }

    public double getRate() {
        return getVelocity();
    }

    public void pidWrite(double output) {
        rawDrive(output, output);
    }

    public boolean isRegularPidOnTarget() {
        return pid.onTarget();
    }

    public int updateAccel() {
        PERIOD = (int) SmartDashboard.getNumber("period");
        double rate = getVelocity();

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
        this.accel = 1000 * sumTop / sumBot;
        SmartDashboard.putNumber("accel", this.accel);
        return PERIOD;
    }

    private double getVelocity() {
        int numEncoders = encoders.length;
        double totalRate = 0;
        for (int i = 0; i < numEncoders; i++) {
            totalRate += encoders[i].getRate();
        }
        final double rate = totalRate / numEncoders;
        return rate;
    }

    public void printEncoders() {
        int i = 1;
        int numEncoders = encoders.length;
        for (int x = 0; x < numEncoders; x++) {
            Display.queue("pos: " + encoders[x].get());
            Display.queue("dis: " + encoders[x].getDistance());
            Display.queue("spd: " + encoders[x].getRate());
        }
    }
}