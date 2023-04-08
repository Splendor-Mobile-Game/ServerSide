package com.github.splendor_mobile_game.game.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.github.splendor_mobile_game.game.Exceptions.NotEnoughBonusPointsException;
import com.github.splendor_mobile_game.game.Exceptions.NotEnoughTokensException;
import com.github.splendor_mobile_game.game.Exceptions.SameTokenTypesException;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.websocket.utils.Log;

public class User {

    private final String name;

    private UUID uuid;

    private int connectionHasCode;

    private int points;

    //initialized tokens hashmap
    private Map<TokenType, Integer> tokens = new HashMap<TokenType, Integer>();

    //hashmap showing how many Bonuses user has
    private Map<TokenType, Integer> cardBonuses = new HashMap<TokenType, Integer>();

    //initialized purchased and reserved cards lists
    private ArrayList<Card> purchasedCards = new ArrayList<Card>();
    private ArrayList<Card> reservedCards = new ArrayList<Card>();

    //initialized nobles list
    private ArrayList<Noble> visitingNobles = new ArrayList<Noble>();

    public User(UUID uuid, String name, int connectionHasCode) {
        this.uuid = uuid;
        this.name = name;
        this.connectionHasCode = connectionHasCode;

        //putting every token type into hashmaps and setting its value to 0
        for (TokenType type : TokenType.values()) {
            this.tokens.put(type, 0);
            if(type != TokenType.GOLD_JOKER) this.cardBonuses.put(type, 0);
        }
    }

    //method returning how many tokens user has
    public int getTokenCount() {
        int result = 0;

        for(Map.Entry<TokenType, Integer> set : this.tokens.entrySet()) {
            result += set.getValue();
        }

        return result;
    }

    public int getTokenCount(TokenType type) {
        return this.tokens.get(type);
    }

    //method for adding two tokens (still needs to be validated if there are at least 4 tokens of this type on the table)
    public boolean takeTwoTokens(TokenType type) {
        this.tokens.put(type, this.tokens.get(type) + 2);

        if(this.getTokenCount() > 10) return false;
        return true;
    }


    //method for taking tokens from user
    public void putDownTokens(TokenType type, int amount) throws Exception {
        if(this.tokens.get(type) - amount < 0) throw new Exception("You can't have less than 0 tokens");
        this.tokens.put(type, this.tokens.get(type) - amount);
    }


    //method for adding three different tokens
    public boolean takeThreeTokens(TokenType type1, TokenType type2, TokenType type3) throws SameTokenTypesException {
        if(type1 == type2 || type1 == type3 || type2 == type3) throw new SameTokenTypesException("No three different token types selected");
        this.tokens.put(type1, this.tokens.get(type1) + 1);
        this.tokens.put(type2, this.tokens.get(type2) + 1);
        this.tokens.put(type3, this.tokens.get(type3) + 1);

        if(this.getTokenCount() > 10) return false;
        return true;
    }

    //method for buying cards
    public void buyCard(Card card) throws NotEnoughTokensException {

        int goldTokensUsed = 0;

        for(Map.Entry<TokenType, Integer> set : this.tokens.entrySet()) {
            if(set.getKey() == TokenType.GOLD_JOKER) continue;
            int tokensPlusBonuses = set.getValue() + this.cardBonuses.get(set.getKey());
            if(tokensPlusBonuses < card.getCost(set.getKey())) {
                int missingTokens = card.getCost(set.getKey()) - tokensPlusBonuses;
                if((this.getTokenCount(TokenType.GOLD_JOKER) - goldTokensUsed) >= missingTokens) {
                    goldTokensUsed += missingTokens;
                } else {
                    throw new NotEnoughTokensException("You don't have enough tokens to buy this card");
                }
            }
        }

        this.tokens.forEach((k,v) -> {
            if(k != TokenType.GOLD_JOKER) {
                int neededTokens = card.getCost(k) - this.cardBonuses.get(k);
                int changedValue = v > neededTokens ? v - neededTokens : 0;
                this.tokens.put(k, changedValue);
            }
        });

        this.tokens.put(TokenType.GOLD_JOKER, this.tokens.get(TokenType.GOLD_JOKER) - goldTokensUsed);

        this.cardBonuses.put(card.getAdditionalToken(), this.cardBonuses.get(card.getAdditionalToken()) + 1);

        this.updatePoints(card.getPoints());

    }

    public void takeNoble(Noble noble) throws NotEnoughBonusPointsException {
        for(Map.Entry<TokenType, Integer> set : this.cardBonuses.entrySet()) {
            if(set.getValue() < noble.getCost((set.getKey()))) throw new NotEnoughBonusPointsException("You don't have enough cards for this Noble to visit you");

            this.visitingNobles.add(noble);
            this.updatePoints(noble.getPoints());
        }
    }

    private void updatePoints(int points) {
        this.points += points;
    }

    public void reserveCard(Card card) {
        this.reservedCards.add(card);
        this.tokens.put(TokenType.GOLD_JOKER, this.tokens.get(TokenType.GOLD_JOKER) + 1);
    }

    public int getConnectionHasCode() {
        return connectionHasCode;
    }

    public void setConnectionHasCode(int connectionHasCode) {
        this.connectionHasCode = connectionHasCode;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) && uuid.equals(user.uuid) && connectionHasCode == user.getConnectionHasCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid, connectionHasCode);
    }
}
