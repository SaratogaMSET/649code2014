package com.team649.frc2014;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * Command Script Parser. Reads commands from local file on cRIO (which can be
 * accessed through FTP with "ftp 10.xx.yy.2", and "ftp put
 * FILENAME.COMMAND_FILE_SUFFIX" [needs to be in /scripts/ directory]). The
 * scripts are read in essentially the same way they are written in Java: any
 * grouping (curly brace '{', square bracket'[]', parenthesis '()', must be
 * closed with a matching character) will be a group of commands. By default,
 * commands will be added in sequence. If a command is preceded with '~', it
 * will be added in parallel. Commands can be separated by any character that is
 * not appropriate for a Java variable and is not a grouping character or a '~'.
 *
 * Commands must either be in the same package as this class (I think), or be
 * referenced in the script by their full package name. Alternatively, the file
 * can start with #define statements, such as #define AutoCoilClawWinch
 * com.team649.frc2014.commands.AutoCoilClawWinch or can use a common package
 * with #define COMMON_PACKAGE com.team649.frc2014.commands
 *
 * Example usage:
 *
 * { CommandA CommandB ~{CommandC, CommandD, ~(CommandE, CommandF)} CommandG }
 * will result in a command that executes CommandA, then CommandB, then CommandG
 * and (CommandC, then CommandD and (CommandE then CommandF))
 *
 * @author Alex Renda (alex@renda.org), Team 649, 2014
 */
public class CommandScriptParser {

    private static final boolean DEBUG = false;
    private static final String COMMAND_FILE_SUFFIX = "frcscript";

    public static Command parseCommand(String name) {
        final String commandFileName = "scripts/" + name + (COMMAND_FILE_SUFFIX != null && !COMMAND_FILE_SUFFIX.equals("") ? "." + COMMAND_FILE_SUFFIX : "");
        try {
            MapEntry fileContents = loadFile(commandFileName);
            return getCommandGroup(fileContents.key, fileContents.value);
        } catch (IOException ex) {
            System.out.println("Command " + name + " was not found at " + commandFileName + ". Are you sure it was named and placed correctly?");
        } catch (Exception ex) {
            System.out.println("Command " + name + " located at " + commandFileName + " could not be parsed correctly.");
        }
        return new DummyCommand();
    }

    private static Command getCommandGroup(String content, String commonPackage) {
        printIfDebug("Adding command group: " + content + "\n with common package: " + commonPackage);
        boolean nextParallel = false;
        String commandName = "";
        CommandGroup group = new CommandGroup();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            Command commandToAdd = null;
            if ('0' <= c && c <= '9' || 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || c == '_' || c == '.') {
                //if the character is part of a valid command name, add it to
                // the command name currently being constructed
                commandName += c;
            } else if (c == '~') {
                nextParallel = true;
            } else if (c == '(') {
                //detect and add the various CommandGroups. I probably should
                //  only allow one character pair for a group, but for readabilty
                //  of the actual command file, I feel that this is best
                commandToAdd = getCommandGroup(content.substring(i + 1, i = getCommandGroupEnd(i, content, '(', ')')), commonPackage);
            } else if (c == '[') {
                commandToAdd = getCommandGroup(content.substring(i + 1, i = getCommandGroupEnd(i, content, '[', ']')), commonPackage);
            } else if (c == '{') {
                commandToAdd = getCommandGroup(content.substring(i + 1, i = getCommandGroupEnd(i, content, '{', '}')), commonPackage);
            } else if (!commandName.equals("")) {
                //if not constructing the command name and not a group, get the
                //  actual command by its name, with the possible use of a common
                //  package
                try {
                    commandName = (commonPackage != null ? commonPackage : "") + commandName;
                    commandToAdd = (Command) Class.forName(commandName).newInstance();
                    printIfDebug("Getting command " + commandName + ".");
                    commandName = "";
                } catch (Exception e) {
                    System.out.println("Command class " + commandName + " not found or could not be initialized due to " + e.getClass().getName() + ".");
                    commandName = "";
                }
            }

            if (commandToAdd != null) {
                printIfDebug("Adding command in " + (nextParallel ? "parallel" : "sequence") + ".");

                if (nextParallel) {
                    group.addParallel(commandToAdd);
                } else {
                    group.addSequential(commandToAdd);
                }

                nextParallel = false;
            }
        }
        return group;
    }

    //get the index of the end of a specific type of command group
    private static int getCommandGroupEnd(int startIndex, String content, char openChar, char closeChar) {
        int countOpenGroups = 1;
        while (countOpenGroups != 0) {
            final char commandGroupSentinelCharacter = content.charAt(++startIndex);
            if (commandGroupSentinelCharacter == openChar) {
                countOpenGroups++;
            } else if (commandGroupSentinelCharacter == closeChar) {
                countOpenGroups--;
            }
        }
        return startIndex;
    }

    private static MapEntry loadFile(final String commandFileName) throws IOException {
        //initialize variables, get connection to the local file.
        String contents = "";
        DataInputStream commandFileStream;
        commandFileStream = Connector.openDataInputStream("file:///" + commandFileName);
        boolean reading = true;
        boolean commentedLine = false;
        String defineProgress = "";
        String key = "";
        String entry = "";
        boolean doneKey = false;

        Vector replacementMap = new Vector();

        //read characters from the file until end of file is reached
        while (reading) {
            try {
                final char nextChar = (char) commandFileStream.readByte();
                if (nextChar == '#') {
                    commentedLine = true;
                }
                //check for define statements. Since the file is most 
                //  efficiently and quickly read on a character-by-character
                //  basis, must keep a reference to past characters
                if ("#define".startsWith(defineProgress + nextChar)) {
                    defineProgress += nextChar;
                } else if (nextChar == '\n' || !"#define".equals(defineProgress)) {
                    if (nextChar == '\n') {
                        commentedLine = false;
                    }
                    //if the define statement is complete, add to the list
                    if (!key.equals("") && !entry.equals("")) {
                        replacementMap.addElement(new MapEntry(key.trim(), entry.trim()));
                    }
                    defineProgress = "";
                    doneKey = false;
                    key = "";
                    entry = "";
                } else {
                    //construct the key of the define statement
                    if (!doneKey) {
                        if (nextChar != ' ') {
                            key += nextChar;
                        } else if (!key.equals("")) {
                            //if the key is constructed and there is a break,
                            //  move on to definining the value
                            doneKey = true;
                        }
                    } else {
                        if (nextChar != ' ') {
                            entry += nextChar;
                        } else {
                            //if the value is interrupted, terminate the define
                            defineProgress = "";
                        }
                    }
                }

                if (!commentedLine) {
                    contents += nextChar;
                }

            } catch (EOFException e) {
                reading = false;
            }
        }

        commandFileStream.close();
        String commonPackage = null;
        //replace the defined keys with the defined values
        for (int i = 0; i < replacementMap.size(); i++) {
            MapEntry mapEntry = (MapEntry) replacementMap.elementAt(i);
            //COMMON_PACKAGE is a special case, and does not behave like other
            //  define entries
            if (mapEntry.key.equals("COMMON_PACKAGE")) {
                commonPackage = mapEntry.value;
                if (!commonPackage.equals("") && !commonPackage.endsWith(".")) {
                    commonPackage += ".";
                }
                printIfDebug("Common Package: " + commonPackage);
            } else {
                printIfDebug("Definition pair: " + mapEntry.key + " = " + mapEntry.value);
                for (int x = 0; x < contents.length() - mapEntry.key.length(); x++) {
                    if (contents.substring(x, x + mapEntry.key.length()).equals(mapEntry.key)) {
                        contents = contents.substring(0, x) + mapEntry.value + contents.substring(x + mapEntry.key.length());
                        x += mapEntry.value.length() - 1;
                    }
                }
            }
        }
        //this is a bit of a hacky way to return two Strings at once. Ideally
        //  there would be a separate class for this pair of values (the file
        //  contents and the common package, if it exists), but since the MapEntry
        //  is essentially identical to this, there's no reason to do so.
        return new MapEntry(contents, commonPackage);
    }

    private static void printIfDebug(String output) {
        if (DEBUG) {
            System.out.println(output);
        }
    }

    private static class DummyCommand extends Command {

        protected void initialize() {
        }

        protected void execute() {
        }

        protected boolean isFinished() {
            return true;
        }

        protected void end() {
        }

        protected void interrupted() {
        }

    }

    private static class MapEntry {

        public MapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }
}
