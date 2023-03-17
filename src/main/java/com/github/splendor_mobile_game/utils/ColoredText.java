package com.github.splendor_mobile_game.utils;

public class ColoredText {

    public static String Red(String s) {
        return "\u001B[31m" + s + "\u001B[0m";
    }

    public static String Green(String s) {
        return "\u001B[32m" + s + "\u001B[0m";
    }

    public static String Yellow(String s) {
        return "\u001B[33m" + s + "\u001B[0m";
    }

    public static String Blue(String s) {
        return "\u001B[34m" + s + "\u001B[0m";
    }

}
