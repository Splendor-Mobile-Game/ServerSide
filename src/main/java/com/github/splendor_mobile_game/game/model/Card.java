package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;

public class Card {

    private final CardTier cardTier;

    private TokenType additionalToken = null;

    private final int points;

    private final int emeraldCost;
    private final int sapphireCost;
    private final int rubyCost;
    private final int diamondCost;
    private final int onyxCost;


    public Card(CardTier cardTier, int points, int emeraldCost, int sapphireCost, int rubyCost, int diamondCost, int onyxCost) {
        this.cardTier     = cardTier;
        this.points       = points;
        this.emeraldCost  = emeraldCost;
        this.sapphireCost = sapphireCost;
        this.rubyCost     = rubyCost;
        this.diamondCost  = diamondCost;
        this.onyxCost     = onyxCost;
    }

    public CardTier getCardTier() {
        return cardTier;
    }

    public TokenType getAdditionalToken() {
        return additionalToken;
    }

    public Card setAdditionalToken(TokenType tokenType) {
        this.additionalToken = tokenType;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public int getEmeraldCost() {
        return emeraldCost;
    }

    public int getSapphireCost() {
        return sapphireCost;
    }

    public int getRubyCost() {
        return rubyCost;
    }

    public int getDiamondCost() {
        return diamondCost;
    }

    public int getOnyxCost() {
        return onyxCost;
    }
}
