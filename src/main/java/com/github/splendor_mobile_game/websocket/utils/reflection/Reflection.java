package com.github.splendor_mobile_game.websocket.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.splendor_mobile_game.websocket.utils.Log;

/** A utility class for reflection operations. */
public class Reflection {

    /**
     * Checks if a class has a default constructor.
     * 
     * @param clazz the class to check
     * @return true if the class has a default constructor, false otherwise
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return constructor.getParameterCount() == 0;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Checks if a class has a constructor with one parameter of a specific type.
     * 
     * @param clazz     the class to check
     * @param parameter the type of the parameter
     * @return true if the class has a constructor with one parameter of the specified type, false otherwise
     */
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

    /**
     * Gets a constructor of a class that matches a set of parameter types.
     * 
     * @param clazz      the class to get the constructor from
     * @param parameters the types of the parameters
     * @return the constructor that matches the parameter types
     * @throws NoSuchMethodException if no constructor matches the parameter types
     */
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
                Class<?> parameter1 = toPrimitiveType(constructorParameters[j]);
                Class<?> parameter2 = toPrimitiveType(parameterTypes[j]);
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

    /**
     * Finds all nested classes within a class that are annotated with a specific annotation.
     *
     * @param parent the class to search within
     * @param annotation the annotation to search for
     * @return a list of nested classes that are annotated with the specified annotation, or an empty list if no such classes exist
     */
    public static List<Class<?>> findClassesWithAnnotationWithinClass(Class<?> parent, Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClasses = new ArrayList<>();

        for (Class<?> clazz : parent.getDeclaredClasses()) {
            if (clazz.isAnnotationPresent(annotation)) {
                annotatedClasses.add(clazz);
            }
        }
        
        return annotatedClasses;
    }

    /**
     * Finds the first (alphabetically) nested class within a class that is annotated with a specific annotation.
     *
     * @param parent the class to search within
     * @param annotation the annotation to search for
     * @return the first nested class that is annotated with the specified annotation, or null if no such class exists
     */
    public static Class<?> findFirstClassWithAnnotationWithinClass(Class<?> parent, Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClasses = findClassesWithAnnotationWithinClass(parent, annotation);
        if (!annotatedClasses.isEmpty()) {
            return annotatedClasses.get(0);
        }
        return null;
    }

    /**
     * Creates an instance of a class using a constructor that matches a set of parameter types.
     * 
     * @param clazz           the class to create an instance of
     * @param constructorArgs the arguments to pass to the constructor
     * @return an instance of the class
     * @throws CannotCreateInstanceException if no constructor matches the parameter types or if an exception occurs while creating the instance
     */
    public static Object createInstanceOfClass(Class<?> clazz, Object... constructorArgs)
            throws CannotCreateInstanceException {
        Class<?>[] paremeterTypes;

        try {
            paremeterTypes = Reflection.getParameterTypes(constructorArgs);
            Constructor<?> constructor = Reflection.getConstructorWithParameters(clazz, paremeterTypes);
            return constructor.newInstance(constructorArgs);

        } catch (Exception e) {
            String message = clazz.getName() + " has no specified public constructor with types: "
                    + Reflection.getParameterTypesNames(constructorArgs);

            Log.ERROR(message);
            throw new CannotCreateInstanceException(message, e);
        }
    }

    /**
     * Returns an array of Class objects representing the parameter types of the given arguments.
     * @param args the arguments whose parameter types are to be determined
     * @return an array of Class objects representing the parameter types of the given arguments
     */
    private static Class<?>[] getParameterTypes(Object... args) {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
            parameterTypes[i] = Reflection.toPrimitiveType(args[i].getClass());

        return parameterTypes;
    }

    /**
     * Returns a string representation of the parameter types of the given arguments.
     * @param args the arguments whose parameter types are to be represented as a string
     * @return a string representation of the parameter types of the given arguments
     */
    private static String getParameterTypesNames(Object... args) {
        if (args.length == 0)
            return "";
        
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            stringBuilder.append(args[i].getClass().getName());
            stringBuilder.append(", ");
        }
        stringBuilder.append(args[args.length - 1].getClass().getName());
        return stringBuilder.toString();
    }

    /**
     * Returns the primitive type corresponding to the given class, if it exists.
     * @param clazz the class to be converted to a primitive type
     * @return the primitive type corresponding to the given class, if it exists; otherwise, the given class
     */
    private static Class<?> toPrimitiveType(Class<?> clazz) {
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
