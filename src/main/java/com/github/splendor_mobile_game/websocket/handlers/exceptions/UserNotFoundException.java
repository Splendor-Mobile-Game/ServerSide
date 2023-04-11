package com.github.splendor_mobile_game.websocket.handlers.exceptions;

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