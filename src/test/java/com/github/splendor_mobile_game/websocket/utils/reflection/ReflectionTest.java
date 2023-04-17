package com.github.splendor_mobile_game.websocket.utils.reflection;

import static com.github.splendor_mobile_game.websocket.utils.reflection.Reflection.*;
import com.github.splendor_mobile_game.websocket.utils.reflection.testClass.OneStringParameter;
import com.github.splendor_mobile_game.websocket.utils.reflection.testClass.*;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.util.List;

/** This class has unit tests for Reflection. */
public class ReflectionTest {

    @Test
    public void testHasDefaultConstructor() {
        // Test without constructor
        assertTrue(hasDefaultConstructor(WithoutConstructor.class));

        // Test with default constructor
        assertTrue(hasDefaultConstructor(WithDefault.class));

        // Test with default constructor and non-default constructor
        assertTrue(hasDefaultConstructor(TwoStringParameters.class));

        // Test with non-default constructor
        assertFalse(hasDefaultConstructor(OneStringParameter.class));    

        // Test with multiple non-default constructors
        assertFalse(hasDefaultConstructor(MultipleParameters.class));    


    }



    @Test
    public void testHasOneParameterConstructor() {
        // Test with matching one-parameter constructor
        assertTrue(hasOneParameterConstructor(OneStringParameter.class, String.class));

        // Test with matching one-parameter constructor and multi-parameter constructor
        assertTrue(hasOneParameterConstructor(MultipleParameters.class, WithDefault.class));

        // Test with non-matching one-parameter constructor
        assertFalse(hasOneParameterConstructor(OneStringParameter.class, int.class));

        // Test with matching multi-parameter constructor
        assertFalse(hasOneParameterConstructor(TwoStringParameters.class, String.class));

        // Test with non-matching multi-parameter constructor
        assertFalse(hasOneParameterConstructor(TwoStringParameters.class, int.class));

        // Test with one matching and other non-matching multi-parameter constructor
        assertFalse(hasOneParameterConstructor(MultipleParameters.class, String.class));

        // Test with default constructor
        assertFalse(hasOneParameterConstructor(WithDefault.class, String.class));

    }



    @Test
    public void TestGetConstructorWithParameters() throws NoSuchMethodException {
        // Test with matching one-parameter constructor
        Constructor<OneStringParameter> oneStringParameterConstr = OneStringParameter.class.getDeclaredConstructor(String.class);
        assertEquals(getConstructorWithParameters(OneStringParameter.class, String.class), oneStringParameterConstr);

        // Test with matching homogeneous multi-parameter constructor
        Constructor<TwoStringParameters> twoStringParameterConstr = TwoStringParameters.class.getDeclaredConstructor(String.class, String.class);
        assertEquals(getConstructorWithParameters(TwoStringParameters.class, String.class, String.class), twoStringParameterConstr);

        // Test with matching diverse multi-parameter constructor
        Constructor<MultipleParameters> multipleParameterConstr = MultipleParameters.class.getDeclaredConstructor(WithDefault.class, String.class, int.class);
        assertEquals(getConstructorWithParameters(MultipleParameters.class, WithDefault.class, String.class, int.class), multipleParameterConstr);

        // Test without constructor
        Constructor<WithoutConstructor> withoutConstructorConstr = WithoutConstructor.class.getDeclaredConstructor();
        assertEquals(getConstructorWithParameters(WithoutConstructor.class), withoutConstructorConstr);

        // Test with only one matching parameter for multiple homogeneous parameters
        assertThrows(NoSuchMethodException.class, 
            () -> getConstructorWithParameters(TwoStringParameters.class, String.class));

        // Test with only one matching parameter for multiple diverse parameters
        assertThrows(NoSuchMethodException.class, 
            () -> getConstructorWithParameters(MultipleParameters.class, String.class));

        // Test with non-matching parameters
        assertThrows(NoSuchMethodException.class, 
            () -> getConstructorWithParameters(MultipleParameters.class, WithoutConstructor.class, TwoStringParameters.class));
    }

    

    @Test
    public void testFindFirstClassWithAnnotationWithinClass() {
        // Test with with matching annotation
        assertEquals(findFirstClassWithAnnotationWithinClass(testClass.class, AnnotationA.class), WithoutConstructor.class);

        // Test with first matching annotation
        assertEquals(findFirstClassWithAnnotationWithinClass(testClass.class, AnnotationB.class), OneStringParameter.class);

        // Test with non-matching annotation
        assertEquals(findFirstClassWithAnnotationWithinClass(testClass.class, AnnotationC.class), null);

        // Test with not first annotation
        assertNotEquals(findFirstClassWithAnnotationWithinClass(testClass.class, AnnotationB.class), WithoutConstructor.class);
    }



    @Test
    public void testFindClassesWithAnnotationWithinClass() {
        // Test with single matching annotation class
        assertEquals(findClassesWithAnnotationWithinClass(testClass.class, AnnotationA.class), List.of(WithoutConstructor.class));

        // Test with multiple matching annotation classes alphabetically
        assertEquals(findClassesWithAnnotationWithinClass(testClass.class, AnnotationB.class), List.of(OneStringParameter.class, WithDefault.class));

        // Test with non-matching annotation classes
        assertEquals(findClassesWithAnnotationWithinClass(testClass.class, AnnotationC.class), List.of());

        // Test with multiple matching annotation classes unalphabetically
        assertNotEquals(findClassesWithAnnotationWithinClass(testClass.class, AnnotationB.class), List.of(WithDefault.class, OneStringParameter.class));
    }



    @Test
    void testCreateInstanceOfClass() {
        // Test creating an instance with two valid arguments
        MyClass instance1 = (MyClass) Reflection.createInstanceOfClass(MyClass.class, 10, "Hello");
        assertEquals(10, instance1.getX());
        assertEquals("Hello", instance1.getStr());

        // Test creating an instance with one valid and one invalid argument
        assertThrows(CannotCreateInstanceException.class, 
            () -> createInstanceOfClass(MyClass.class, "invalid", 5));

        // Test creating an instance with two invalid arguments
        assertThrows(CannotCreateInstanceException.class, 
            () -> createInstanceOfClass(MyClass.class, "invalid", "invalid"));

        // Test creating an instance with default constructor
        assertEquals(WithDefault.class, createInstanceOfClass(WithDefault.class).getClass());

        // Test creating an instance without constructor
        assertEquals(WithoutConstructor.class, createInstanceOfClass(WithoutConstructor.class).getClass());
    }
}