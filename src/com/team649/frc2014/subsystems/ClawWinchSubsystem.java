/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import com.team649.frc2014.RobotMap;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * @author Suneel
 */
public class ClawWinchSubsystem extends Subsystem {

    private static final double MOTOR_SPEED = 1;
    public static final int TIME_TO_FIRE = 500;
    public static final int TIME_TO_ENGAGE_SOLENOID = 250;
    private final SpeedController motor;
    private final DigitalInput limit;
    private DoubleSolenoid engageClaw;

    public ClawWinchSubsystem() {
        motor = new Victor(RobotMap.CLAW_WINCH.MOTOR);
        limit = new DigitalInput(RobotMap.CLAW_WINCH.LIMIT_SWITCH_INPUT);
        engageClaw = new DoubleSolenoid(RobotMap.CLAW_WINCH.ENGAGED_SOLENOID_CHANNEL, RobotMap.CLAW_WINCH.LOOSE_SOLENOID_CHANNEL);

    }

    public void runMotor() {
        motor.set(MOTOR_SPEED);
    }

    public void stopMotor() {
        motor.set(0);
    }

    public boolean isSwitchPressed() {
        return limit.get();
    }

    public void setSolenoid(boolean state) {
        engageClaw.set(state ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    }

    public boolean getSolenoidState() {
        return engageClaw.get() == DoubleSolenoid.Value.kForward ? true : false;
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}
