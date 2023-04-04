package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.game.enums.TokenType;

import java.util.ArrayList;

public class TokenList {

    private ArrayList<Token> tokens;

    private final TokenType tokensType;

    private int availableTokens = 7;

    public TokenList(TokenType tokensType) {
        this.tokensType = tokensType;
        this.tokens = new ArrayList<Token>();
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

        // TODO 7 is not a valid value for every number of players. Value of 7 is only valid for game with 4 players.
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
                return token;
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
