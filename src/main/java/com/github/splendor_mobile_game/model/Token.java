package com.github.splendor_mobile_game.model;

import com.github.splendor_mobile_game.enums.TokenType;

public class Token {

    private final TokenType tokenType;

    private int count;

    private User owner = null;


    public Token(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public Token(TokenType tokenType, User owner) {
        this.tokenType = tokenType;
        this.owner = owner;
    }


    public TokenType getTokenType() {
        return tokenType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
