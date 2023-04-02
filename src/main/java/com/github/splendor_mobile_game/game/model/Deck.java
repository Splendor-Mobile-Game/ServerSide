package com.github.splendor_mobile_game.game.model;

import java.util.ArrayList;

import com.github.splendor_mobile_game.game.enums.CardTier;

public class Deck extends ArrayList<Card> {
    private final CardTier tier;
    private final int maxSize;

    public Deck(CardTier tier){
        super();
        this.tier=tier;
        this.maxSize=whatSize(tier);
    }
    public Deck(CardTier tier,int size){
        super(size);
        this.tier=tier;
        this.maxSize=whatSize(tier);
    }

    public Deck(CardTier tier,java.util.Collection<? extends Card> array){
        super(array);
        this.tier=tier;
        this.maxSize=whatSize(tier);
    }

    public CardTier GetTier(){
        return tier;
    }

    public int MaxSize(){
        return maxSize;
    }

    public static int whatSize(CardTier tier){
        switch(tier){
            case LEVEL_1:
                return 40;
            case LEVEL_2:
                return 30;
            case LEVEL_3:
                return 20;
            default:
                return 0;
        }
    }
    
}
