/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team649.frc2014;

import com.sun.squawk.microedition.io.FileConnection;
import com.sun.squawk.util.StringTokenizer;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 *
 * @author Alex
 */
public class Constants {

    private static final boolean DEBUG = true;
    private static final String CONSTANTS_FILE_NAME = "constants.txt";
    private static Vector CONSTANTS = new Vector();

    public static void loadConstants() {
        try {
            //get a connection to the constants file and read it
            final String fileName = "file:///" + CONSTANTS_FILE_NAME;
            printIfDebug("Opening constants file: " + fileName);
            FileConnection commandFileConnection = (FileConnection) Connector.open(fileName, Connector.READ);
            DataInputStream commandFileStream = commandFileConnection.openDataInputStream();
            StringBuffer fileContentsBuffer = new StringBuffer((int) commandFileConnection.fileSize());
            
            //read characters from the file until end of file is reached
            byte[] buff = new byte[255];
            while (commandFileStream.read(buff) != -1) {
                fileContentsBuffer.append(new String(buff));
                //inefficient, but with long files necessary
                buff = new byte[255];
            }
            String fileContents = fileContentsBuffer.toString();
            printIfDebug("Constants file output: " + fileContents);
            StringTokenizer lineTokenizer = new StringTokenizer(fileContents, "\n");
            CONSTANTS = new Vector(lineTokenizer.countTokens());

            //for each line, split into space-separated tokens
            while (lineTokenizer.hasMoreTokens()) {
                String line = lineTokenizer.nextToken().trim();
                StringTokenizer spaceTokenizer = new StringTokenizer(line, " ");
                //map the first two tokens
                if (spaceTokenizer.countTokens() > 1) {
                    final String key = spaceTokenizer.nextToken().trim();
                    final String value = spaceTokenizer.nextToken().trim();
                    CONSTANTS.addElement(new Constant(key, value));
                    printIfDebug("Put constant: " + key + ": " + value + ", of type " + Constant.TYPE_NAMES[((Constant) CONSTANTS.lastElement()).getType()]);
                }
            }
        } catch (Exception ex) {
            System.out.println("Could not load file " + CONSTANTS_FILE_NAME + ". Are you sure it is in the root directory of the cRIO?");
        }
    }

    public static String getString(String key) throws KeyNotFoundException, InvalidTypeException {
        for (int i = 0; i < CONSTANTS.size(); i++) {
            Constant constant = (Constant) CONSTANTS.elementAt(i);
            if (constant.getKey().equals(key)) {
                if (constant.getType() == Constant.STRING_TYPE) {
                    return constant.stringValue;
                } else {
                    throw new InvalidTypeException(key, "String", constant.getType());
                }
            }
        }
        throw new KeyNotFoundException(key);
    }

    public static String getString(String key, String defaultValue) {
        try {
            return getString(key);
        } catch (Exception e) {
            printIfDebug(e.getMessage());
            return defaultValue;
        }
    }

    public static double getDouble(String key) throws KeyNotFoundException, InvalidTypeException {
        for (int i = 0; i < CONSTANTS.size(); i++) {
            Constant constant = (Constant) CONSTANTS.elementAt(i);
            if (constant.getKey().equals(key)) {
                if (constant.getType() == Constant.DOUBLE_TYPE) {
                    return constant.doubleValue;
                } else {
                    throw new InvalidTypeException(key, "Double", constant.getType());
                }
            }
        }
        throw new KeyNotFoundException(key);
    }

    public static double getDouble(String key, double defaultValue) {
        try {
            return getDouble(key);
        } catch (Exception e) {
            printIfDebug(e.getMessage());
            return defaultValue;
        }
    }

    public static int getInt(String key) throws KeyNotFoundException, InvalidTypeException {
        for (int i = 0; i < CONSTANTS.size(); i++) {
            Constant constant = (Constant) CONSTANTS.elementAt(i);
            if (constant.getKey().equals(key)) {
                if (constant.getType() == Constant.INTEGER_TYPE) {
                    return constant.intValue;
                } else {
                    throw new InvalidTypeException(key, "Integer", constant.getType());
                }
            }
        }
        throw new KeyNotFoundException(key);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            printIfDebug(e.getMessage());
            return defaultValue;
        }
    }

    private static void printIfDebug(String output) {
        if (DEBUG) {
            System.out.println(output);
        }
    }

    private static class KeyNotFoundException extends Exception {

        public KeyNotFoundException(String key) {
            super("Key " + key + " not found.");
        }
    }

    private static class InvalidTypeException extends Exception {

        public InvalidTypeException(String key, String expected, int actual) {
            super("Expected " + expected + " for " + key + ", got " + Constant.TYPE_NAMES[actual]);
        }
    }

    private static class Constant {

        private static final int INTEGER_TYPE = 0;
        private static final int DOUBLE_TYPE = 1;
        private static final int STRING_TYPE = 2;
        private static final String[] TYPE_NAMES = new String[3];

        static {
            TYPE_NAMES[Constant.INTEGER_TYPE] = "Integer";
            TYPE_NAMES[Constant.DOUBLE_TYPE] = "Double";
            TYPE_NAMES[Constant.STRING_TYPE] = "String";
        }
        String key;
        String stringValue;
        int intValue;
        double doubleValue;
        int type;

        private Constant(String key, String value) {
            this.key = key;
            try {
                intValue = Integer.parseInt(value);
                type = INTEGER_TYPE;
            } catch (NumberFormatException intE) {
                try {
                    doubleValue = Double.parseDouble(value);
                    type = DOUBLE_TYPE;
                } catch (NumberFormatException doubleE) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    stringValue = value;
                    type = STRING_TYPE;
                }
            }
        }

        public int getType() {
            return type;
        }

        public String getKey() {
            return key;
        }

        public String getStringValue() {
            return stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }
    }
}
