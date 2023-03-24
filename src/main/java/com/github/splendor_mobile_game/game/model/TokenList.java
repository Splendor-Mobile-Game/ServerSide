package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.game.enums.TokenType;

import java.util.ArrayList;

public class TokenList {

    private ArrayList<Token> tokens;

    private final TokenType tokensType;

    private int availableTokens = 7;

    public TokenList(TokenType tokensType) {
        this.tokensType = tokensType;
    }

    public void addToken(Token token) {
        if (tokensType != token.getTokenType()) return;

        this.tokens.add(token);
    }




    public int getAvailableTokensCount() {
        return availableTokens;
    }


    public int getEquippedTokensCount() {
        return 7 - availableTokens;
    }

    /**
     * Assign a token to user
     *
     * @param user -> player which will be the owner of available token.
     * @return Token object, including its uuid
     */
    public Token equipToken(User user) {
        for (Token token : tokens) {
            if (token.getOwner() == null) {
                token.setOwner(user);
                availableTokens--;
            }
        }
        return null;
    }


    /**
     * Dissociate a token from user
     *
     * @param user -> player which we are dissociating token from
     * @return Token object, including its uuid
     */
    public Token unequipToken(User user) {
        for (Token token : tokens) {
            if (token.getOwner().equals(user)) {
                token.setOwner(null);
                availableTokens++;
            }
        }
        return null;
    }


    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public TokenType getTokensType() {
        return tokensType;
    }
}
