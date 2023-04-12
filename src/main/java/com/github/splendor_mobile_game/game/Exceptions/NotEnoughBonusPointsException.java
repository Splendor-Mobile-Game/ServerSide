package com.github.splendor_mobile_game.game.Exceptions;

public class NotEnoughBonusPointsException extends Exception {

    public NotEnoughBonusPointsException() {
    }

    public NotEnoughBonusPointsException(String message) {
        super(message);
    }

    public NotEnoughBonusPointsException(Throwable cause) {
        super(cause);
    }

    public NotEnoughBonusPointsException(String message, Throwable cause) {
        super(message, cause);
    }

}
