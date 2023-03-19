package com.github.splendor_mobile_game.database;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;

import java.util.ArrayList;

public class CardDatabase {



    public static ArrayList<Card> getAllCards(CardTier cardTier) {
        ArrayList<Card> array = new ArrayList<>();

        if (cardTier == CardTier.LEVEL_1) {
            array.add(new Card(CardTier.LEVEL_1, 0, 2, 2, 0, 0, 0));
            array.add(new Card(CardTier.LEVEL_1, 2, 1, 0, 1, 1, 0).setAdditionalToken(TokenType.SAPPHIRE));
            array.add(new Card(CardTier.LEVEL_1, 1, 0, 0, 3, 3, 3));
            array.add(new Card(CardTier.LEVEL_1, 0, 0, 0, 2, 1, 2).setAdditionalToken(TokenType.RUBY));
            array.add(new Card(CardTier.LEVEL_1, 2, 0, 0, 2, 1, 2).setAdditionalToken(TokenType.DIAMOND));
            array.add(new Card(CardTier.LEVEL_1, 1, 1, 2, 0, 1, 0));
            array.add(new Card(CardTier.LEVEL_1, 1, 2, 3, 3, 0, 0));
        }

        return array;
    }



}
