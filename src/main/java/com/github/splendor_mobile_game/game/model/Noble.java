package com.github.splendor_mobile_game.game.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.splendor_mobile_game.game.enums.TokenType;

public class Noble {

    //Noble -> Arystokrata, Magnat
    private final UUID uuid;

    private final int graphicsID;

    // private final int emeraldCost; // Green
    // private final int sapphireCost;  // Blue
    // private final int rubyCost;  // Red
    // private final int diamondCost;  // White
    // private final int onyxCost;  // Black

    private Map<TokenType, Integer> cost = new HashMap<TokenType, Integer>();

    public Noble(int emeraldCost, int sapphireCost, int rubyCost, int diamondCost, int onyxCost, int graphicsID) {
        this.uuid = UUID.randomUUID();

        this.cost.put(TokenType.EMERALD, emeraldCost);
        this.cost.put(TokenType.SAPPHIRE, sapphireCost);
        this.cost.put(TokenType.RUBY, rubyCost);
        this.cost.put(TokenType.DIAMOND, diamondCost);
        this.cost.put(TokenType.ONYX, onyxCost);

        this.graphicsID = graphicsID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getGraphicsID() {
        return graphicsID;
    }

    public int getPoints() {
        return 3;
    }

    public int getCost(TokenType type) {
        return this.cost.get(type);
    }
}
