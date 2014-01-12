package com.team649.frc2014;

import edu.wpi.first.wpilibj.DriverStationLCD;

public class DisplayLCD {

    private static final String PADDING = "                                   ";
    private static final boolean[] printed = new boolean[]{false, false, false, false, false, false};
    private static final String[] queuedLines = new String[6];
    private static int queueCount = 0;
    private static DriverStationLCD display = DriverStationLCD.getInstance();

    public static boolean queue(String text) {
        if (queueCount >= 6) {
            return false;
        }
        queuedLines[queueCount++] = text;
        return true;
    }

    public static void println(int line, String text) {
        printed[line] = true;
        switch (line) {

            case 0:
                display.println(DriverStationLCD.Line.kUser1, 1, text);
                break;
            case 1:
                display.println(DriverStationLCD.Line.kUser2, 1, text);
                break;
            case 2:
                display.println(DriverStationLCD.Line.kUser3, 1, text);
                break;
            case 3:
                display.println(DriverStationLCD.Line.kUser4, 1, text);
                break;
            case 4:
                display.println(DriverStationLCD.Line.kUser5, 1, text);
                break;
            case 5:
                display.println(DriverStationLCD.Line.kUser6, 1, text);
                break;
        }
    }

    public static void update() {
        int queuedLineIndex = 0;
        for (int i = 0; i < 6 && queuedLineIndex < queueCount; i++) {
            if (!printed[i]) {
                println(i, queuedLines[queuedLineIndex++]);
            }
        }
        display.updateLCD();
    }

    public static void clear() {
        display.println(DriverStationLCD.Line.kUser1, 1, PADDING);
        display.println(DriverStationLCD.Line.kUser2, 1, PADDING);
        display.println(DriverStationLCD.Line.kUser3, 1, PADDING);
        display.println(DriverStationLCD.Line.kUser4, 1, PADDING);
        display.println(DriverStationLCD.Line.kUser5, 1, PADDING);
        display.println(DriverStationLCD.Line.kUser6, 1, PADDING);
        for (int i = 0; i < 6; i++) {
            printed[i] = false;
        }
        queueCount = 0;
        
    }
}
