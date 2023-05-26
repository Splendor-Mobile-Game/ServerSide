package com.github.splendor_mobile_game.game.model;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.ReservationResult;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.exceptions.NotEnoughTokensException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.CardDoesntExistException;
import com.github.splendor_mobile_game.websocket.utils.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    private Database database;

    private final int tierOneCardCount = 40;
    private final int tierTwoCardCount = 30;
    private final int tierThreeCardCount = 20;

    @BeforeEach
    public void setup() {
        this.database = new InMemoryDatabase();
    }

    @Test
    public void startGameTest() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        for (TokenType type : TokenType.values()) {
            if (type == TokenType.GOLD_JOKER) assertEquals(5,game.getTokenCount(type));
            else assertEquals(4, game.getTokenCount(type));
        }

        for (CardTier tier : CardTier.values()) {
            assertEquals(4, game.getRevealedCards(tier).size());
        }

        assertEquals(3, game.getNobles().size());
    }

    @Test
    public void reserveCardFromDeckTest() throws CardDoesntExistException {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        ReservationResult result = game.reserveCardFromDeck(CardTier.LEVEL_1, owner);
        assertTrue(result.getGoldenToken());
        assertEquals(1, game.getGameReservationCount());
        assertEquals(1, owner.getReservedCards().size());
        assertEquals(result.getCard(), owner.getReservedCards().get(0));
    }

    @Test
    public void reserveNonexistentCardFromDeckTest() throws CardDoesntExistException {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        ReservationResult result = null;
        for (int index = 0; index < this.tierThreeCardCount - 4; index++) {
            result = game.reserveCardFromDeck(CardTier.LEVEL_3, owner);
            assertTrue(owner.getReservedCards().contains(result.getCard()));
        }
        assertFalse(result.getGoldenToken());
        assertEquals(16, owner.getReservedCards().size());

        assertThrows(CardDoesntExistException.class, () -> game.reserveCardFromDeck(CardTier.LEVEL_3, owner));
    }

    @Test
    public void reserveCardFromTableTest() throws CardDoesntExistException{
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        Card card = game.getRevealedCards(CardTier.LEVEL_1).get(0);
        ReservationResult result = game.reserveCardFromTable(card, owner);
        assertTrue(result.getGoldenToken());
        assertNotEquals(card, result.getCard());
        assertEquals(1, game.getGameReservationCount());
        assertEquals(1, owner.getReservedCards().size());
        assertEquals(card, owner.getReservedCards().get(0));
    }

    @Test
    public void reserveNonexistentCardFromTableTest() throws CardDoesntExistException {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        ReservationResult result = null;
        for (int index = 0; index < this.tierThreeCardCount; index++) {
            Card card = game.getRevealedCards(CardTier.LEVEL_3).get(0);
            result = game.reserveCardFromTable(card, owner);
            assertTrue(owner.getReservedCards().contains(card));
        }
        assertFalse(result.getGoldenToken());
        assertEquals(20, owner.getReservedCards().size());
        assertEquals(0, game.getRevealedCards(CardTier.LEVEL_3).size());

        Card card = result.getCard();
        assertNull(card);
        assertThrows(CardDoesntExistException.class, () -> game.reserveCardFromTable(card, owner));
    }

    @Test
    public void takeCardFromRevealedTest() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        assertFalse(game.getRevealedCards(CardTier.LEVEL_3).isEmpty());
        Card card = game.getRevealedCards(CardTier.LEVEL_3).get(0);
        assertTrue(game.isCardRevealed(card.getUuid()));
        Card newCard = game.takeCardFromRevealed(card);
        assertNotEquals(card, newCard);
        assertFalse(game.isCardRevealed(card.getUuid()));
        assertTrue(game.isCardRevealed(newCard.getUuid()));

        card = newCard;
        for (int index = 0; index < this.tierThreeCardCount - 5; index++) {
            newCard = game.takeCardFromRevealed(card);
            assertNotEquals(card, newCard);
            assertFalse(game.isCardRevealed(card.getUuid()));
            assertTrue(game.isCardRevealed(newCard.getUuid()));
            card = newCard;
        }

        Card nullCard = game.takeCardFromRevealed(card);
        assertNull(nullCard);
        assertFalse(game.isCardRevealed(card.getUuid()));
    }

    @Test
    public void changeTokensTest() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        Random random = new Random();
        Map<TokenType, Integer> tokens = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            if (type == TokenType.GOLD_JOKER) continue;
            tokens.put(type, random.nextInt(3));
        }

        game.changeTokens(tokens);
        for (TokenType type : TokenType.values()) {
            if (type == TokenType.GOLD_JOKER) continue;
            assertEquals(4 - tokens.get(type), game.getTokens(type));
        }
    }

    @Test
    public void testTest() throws NotEnoughTokensException {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User joiner = new User(UUID.randomUUID(), "JOINER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.joinGame(joiner);

        this.database.addUser(owner);
        this.database.addUser(joiner);
        this.database.addRoom(room);

        room.startGame();
        Game game = room.getGame();

        Log.DEBUG("users = " + game.users.size());
        Log.DEBUG("owner ranking = " + game.getUserRanking(owner.getUuid()));
        Card card = new Card(CardTier.LEVEL_1, 10, 0,0,0,0,0, TokenType.ONYX, 0);
        joiner.buyCard(card);
        Log.DEBUG("owner ranking = " + game.getUserRanking(owner.getUuid()));
    }
}