package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.splendor_mobile_game.websocket.handlers.ServerMessageType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Deck;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.utils.Log;

import static  org.junit.jupiter.api.Assertions.*;

public class BuyRevealedMineTests {
    private Database database;

    private String newBaseMessage(String contextUuid) {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "BUY_REVEALED_MINE",
                    "data": {
                        "userDTO":{
                            "uuid": "$userUuid"
                        },
                        "cardDTO":{
                            "uuid": "$cardUuid"
                        }
                    }
                }
                """.replace("$contextUuid", contextUuid);
    }

    private String newBaseErrorMessage(String contextUuid) {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "BUY_REVEALED_MINE_RESPONSE",
                    "result": "FAILURE",
                    "data": {
                        "error": "$error"
                    }
                }
                """.replace("$contextUuid", contextUuid);
    }

    @Test
    public void test1(){
        Log.DEBUG("TEST 1");

        // 1. Setup data for the test
        
        int clientConnectionHashCode = 714239;
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "Janek", clientConnectionHashCode);
        database.addUser(owner);
        Room room = new Room(UUID.randomUUID(), "Pokoj", "123", owner, database);
        database.addRoom(room);

        User player = new User(UUID.randomUUID(), "Rodrigo", 1234);
        database.addUser(player);
        room.joinGame(player);
        room.startGame();
        
        Game game = room.getGame();
        Card cardToBuy=null;
        try{
            Field privateField = Game.class.getDeclaredField("revealedCards");
            privateField.setAccessible(true);
            Map<CardTier,Deck> revealedDecks = (HashMap<CardTier,Deck>)privateField.get(game);
            cardToBuy = revealedDecks.get(CardTier.LEVEL_1).get(0);

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            Map<TokenType, Integer> tokens = (HashMap<TokenType,Integer>)privateTokens.get(owner);

            tokens.put(TokenType.RUBY,cardToBuy.getCost(TokenType.RUBY)+2);
            tokens.put(TokenType.EMERALD,cardToBuy.getCost(TokenType.EMERALD)+1);
            tokens.put(TokenType.SAPPHIRE,cardToBuy.getCost(TokenType.SAPPHIRE));
            tokens.put(TokenType.DIAMOND,cardToBuy.getCost(TokenType.DIAMOND));
            tokens.put(TokenType.ONYX,cardToBuy.getCost(TokenType.ONYX)+1);

        }catch (Exception e){
            e.printStackTrace();
        }
        

        String contextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "BUY_MINE";
        String userUuid = owner.getUuid().toString();
        String cardUuid = cardToBuy.getUuid().toString();

        String message = """
        {
            "contextId": "$contextId",
            "type": "$type",
            "data": {
                "userDTO": {
                    "uuid": "$userId"
                },
                "cardDTO": {
                    "uuid": "$cardId"
                }
            }
        }""".replace("$contextId", contextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid)
            .replace("$cardId", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        BuyRevealedMine buyRevealedMine = new BuyRevealedMine(clientConnectionHashCode, receivedMessage, messenger, database);

        Log.DEBUG(message);


        // Parse the data given in the message
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        buyRevealedMine.react();
 
        // 3. Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        Log.DEBUG(serverReply);

        // 4. Check that return value and side effects of this call is correct
        // TO DO
    }

    @Test
    public void test2(){
        // 1. Setup data for the test
        Log.DEBUG("TEST 2");
        
        int clientConnectionHashCode = 7164239;
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();

        User owner = new User(UUID.randomUUID(), "Janeek", clientConnectionHashCode);
        database.addUser(owner);
        Room room = new Room(UUID.randomUUID(), "Pokojj", "1423", owner, database);
        database.addRoom(room);

        User player = new User(UUID.randomUUID(), "Rodrrigo", 12634);
        database.addUser(player);
        room.joinGame(player);
        room.startGame();
        
        Game game = room.getGame();
        Card cardToBuy=null;
        try{
            Field privateFieldDecks = Game.class.getDeclaredField("decks");
            privateFieldDecks.setAccessible(true);
            Map<CardTier,Deck> decks = (HashMap<CardTier,Deck>)privateFieldDecks.get(game);
          
            decks.get(CardTier.LEVEL_1).removeAll(decks.get(CardTier.LEVEL_1));
            
            Field privateFieldRevealedCards = Game.class.getDeclaredField("revealedCards");
            privateFieldRevealedCards.setAccessible(true);
            Map<CardTier,Deck> revealedCards = (HashMap<CardTier,Deck>)privateFieldRevealedCards.get(game);

            for(int i=0; i<3;i++){
                revealedCards.get(CardTier.LEVEL_1).remove(0);
            }

            cardToBuy = revealedCards.get(CardTier.LEVEL_1).get(0);

            Field privateFieldTokens = User.class.getDeclaredField("tokens");
            privateFieldTokens.setAccessible(true);
            Map<TokenType, Integer> tokens = (HashMap<TokenType,Integer>)privateFieldTokens.get(owner);

            tokens.put(TokenType.RUBY,cardToBuy.getCost(TokenType.RUBY)+2);
            tokens.put(TokenType.EMERALD,cardToBuy.getCost(TokenType.EMERALD)+1);
            tokens.put(TokenType.SAPPHIRE,cardToBuy.getCost(TokenType.SAPPHIRE));
            tokens.put(TokenType.DIAMOND,cardToBuy.getCost(TokenType.DIAMOND));
            tokens.put(TokenType.ONYX,cardToBuy.getCost(TokenType.ONYX)+1);

        }catch (Exception e){
            e.printStackTrace();
        }

        String contextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "BUY_MINE";
        String userUuid = owner.getUuid().toString();
        String cardUuid = cardToBuy.getUuid().toString();

        String message = """
        {
            "contextId": "$contextId",
            "type": "$type",
            "data": {
                "userDTO": {
                    "uuid": "$userId"
                },
                "cardDTO": {
                    "uuid": "$cardId"
                }
            }
        }""".replace("$contextId", contextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid)
            .replace("$cardId", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        BuyRevealedMine buyRevealedMine = new BuyRevealedMine(clientConnectionHashCode, receivedMessage, messenger, database);

        Log.DEBUG(message);

        // Parse the data given in the message
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        // 2. Call the function you are testing
        buyRevealedMine.react();
 
        // 3. Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        Log.DEBUG(serverReply);
    }

    @Test
    public void invalidUserUuidTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "invalid-uuid";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", userUuid)
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class));
    }

    @Test
    public void invalidCardUuidTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";
        String cardUuid = "invalid-uuid";

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", userUuid)
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class));
    }

    @Test
    public void userNotFoundTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", userUuid)
                .replace("$cardUuid", cardUuid);

        int clientConnectionHashCode = 100000;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "Couldn't find a user with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void userNotInRoomTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User user = new User(UUID.randomUUID(), "PLAYER", 100000);
        this.database.addUser(user);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", user.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(user.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(user.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "You are not a member of any room!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void gameNotStartedTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        this.database.addUser(owner);
        this.database.addRoom(room);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "The game hasn't started yet!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void invalidTurnTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", player.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "It is not your turn!");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void cardNotFoundTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";
        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", cardUuid);

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "Couldn't find a card with given UUID.");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void cardNotRevealedTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        Card card = new Card(CardTier.LEVEL_1, 0, 0,0,0,0,0, TokenType.EMERALD, 3);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);
        this.database.getAllCards().add(card);

        room.startGame();
        room.getGame().getRevealedCards(CardTier.LEVEL_1).remove(card);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", card.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage(messageUuid)
                .replace("$error", "The card is not in the revealed deck");

        String reply = messenger.getMessages().get(0).getMessage();

        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void validRequestTest() {
        this.database = new InMemoryDatabase();

        String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        Card card = room.getGame().getRevealedCards(CardTier.LEVEL_1).get(0);
        Map<TokenType, Integer> tokens = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            if (type == TokenType.GOLD_JOKER) continue;
            tokens.put(type, card.getCost(type));
        }
        owner.changeTokens(tokens);

        String message = this.newBaseMessage(messageUuid)
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$cardUuid", card.getUuid().toString());

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        BuyRevealedMine brm = new BuyRevealedMine(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(BuyRevealedMine.DataDTO.class);
        brm.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertFalse(room.getGame().getRevealedCards(CardTier.LEVEL_1).contains(card));
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        JsonObject jsonObject = JsonParser.parseString(reply).getAsJsonObject();
        assertEquals(ServerMessageType.BUY_REVEALED_MINE_ANNOUNCEMENT.toString(), jsonObject.get("type").getAsString());
        assertEquals(Result.OK.toString(), jsonObject.get("result").getAsString());
    }
}