package com.github.splendor_mobile_game.game.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;

public class Card {
    private final UUID uuid;

    private final CardTier cardTier;

    private final TokenType additionalToken;

    private final int points;

    private final int cardID;

    // private final int emeraldCost;
    // private final int sapphireCost;
    // private final int rubyCost;
    // private final int diamondCost;
    // private final int onyxCost;

    private Map<TokenType, Integer> cost = new HashMap<TokenType, Integer>();


    public Card(CardTier cardTier, int points, int emeraldCost, int sapphireCost, int rubyCost, int diamondCost, int onyxCost, TokenType token, int cardID) {
        this.uuid         = UUID.randomUUID();
        this.cardTier     = cardTier;
        this.points       = points;

        this.cost.put(TokenType.EMERALD, emeraldCost);
        this.cost.put(TokenType.SAPPHIRE, sapphireCost);
        this.cost.put(TokenType.RUBY, rubyCost);
        this.cost.put(TokenType.DIAMOND, diamondCost);
        this.cost.put(TokenType.ONYX, onyxCost);

        this.cardID = cardID;

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

    public int getCardID() {
        return cardID;
    }

    public int getPoints() {
        return points;
    }

    // public int getEmeraldCost() {
    //     return emeraldCost;
    // }

    // public int getSapphireCost() {
    //     return sapphireCost;
    // }

    // public int getRubyCost() {
    //     return rubyCost;
    // }

    // public int getDiamondCost() {
    //     return diamondCost;
    // }

    // public int getOnyxCost() {
    //     return onyxCost;
    // }

    public int getCost(TokenType type) {
        return this.cost.get(type);
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %d %d %d %s", cardTier.toString(), points, this.cost.get(TokenType.EMERALD), this.cost.get(TokenType.SAPPHIRE),  this.cost.get(TokenType.RUBY),  this.cost.get(TokenType.DIAMOND),  this.cost.get(TokenType.ONYX),  additionalToken.toString());
    }
}
