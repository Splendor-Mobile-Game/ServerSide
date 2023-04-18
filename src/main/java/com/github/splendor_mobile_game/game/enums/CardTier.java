package com.github.splendor_mobile_game.game.enums;

public enum CardTier {

    LEVEL_1,
    LEVEL_2,
    LEVEL_3;

    public static CardTier fromInt(int value) {
        switch (value) {
            case 1:
                return LEVEL_1;
            case 2:
                return LEVEL_2;
            case 3:
                return LEVEL_3;
            default:
                throw new IllegalArgumentException("Invalid integer value for CardTier: " + value);
        }
    }
}
