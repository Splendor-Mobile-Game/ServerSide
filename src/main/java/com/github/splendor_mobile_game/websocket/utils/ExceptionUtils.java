package com.github.splendor_mobile_game.websocket.utils;

/**
 * This class provides utility methods for working with exceptions.
 */
public class ExceptionUtils {
    /**
     * Returns the stack trace of the given exception as a string.
     * 
     * @param exception the exception to get the stack trace from
     * @return the stack trace as a string
     */
    public static String getStackTrace(Throwable exception) {
        StringBuilder sb = new StringBuilder();
        
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.getClassName())
              .append(".")
              .append(element.getMethodName())
              .append("(")
              .append(element.getFileName())
              .append(":")
              .append(element.getLineNumber())
              .append(")")
              .append("\n");
        }

        return sb.toString();
    }

}
