package com.github.splendor_mobile_game.game;

import com.github.splendor_mobile_game.game.model.Card;

final public class ReservationResult {
    
    private final Card card;
    private final boolean goldenToken;

    public ReservationResult(Card card, boolean goldenToken) {
        this.card = card;
        this.goldenToken = goldenToken;
    }

    public Card getCard() {
        return card;
    }

    public boolean getGoldenToken() {
        return goldenToken;
    }
}
