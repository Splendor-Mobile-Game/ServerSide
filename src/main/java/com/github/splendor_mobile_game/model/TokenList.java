package com.github.splendor_mobile_game.model;

import com.github.splendor_mobile_game.enums.TokenType;

import java.util.ArrayList;

public class TokenList {

    private ArrayList<Token> tokens;

    private TokenType tokenType;

    public TokenList(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean addToken(Token token) {
        if (tokenType != token.getTokenType()) return false;

        this.tokens.add(token);
        return true;
    }

}
