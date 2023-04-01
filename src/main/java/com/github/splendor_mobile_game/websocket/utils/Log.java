package com.github.splendor_mobile_game.websocket.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

public class Log {

    public enum LogLevel{
        TRACE,
        INFO,
        DEBUG,
        WARNING,
        ERROR
    }

    private static EnumSet<LogLevel> fileLogLevels=EnumSet.allOf(LogLevel.class);
    private static EnumSet<LogLevel> consoleLogLevels=EnumSet.allOf(LogLevel.class);
    

    private static boolean savingToFile;
    // private String logsDir;
    private static File fileToWrite;

    public static void setSavingLogsToFile(String logsDir) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedTime = currentTime.format(formatter);
        Path path = Path.of(logsDir, formattedTime + ".txt");
        Log.fileToWrite = new File(path.toString());
        Log.fileToWrite.getParentFile().mkdirs();
        Log.savingToFile = true;  
    }

    public static boolean AddFileLogLevel(LogLevel LogLevel){
        return fileLogLevels.add(LogLevel);
    }

    public static boolean DeleteFileLogLevel(LogLevel LogLevel){
        return fileLogLevels.remove(LogLevel);
    }

    public static boolean AddConsoleLogLevel(LogLevel LogLevel){
        return consoleLogLevels.add(LogLevel);
    }

    public static boolean DeleteConsoleLogLevel(LogLevel LogLevel){
        return consoleLogLevels.remove(LogLevel);
    }

    private static void saveToFile(String message) {
        try {
            FileWriter writer = new FileWriter(Log.fileToWrite, true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            System.err.println("Error occured while saving to the log file.");
            e.printStackTrace();
        }
    }

    private static String getTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        return formattedTime;
    }

    private static String getHeader() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement callingMethod = stackTrace[3]; // 0 - getStackTrace, 1 - currentThread, 2 - calling method
        String fileName = callingMethod.getFileName();
        int lineNumber = callingMethod.getLineNumber();
        String location = fileName + ":" + lineNumber;
        return "[" + Log.getTime() + "]" + " [" + stackTrace[2].getMethodName() + "] [" + location + "] ";
    }

    public static void TRACE(String message) {      
        String logMessage = Log.getHeader() + message;

        if(consoleLogLevels.contains(LogLevel.TRACE)){
            System.out.println(logMessage);
        }
        
        if (fileLogLevels.contains(LogLevel.TRACE) && Log.savingToFile) {
            Log.saveToFile(logMessage);
        }
    }

    public static void INFO(String message) {
        String logMessage = Log.getHeader() + message;

        if(consoleLogLevels.contains(LogLevel.INFO)){
            System.out.println(ColoredText.Green(logMessage));
        }

        if (fileLogLevels.contains(LogLevel.INFO) && Log.savingToFile) {
            Log.saveToFile(logMessage);
        }
    }

    public static void DEBUG(String message) {
        String logMessage = Log.getHeader() + message;
        if(consoleLogLevels.contains(LogLevel.DEBUG)){
            System.out.println(ColoredText.Blue(logMessage));
        }
        
        if (fileLogLevels.contains(LogLevel.DEBUG) && Log.savingToFile) {
            Log.saveToFile(logMessage);
        }
    }

    public static void WARNING(String message) {
        String logMessage = Log.getHeader() + message;
        if(consoleLogLevels.contains(LogLevel.WARNING)){
            System.out.println(ColoredText.Yellow(logMessage));
        }
        
        if (fileLogLevels.contains(LogLevel.WARNING) && Log.savingToFile) {
            Log.saveToFile(logMessage);
        }
    }

    public static void ERROR(String message) {
        String logMessage = Log.getHeader() + message;
        if(consoleLogLevels.contains(LogLevel.ERROR)){
            System.out.println(ColoredText.Red(logMessage));
        }
        
        if (fileLogLevels.contains(LogLevel.ERROR) && Log.savingToFile) {
            Log.saveToFile(logMessage);
        }
    }
}
