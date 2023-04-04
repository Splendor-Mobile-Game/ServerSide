package com.github.splendor_mobile_game.websocket.utils.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a field as optional when converting a JSON string to an object.
 * If a field is marked as optional and its value is not present in the JSON string, 
 * it will be set to null in the resulting object. Fields that are not annotated with Optional are required 
 * and if they are not present in the JSON string, an error will occur during deserialization
 * and the client will get an error response that some fields are missing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Optional {

}
