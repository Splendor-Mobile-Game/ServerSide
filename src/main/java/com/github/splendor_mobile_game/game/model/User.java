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
