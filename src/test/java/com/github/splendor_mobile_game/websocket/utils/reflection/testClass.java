package com.github.splendor_mobile_game.websocket.utils.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class testClass {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AnnotationA {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AnnotationB {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AnnotationC {}

    

    @AnnotationA
    public static class WithoutConstructor {}

    @AnnotationB
    public static class WithDefault {
        public WithDefault() {}
    }

    @AnnotationB
    public static class OneStringParameter {
        public OneStringParameter(String string) {}
    }

    public static class TwoStringParameters {
        public TwoStringParameters() {}

        public TwoStringParameters(String string1, String string2) {}
    }

    public static class MultipleParameters {
        public MultipleParameters(WithDefault withDefault, String string, int integer) {}

        public MultipleParameters(WithDefault withDefault) {}
    }

    public static class MyClass {
        private int x;
        private String str;

        public MyClass(int x, String str) {
            this.x = x;
            this.str = str;
        }

        public int getX() {
            return x;
        }

        public String getStr() {
            return str;
        }
    }
}
