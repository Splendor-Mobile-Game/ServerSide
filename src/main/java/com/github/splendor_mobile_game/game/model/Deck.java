package com.github.splendor_mobile_game.game.model;

import java.util.ArrayList;

import com.github.splendor_mobile_game.game.enums.CardTier;

public class Deck extends ArrayList<Card> {
    private final CardTier tier;

    public Deck(CardTier tier){
        super();
        this.tier=tier;
    }
    public Deck(CardTier tier,int size){
        super(size);
        this.tier=tier;
    }

    public Deck(CardTier tier,java.util.Collection<? extends Card> collection){
        super(collection);
        this.tier=tier;
    }

    public CardTier getTier(){
        return tier;
    }  
} 