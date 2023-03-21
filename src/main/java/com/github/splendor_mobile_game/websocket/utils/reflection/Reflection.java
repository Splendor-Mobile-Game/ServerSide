package com.github.splendor_mobile_game.websocket.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import com.github.splendor_mobile_game.websocket.utils.Log;

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

    public static Constructor<?> getConstructorWithParameters(Class<?> clazz, Class<?>... parameters) throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i];
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        
        for (int i = 0; i < constructors.length; i++) {
            Class<?>[] constructorParameters = constructors[i].getParameterTypes();

            if (constructorParameters.length != parameterTypes.length) {
                continue;
            }

            int j;
            for (j = 0; j < parameterTypes.length; j++) {
                Class<?> parameter1 = toSimpleType(constructorParameters[j]);
                Class<?> parameter2 = toSimpleType(parameterTypes[j]);
                if (!parameter1.isAssignableFrom(parameter2)) {
                    // Log.DEBUG(constructorParameters[j].getName() + "-" + parameters[j].getName());
                    break;
                }
            }
            
            if (j == parameterTypes.length) {
                return constructors[i];
            }
        }

        throw new NoSuchMethodException();
    }

    public static Class<?> findClassWithAnnotationWithinClass(Class<?> parent, Class<? extends Annotation> annotation) {
        for (Class<?> clazz : parent.getDeclaredClasses())
            if (clazz.isAnnotationPresent(annotation))
                return clazz;

        return null;
    }

    public static Object createInstanceOfClass(Class<?> clazz, Object... constructorArgs)
            throws CannotCreateInstanceException {
        Class<?>[] paremeterTypes;

        try {
            paremeterTypes = Reflection.getParameterTypes(constructorArgs);
            Constructor<?> constructor = Reflection.getConstructorWithParameters(clazz, paremeterTypes);
            return constructor.newInstance(constructorArgs);

        } catch (Exception e) {
            String message = clazz.getName() + " has no specified constructor with types: "
                    + Reflection.getParameterTypesNames(constructorArgs);

            Log.ERROR(message);
            throw new CannotCreateInstanceException(message, e);
        }
    }

    private static Class<?>[] getParameterTypes(Object... args) {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
            parameterTypes[i] = Reflection.toSimpleType(args[i].getClass());

        return parameterTypes;
    }

    private static String getParameterTypesNames(Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            stringBuilder.append(args[i].getClass().getName());
            stringBuilder.append(", ");
        }
        stringBuilder.append(args[args.length - 1].getClass().getName());
        return stringBuilder.toString();
    }

    private static Class<?> toSimpleType(Class<?> clazz) {
        if (clazz.equals(Integer.class))
            return int.class;
        if (clazz.equals(Long.class))
            return long.class;
        if (clazz.equals(Double.class))
            return double.class;
        if (clazz.equals(Float.class))
            return float.class;
        if (clazz.equals(Byte.class))
            return byte.class;
        if (clazz.equals(Short.class))
            return short.class;
        if (clazz.equals(Character.class))
            return char.class;
        if (clazz.equals(Boolean.class))
            return boolean.class;

        return clazz;
    }
}
