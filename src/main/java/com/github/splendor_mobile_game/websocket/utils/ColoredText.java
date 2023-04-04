package com.github.splendor_mobile_game.websocket.utils;

/** A utility class for adding color to console output. */
public class ColoredText {

    /** ANSI escape code for resetting color. */
    private static final String RESET = "\u001B[0m";

    /** ANSI escape code for red color. */
    private static final String RED = "\u001B[31m";

    /** ANSI escape code for green color. */
    private static final String GREEN = "\u001B[32m";

    /** ANSI escape code for yellow color. */
    private static final String YELLOW = "\u001B[33m";

    /** ANSI escape code for blue color. */
    private static final String BLUE = "\u001B[34m";

    /**
     * Colorizes a string with the specified color.
     * @param s the string to colorize
     * @param color the color to apply
     * @return the colorized string
     */
    private static String colorize(String s, String color) {
        return color + s + RESET;
    }

    /**
     * Colorizes a string with red color.
     * @param s the string to colorize
     * @return the colorized string
     */
    public static String red(String s) {
        return colorize(s, RED);
    }

    /**
     * Colorizes a string with green color.
     * @param s the string to colorize
     * @return the colorized string
     */
    public static String green(String s) {
        return colorize(s, GREEN);
    }

    /**
     * Colorizes a string with yellow color.
     * @param s the string to colorize
     * @return the colorized string
     */
    public static String yellow(String s) {
        return colorize(s, YELLOW);
    }

    /**
     * Colorizes a string with blue color.
     * @param s the string to colorize
     * @return the colorized string
     */
    public static String blue(String s) {
        return colorize(s, BLUE);
    }

}
