package com.github.splendor_mobile_game.game.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.TokenType;

public class User {

    private final String name;

    private UUID uuid;

    private int connectionHasCode;

    //initialized tokens hashmap
    private Map<TokenType, Integer> tokens = new HashMap<TokenType, Integer>();

    public User(UUID uuid, String name, int connectionHasCode) {
        this.uuid = uuid;
        this.name = name;
        this.connectionHasCode = connectionHasCode;

        //putting every token type into the hashmap and setting its value to 0
        for (TokenType type : TokenType.values()) {
            tokens.put(type, 0);
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

    //method which is adding two tokens
    public void takeTwoTokens(TokenType type) throws Exception {
        if(this.getTokenCount() > 8) throw new Exception("You can't have more than 10 tokens");
        this.tokens.put(type, this.tokens.get(type) + 2);
    }

    //method which is taking tokens from user
    public void putDownTokens(TokenType type, int amount) throws Exception {
        if(this.tokens.get(type) - amount < 0) throw new Exception("You can't have less than 0 tokens");

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
