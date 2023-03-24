package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.game.enums.TokenType;

import java.util.UUID;

public class Token {

    private final UUID uuid;

    private final TokenType tokenType;

    private User owner = null;


    public Token(TokenType tokenType) {
        this.uuid = UUID.randomUUID();
        this.tokenType = tokenType;
    }

    public Token(TokenType tokenType, User owner) {
        this.uuid = UUID.randomUUID();
        this.tokenType = tokenType;
        this.owner = owner;
    }

    public UUID getUuid() {
        return uuid;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
