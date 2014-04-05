/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014.subsystems;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;

/**
 *
 * @author Alex
 */
public class CameraSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    private AxisCamera cam;

    public CameraSubsystem() {
        cam = AxisCamera.getInstance("10.6.49.11");
    }

    public ColorImage getImage() throws AxisCameraException, NIVisionException {
        return cam.getImage();
    }

    protected void initDefaultCommand() {
    }
}
