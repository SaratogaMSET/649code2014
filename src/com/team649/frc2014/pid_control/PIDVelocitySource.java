/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.pid_control;

import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author Alex
 */
public interface PIDVelocitySource extends PIDSource {

    public double getRate();
}
