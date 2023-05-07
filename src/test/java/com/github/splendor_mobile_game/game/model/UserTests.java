package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.game.Exceptions.NotEnoughBonusPointsException;
import com.github.splendor_mobile_game.game.Exceptions.NotEnoughTokensException;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {
    private final String userName = "USER";
    private final String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
    private final int userConnectionHashCode = 100000;

    @Test
    public void buyCardInsufficientTokensTest() {
        User user = new User(UUID.fromString(this.userUuid), this.userName, this.userConnectionHashCode);
        Card card = new Card(CardTier.LEVEL_1, 10,3,0,0,0,0, TokenType.EMERALD);

        Throwable throwable = assertThrows(NotEnoughTokensException.class, () -> user.buyCard(card));
        assertEquals("You don't have enough tokens to buy this card", throwable.getMessage());
    }

    private Map<TokenType, Integer> newTokenMap() {
        Map<TokenType, Integer> tokens = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            if (type == TokenType.GOLD_JOKER) continue;
            tokens.put(type, 0);
        }
        return tokens;
    }

    @Test
    public void successfulCardPurchaseTest() throws NotEnoughTokensException{
        User user = new User(UUID.fromString(this.userUuid), this.userName, this.userConnectionHashCode);
        Card card = new Card(CardTier.LEVEL_1, 10,3,0,0,0,0, TokenType.EMERALD);

        Map<TokenType, Integer> tokens = this.newTokenMap();
        tokens.put(TokenType.EMERALD, 3);

        user.changeTokens(tokens);
        assertEquals(3, user.getTokenCount(TokenType.EMERALD));
        assertEquals(0, user.getTokenCount(TokenType.SAPPHIRE));

        user.buyCard(card);
        assertEquals(1, user.getNumberOfPurchesedCards());
        assertEquals(0, user.getTokenCount(TokenType.EMERALD));
        assertEquals(card.getPoints(), user.getPoints());
    }

    @Test
    public void successfulCardPurchaseWithGoldTokenTest() throws NotEnoughTokensException{
        User user = new User(UUID.fromString(this.userUuid), this.userName, this.userConnectionHashCode);
        Card card = new Card(CardTier.LEVEL_1, 10,3,0,0,0,0, TokenType.EMERALD);

        Map<TokenType, Integer> tokens = this.newTokenMap();
        tokens.put(TokenType.EMERALD, 2);

        user.changeTokens(tokens);
        assertEquals(2, user.getTokenCount(TokenType.EMERALD));
        assertEquals(0, user.getTokenCount(TokenType.GOLD_JOKER));

        user.reserveCard(card, true);
        assertEquals(1, user.getReservationCount());
        assertEquals(1, user.getTokenCount(TokenType.GOLD_JOKER));

        user.buyCard(card);
        assertEquals(1, user.getNumberOfPurchesedCards());
        assertEquals(0, user.getTokenCount(TokenType.EMERALD));
        assertEquals(0, user.getTokenCount(TokenType.GOLD_JOKER));
        assertEquals(card.getPoints(), user.getPoints());
    }

    @Test
    public void takeNobleInsufficientTokensTest() {
        User user = new User(UUID.fromString(this.userUuid), this.userName, this.userConnectionHashCode);
        Noble noble = new Noble(1,0,0,0,0);

        assertFalse(user.takeNoble(noble));
        assertEquals(0, user.getPoints());
    }

    @Test
    public void successfulTakeNobleTest() throws NotEnoughTokensException {
        User user = new User(UUID.fromString(this.userUuid), this.userName, this.userConnectionHashCode);
        Card card = new Card(CardTier.LEVEL_1, 3,0,0,0,0,0, TokenType.EMERALD);

        user.buyCard(card);
        assertEquals(1, user.getNumberOfPurchesedCards());
        assertEquals(3, user.getPoints());

        Noble noble = new Noble(1,0,0,0,0);
        assertTrue(user.takeNoble(noble));
        assertEquals(6, user.getPoints());
    }
}