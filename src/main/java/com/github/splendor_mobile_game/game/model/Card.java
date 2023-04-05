package com.github.splendor_mobile_game.game.model;

import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;

public class Card {
    private final UUID uuid;

    private final CardTier cardTier;

    private final TokenType additionalToken;

    private final int points;

    private final int emeraldCost;
    private final int sapphireCost;
    private final int rubyCost;
    private final int diamondCost;
    private final int onyxCost;


    public Card(CardTier cardTier, int points, int emeraldCost, int sapphireCost, int rubyCost, int diamondCost, int onyxCost, TokenType token) {
        this.uuid         = UUID.randomUUID();
        this.cardTier     = cardTier;
        this.points       = points;
        this.emeraldCost  = emeraldCost;
        this.sapphireCost = sapphireCost;
        this.rubyCost     = rubyCost;
        this.diamondCost  = diamondCost;
        this.onyxCost     = onyxCost;
        this.additionalToken = token;
    }

    public UUID getUuid(){
        return uuid;
    }

    public CardTier getCardTier() {
        return cardTier;
    }

    public TokenType getAdditionalToken() {
        return additionalToken;
    }

    // public Card setAdditionalToken(TokenType tokenType) {
    //     this.additionalToken = tokenType;
    //     return this;
    // }

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

    @Override
    public String toString() {
        return String.format("%s %d %d %d %d %d %d %s", cardTier.toString(), points, emeraldCost, sapphireCost,  rubyCost,  diamondCost,  onyxCost,  additionalToken.toString());
    }
}
