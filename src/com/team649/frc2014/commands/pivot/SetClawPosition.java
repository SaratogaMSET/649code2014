/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.commands.pivot;

import com.team649.frc2014.Display;
import com.team649.frc2014.commands.ChangeableBoolean;
import com.team649.frc2014.commands.CommandBase;
import com.team649.frc2014.pid_control.PIDController649;
import com.team649.frc2014.subsystems.ClawPivotSubsystem;
import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 * @author Kabi
 */
public class SetClawPosition extends CommandBase {

    private final PIDController649 clawPID;
    private final int state;
    private long startTime;
    private ChangeableBoolean bool;

    public SetClawPosition(int state) {
        clawPID = clawPivotSubsystem.getClawPID();
        this.state = state;
    }

    public SetClawPosition(int state, ChangeableBoolean bool) {
        this(state);
        this.bool = bool;

    }

    protected void initialize() {
        clawPID.enable();
        final double setpoint = ClawPivotSubsystem.CLAW_POT_STATES[state];
        clawPID.setSetpoint(setpoint);
        startTime = System.currentTimeMillis();
        Display.printToOutputStream("s: " + setpoint + ", p: " + clawPID.getP() + ", i: " + clawPID.getI() + ", d: " + clawPID.getD());
    }

    protected void execute() {
    }

    protected boolean isFinished() {
        final long timeDiff = System.currentTimeMillis() - startTime;
        //TODO make sure things get finished (time to be on target)

        return ((state != ClawPivotSubsystem.PICKUP && clawPID.onTarget() && timeDiff > 1500) || timeDiff > 4000) && (bool == null || bool.bool);
    }

    protected void end() {
        killCommand();
    }

    protected void interrupted() {
        killCommand();
    }

    private void killCommand() {
        try {
            Display.printToOutputStream("end pivot: " + DriverStation.getInstance().getMatchTime() + ": " + clawPivotSubsystem.getPotValue());
            clawPID.disable();
        } catch (NullPointerException e) {
        }
        clawPivotSubsystem.setPower(0);
    }

    public int getState() {
        return state;
    }
}
