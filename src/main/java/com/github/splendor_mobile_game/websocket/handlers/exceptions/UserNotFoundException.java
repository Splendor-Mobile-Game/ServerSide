package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/** User not found exception, used when userUuid in request doesn't match any in database */
public class UserNotFoundException extends Exception {

   public UserNotFoundException() {
   }

   public UserNotFoundException(String message) {
       super(message);
   }

   public UserNotFoundException(Throwable cause) {
       super(cause);
   }

   public UserNotFoundException(String message, Throwable cause) {
       super(message, cause);
   }

}