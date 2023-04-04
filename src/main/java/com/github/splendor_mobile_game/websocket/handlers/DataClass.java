package com.github.splendor_mobile_game.websocket.handlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a class as a data class.
 * It is used to indicate that the class is a simple data container
 * and should be treated as such. Also, a class annotated with it is used
 * as a definition for an input provided by the client. The client must conform
 * to the class annotated with it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataClass {

}
