package com.github.splendor_mobile_game.websocket.utils;

import java.lang.reflect.Constructor;

public class Reflection {
    public static boolean hasDefaultConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return constructor.getParameterCount() == 0;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean hasOneParameterConstructor(Class<?> clazz, Class<?> parameter) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameter);
            if (constructor.getParameterCount() != 1) {
                throw new NoSuchMethodException(clazz.getName() + " should have one parameter: " + parameter.getName());
            }
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
