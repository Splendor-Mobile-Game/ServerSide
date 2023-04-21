package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/**
 * Exception thrown when attempting to make reservation but user cannot
 */
public class UserReservationException extends Exception{
    public UserReservationException() {
    }

    public UserReservationException(String message) {
        super(message);
    }

    public UserReservationException(Throwable cause) {
        super(cause);
    }

    public UserReservationException(String message, Throwable cause) {
        super(message, cause);
    }   
}
