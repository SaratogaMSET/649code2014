package com.team649.frc2014;

import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.DriverStationLCD;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;

public class Display {

    private static final String PADDING = "                     ";
    private static final boolean[] printed = new boolean[]{false, false, false, false, false, false};
    private static final String[] queuedLines = new String[6];
    private static final Marquee[] marqueedLines = new Marquee[6];
    private static int queueCount = 0;
    public static final short MAX_LINE_LENGTH = 21;
    public static final String TRUNCATE_MARKER = ">";
    private static DriverStationLCD display = DriverStationLCD.getInstance();
    private static OutputStream FILE_OUTPUT_STREAM;

    static {
        try {
            FileConnection conn = (FileConnection) Connector.open("file:///output" + System.currentTimeMillis() + ".txt", Connector.READ_WRITE);
            conn.create();
            FILE_OUTPUT_STREAM = conn.openOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Queue a line of text to be displayed. Does not guarantee any consistent
     * location.
     *
     * @param text The line of text to be displayed. Max of 16 characters
     * @return true if the line was successfully queued, else false. If returns
     * false, a good alternative is a Sysout
     */
    public static boolean queue(String text) {
        if (queueCount >= 6) {
            return false;
        }
        queuedLines[queueCount++] = text;
        return true;
    }

    public static void printToOutputStream(Object text) {
        System.out.println(text);
        if (FILE_OUTPUT_STREAM != null) {
            try {
                FILE_OUTPUT_STREAM.write(text.toString().getBytes());
                FILE_OUTPUT_STREAM.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Print all output to the driver station
     */
    public static void update() {
        for (int i = 0; i < 6; i++) {
            if (marqueedLines[i] != null) {
                marqueedLines[i].doIncrement();
                println(i + 1, marqueedLines[i].getPrintableString());
                printed[i] = true;
            }
        }
        int queuedLineIndex = 0;
        for (int i = 0; i < 6 && queuedLineIndex < queueCount; i++) {
            if (!printed[i]) {
                println(i + 1, queuedLines[queuedLineIndex++]);
            }
        }
        display.updateLCD();
    }

    /**
     * Clears the LCD display.
     */
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

    /**
     * Prints text to a specific line on the LCD display
     *
     * @param line Line number from [1,6].
     * @param text String to print out. If it's longer than
     * {@link #MAX_LINE_LENGTH} characters, it will be truncated and a marker
     * ({@link #TRUNCATE_MARKER}) will be added on to the end.
     */
    public static void println(int line, String text) {
        if (line < 1 || line > 6) {
            return;
        }
//        if (marqueedLines[line - 1] != null) {
//            marqueedLines[line - 1] = null;
//        }
        //truncate the text if it's longer than the maximum length and add a marker to indicate that it was truncated
        if (text.length() > MAX_LINE_LENGTH) {
            text = text.substring(0, MAX_LINE_LENGTH - TRUNCATE_MARKER.length()) + TRUNCATE_MARKER;
        }
        switch (line) {
            case 1:
                display.println(DriverStationLCD.Line.kUser1, 1, text);
                break;
            case 2:
                display.println(DriverStationLCD.Line.kUser2, 1, text);
                break;
            case 3:
                display.println(DriverStationLCD.Line.kUser3, 1, text);
                break;
            case 4:
                display.println(DriverStationLCD.Line.kUser4, 1, text);
                break;
            case 5:
                display.println(DriverStationLCD.Line.kUser5, 1, text);
                break;
            case 6:
                display.println(DriverStationLCD.Line.kUser6, 1, text);
                break;
        }
    }

    /**
     * Prints text to marquee on a specific line on the LCD display. This is
     * semipermanent - it does not get cleared by {@link clear()}, but can be
     * removed by printing a line over it.
     *
     * @param line Line number from [1,6].
     * @param text String to print out. If it's longer than
     * {@link #MAX_LINE_LENGTH} characters, it will be truncated and a marker
     * ({@link #TRUNCATE_MARKER}) will be added on to the end.
     * @param startColumn The column to start the marquee on. [0-15]
     * @param columnsPerSecond The number of columns to scroll through per
     * second.
     * @param fullLength Whether to use the full length of the row or just the
     * String
     */
    public static void marquee(int line, String text, int startColumn, int columnsPerSecond, boolean fullLength) {
        //truncate the text if it's longer than the maximum length and add a marker to indicate that it was truncated
        if (text != null && !text.equals("")) {
            if (text.length() > MAX_LINE_LENGTH) {
                text = text.substring(0, MAX_LINE_LENGTH - TRUNCATE_MARKER.length()) + TRUNCATE_MARKER;
            }
            marqueedLines[line - 1] = new Marquee(text, startColumn, columnsPerSecond, fullLength);
        } else {
            marqueedLines[line - 1] = null;
        }
    }

    static void clearMarquees() {
        for (int i = 0; i < 6; i++) {
            marqueedLines[i] = null;
        }
    }

    private static class Marquee {

        private long lastMarqueeUpdateTime;
        private final String text;
        private int column;
        private final int columnTime;
        private final int length;

        public Marquee(String text, int column, int columnsPerSecond, boolean fullLength) {
            this.text = text;
            this.column = column;
            this.columnTime = 1000 / columnsPerSecond;
            this.lastMarqueeUpdateTime = System.currentTimeMillis();
            this.length = fullLength ? MAX_LINE_LENGTH : text.length();
        }

        public String getPrintableString() {
            final int splitColumn = Math.max(0, Math.min(length - column, text.length()));
            if (splitColumn != text.length()) {
                return text.substring(splitColumn, text.length()) + PADDING.substring(0, length - text.length()) + text.substring(0, splitColumn);

            } else {
                final int len = Math.min(column + text.length(), PADDING.length());
                final String startString = PADDING.substring(0, column) + text;
                return startString + PADDING.substring(0, length - startString.length());
            }
        }

        public void syncTime(long startTime) {
            lastMarqueeUpdateTime = startTime;
        }

        public void doIncrement() {
            final long currTime = System.currentTimeMillis();
            if (currTime - lastMarqueeUpdateTime > columnTime) {
                column = (column + 1) % length;
                lastMarqueeUpdateTime = System.currentTimeMillis();
            }
        }
    }
}
