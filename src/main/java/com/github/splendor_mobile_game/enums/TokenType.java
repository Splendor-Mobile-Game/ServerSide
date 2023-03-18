package com.github.splendor_mobile_game.enums;

public enum TokenType {

    EMERALD(Color.GREEN),
    SAPPHIRE(Color.BLUE),
    RUBY(Color.RED),
    DIAMOND(Color.WHITE),
    ONYX(Color.BLACK),
    GOLD_JOKER(Color.GOLD);

    public final Color color;

    private TokenType(Color color) {
        this.color = color;
    }

}
