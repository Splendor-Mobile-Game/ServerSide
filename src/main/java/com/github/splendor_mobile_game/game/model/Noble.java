package com.github.splendor_mobile_game.game.model;

public class Noble {

    //Noble -> Arystokrata, Magnat

    private final int emeraldCost; // Green
    private final int sapphireCost;  // Blue
    private final int rubyCost;  // Red
    private final int diamondCost;  // White
    private final int onyxCost;  // Black



    public Noble(int emeraldCost, int sapphireCost, int rubyCost, int diamondCost, int onyxCost) {
        this.emeraldCost  = emeraldCost;
        this.sapphireCost = sapphireCost;
        this.rubyCost     = rubyCost;
        this.diamondCost  = diamondCost;
        this.onyxCost     = onyxCost;
    }


    public int getPoints() {
        return 3;
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
