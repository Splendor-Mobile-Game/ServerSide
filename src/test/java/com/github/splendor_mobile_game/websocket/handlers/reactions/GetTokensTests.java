package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;
import com.github.splendor_mobile_game.game.model.Deck;
import com.github.splendor_mobile_game.game.model.Game;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetTokensTests {

    private Database database;
    private Map<TokenType, Integer> tokens;
    private final String messageUuid = "80bdc250-5365-4caf-8dd9-a33e709a0110";

    private String newBaseMessage() {
        return """
                {
                     "contextId": "$contextUuid",
                     "type": "GET_TOKENS",
                     "data": {
                         "userUuid": "$userUuid",
                         "tokensTakenDTO": {
                            "ruby": $rubyTaken,
                            "sapphire": $sapphireTaken,
                            "emerald": $emeraldTaken,
                            "diamond": $diamondTaken,
                            "onyx": $onyxTaken
                         },
                         "tokensReturnedDTO": {
                            "ruby": $rubyReturned,
                            "sapphire": $sapphireReturned,
                            "emerald": $emeraldReturned,
                            "diamond": $diamondReturned,
                            "onyx": $onyxReturned
                         }
                     }
                }""".replace("$contextUuid", this.messageUuid);
    }

    private String newBaseAnnouncement() {
        return """
            {
                "contextId": "$contextUuid",
                "type": "GET_TOKENS_RESPONSE",
                "result": "OK",
                "data":{
                    "data": {
                        "userUuid": "$userUuid",
                        "tokensTakenDTO": {
                        "ruby": $rubyTaken,
                        "sapphire": $sapphireTaken,
                        "emerald": $emeraldTaken,
                        "diamond": $diamondTaken,
                        "onyx": $onyxTaken
                        },
                        "tokensReturnedDTO": {
                        "ruby": $rubyReturned,
                        "sapphire": $sapphireReturned,
                        "emerald": $emeraldReturned,
                        "diamond": $diamondReturned,
                        "onyx": $onyxReturned
                        }
                    }
                }
            }""".replace("$contextUuid", messageUuid);
    }

    private String newBaseErrorMessage() {
        return """
                {
                    "contextId": "$contextUuid",
                    "type": "GET_TOKENS_RESPONSE",
                    "result": "FAILURE",
                    "data": {
                        "error": "$error"
                    }
                }
                """.replace("$contextUuid", this.messageUuid);
    }

    @BeforeEach
    public void setUp() {
        this.database = new InMemoryDatabase();
    }

    @Test
    public void validTokensRequest1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        
        String expectedJsonString = newBaseAnnouncement()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

            
            JsonElement actualJson = JsonParser.parseString(reply);
        
        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);

        int baseGameTokens = 4;
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.RUBY), room.getGame().getTokens(TokenType.RUBY));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.SAPPHIRE), room.getGame().getTokens(TokenType.SAPPHIRE));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.EMERALD), room.getGame().getTokens(TokenType.EMERALD));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.DIAMOND), room.getGame().getTokens(TokenType.DIAMOND));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.ONYX), room.getGame().getTokens(TokenType.ONYX));
    }

    @Test
    public void validTokensRequest2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 8);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(1))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        
        String expectedJsonString = newBaseAnnouncement()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(1))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

            
            JsonElement actualJson = JsonParser.parseString(reply);
        
        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);

        int baseGameTokens = 4;
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.RUBY) + 8, room.getGame().getTokens(TokenType.RUBY));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.SAPPHIRE), room.getGame().getTokens(TokenType.SAPPHIRE));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.EMERALD), room.getGame().getTokens(TokenType.EMERALD));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.DIAMOND), room.getGame().getTokens(TokenType.DIAMOND));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.ONYX), room.getGame().getTokens(TokenType.ONYX));
    }

    @Test
    public void validTokensRequest3Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 10);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(3))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        
        String expectedJsonString = newBaseAnnouncement()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(3))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

            
            JsonElement actualJson = JsonParser.parseString(reply);
        
        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);

        int baseGameTokens = 4;
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.RUBY) + 10, room.getGame().getTokens(TokenType.RUBY));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.SAPPHIRE), room.getGame().getTokens(TokenType.SAPPHIRE));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.EMERALD), room.getGame().getTokens(TokenType.EMERALD));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.DIAMOND), room.getGame().getTokens(TokenType.DIAMOND));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.ONYX), room.getGame().getTokens(TokenType.ONYX));
    }

    @Test
    public void validTokensRequest4Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        
        String expectedJsonString = newBaseAnnouncement()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(2))
                .replace("$sapphireTaken", String.valueOf(0))
                .replace("$emeraldTaken", String.valueOf(0))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

            
            JsonElement actualJson = JsonParser.parseString(reply);
        
        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);

        int baseGameTokens = 4;
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.RUBY), room.getGame().getTokens(TokenType.RUBY));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.SAPPHIRE), room.getGame().getTokens(TokenType.SAPPHIRE));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.EMERALD), room.getGame().getTokens(TokenType.EMERALD));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.DIAMOND), room.getGame().getTokens(TokenType.DIAMOND));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.ONYX), room.getGame().getTokens(TokenType.ONYX));
    }

    @Test
    public void validTokensRequest5Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 9);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(1))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        
        String expectedJsonString = newBaseAnnouncement()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(2))
                .replace("$sapphireTaken", String.valueOf(0))
                .replace("$emeraldTaken", String.valueOf(0))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(1))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

            
            JsonElement actualJson = JsonParser.parseString(reply);
        
        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);

        int baseGameTokens = 4;
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.RUBY) + 9, room.getGame().getTokens(TokenType.RUBY));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.SAPPHIRE), room.getGame().getTokens(TokenType.SAPPHIRE));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.EMERALD), room.getGame().getTokens(TokenType.EMERALD));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.DIAMOND), room.getGame().getTokens(TokenType.DIAMOND));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.ONYX), room.getGame().getTokens(TokenType.ONYX));
    }

    @Test
    public void validTokensRequest6Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 10);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(2))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        String reply = messenger.getMessages().get(0).getMessage();
        
        String expectedJsonString = newBaseAnnouncement()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(2))
                .replace("$sapphireTaken", String.valueOf(0))
                .replace("$emeraldTaken", String.valueOf(0))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(2))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

            
            JsonElement actualJson = JsonParser.parseString(reply);
        
        assertEquals(JsonParser.parseString(expectedJsonString), actualJson);

        int baseGameTokens = 4;
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.RUBY) + 10, room.getGame().getTokens(TokenType.RUBY));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.SAPPHIRE), room.getGame().getTokens(TokenType.SAPPHIRE));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.EMERALD), room.getGame().getTokens(TokenType.EMERALD));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.DIAMOND), room.getGame().getTokens(TokenType.DIAMOND));
        assertEquals(baseGameTokens - owner.getTokenCount(TokenType.ONYX), room.getGame().getTokens(TokenType.ONYX));
    }

    @Test
    public void invalidUserUuidTest() {

        String userUuid = "invalid-uuid";

        String message = this.newBaseMessage()
                .replace("$userUuid", userUuid)
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));;                

        UserMessage receivedMessage = new UserMessage(message);
        assertThrows(JsonSyntaxException.class, () -> receivedMessage.parseDataToClass(GetTokens.DataDTO.class));
    }

    @Test
    public void userNotFoundTest() {

        String userUuid = "80bdc250-5365-4caf-8dd9-a33e709a0111";

        String message = this.newBaseMessage()
                .replace("$userUuid", userUuid)
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));

        int clientConnectionHashCode = 100000;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(clientConnectionHashCode, receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(clientConnectionHashCode, messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "User with this UUID not found");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void userNotInRoomTest() {

        User user = new User(UUID.randomUUID(), "PLAYER", 100000);
        this.database.addUser(user);

        String message = this.newBaseMessage()
                .replace("$userUuid", user.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0));   

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(user.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(user.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "This user isn't in any room");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void gameNotStartedTest() {

        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        this.database.addUser(owner);
        this.database.addRoom(room);

        String message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0)); 

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You can't take tokens when game didn't start");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void invalidTurnTest() {

        String cardUuid = "80bdc250-5365-4caf-8dd9-a33e709a0112";

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = this.newBaseMessage()
                .replace("$userUuid", player.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0)); 

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(player.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(player.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "It's not your turn");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void userHasPerformedActionTest() {

        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0)); 

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(2, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());
        assertTrue(owner.hasPerformedAction());

        message = this.newBaseMessage()
                .replace("$userUuid", owner.getUuid().toString())
                .replace("$rubyTaken", String.valueOf(1))
                .replace("$sapphireTaken", String.valueOf(1))
                .replace("$emeraldTaken", String.valueOf(1))
                .replace("$diamondTaken", String.valueOf(0))
                .replace("$onyxTaken", String.valueOf(0))
                .replace("$rubyReturned", String.valueOf(0))
                .replace("$sapphireReturned", String.valueOf(0))
                .replace("$emeraldReturned", String.valueOf(0))
                .replace("$diamondReturned", String.valueOf(0))
                .replace("$onyxReturned", String.valueOf(0)); 

        receivedMessage = new UserMessage(message);
        messenger = new Messenger();
        getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You've already made an action this round");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void notAllowedReturning1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 0);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 1);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(1));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You are trying to return tokens when you already have less than 10");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void notAllowedReturning2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 0);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 1);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(1));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You are trying to return tokens when you already have less than 10");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void insufficientReturnedTokens1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 8);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(1));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You don't have enough tokens to return");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void insufficientReturnedTokens2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 9);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(1));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You don't have enough tokens to return");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void tooManyReturnedTokens1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 8);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(2))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You are trying to return too many tokens");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void tooManyReturnedTokens2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 9);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(2))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You are trying to return too many tokens");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }
    
    @Test
    public void tooManyFinalTokens1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 8);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You have too many tokens");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void tooManyFinalTokens2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = User.class.getDeclaredField("tokens");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(owner);

            tokens.put(TokenType.RUBY, 9);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You have too many tokens");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void tooManyTokensTakenTest() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(3))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "You've taken too many RUBY tokens");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void insufficientTokensOnTable1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = Game.class.getDeclaredField("tokensOnTable");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(room.getGame());

            tokens.put(TokenType.RUBY, 0);
            tokens.put(TokenType.SAPPHIRE, 4);
            tokens.put(TokenType.EMERALD, 4);
            tokens.put(TokenType.DIAMOND, 4);
            tokens.put(TokenType.ONYX, 4);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "There are not enough RUBY tokens on the table");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void insufficientTokensOnTable2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        try{
            Game game = room.getGame();

            Field privateTokens = Game.class.getDeclaredField("tokensOnTable");
            privateTokens.setAccessible(true);
            tokens = (HashMap<TokenType, Integer>) privateTokens.get(room.getGame());

            tokens.put(TokenType.RUBY, 0);
            tokens.put(TokenType.SAPPHIRE, 0);
            tokens.put(TokenType.EMERALD, 0);
            tokens.put(TokenType.DIAMOND, 0);
            tokens.put(TokenType.ONYX, 0);
        }catch (Exception e){
                e.printStackTrace();
        }

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "There are not enough RUBY tokens on the table");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void wrongTokensTaken1Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(0))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "Your token choice is wrong");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void wrongTokensTaken2Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(0))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "Your token choice is wrong");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void wrongTokensTaken3Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(0))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "Your token choice is wrong");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void wrongTokensTaken4Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(1))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(1))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "Your token choice is wrong");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void wrongTokensTaken5Test() {
        User owner = new User(UUID.randomUUID(), "OWNER", 100000);
        User player = new User(UUID.randomUUID(), "PLAYER", 100001);
        Room room = new Room(UUID.randomUUID(), "ROOM", "PASSWORD", owner, this.database);
        room.getAllUsers().add(player);
        this.database.addUser(owner);
        this.database.addUser(player);
        this.database.addRoom(room);

        room.startGame();

        String message = newBaseMessage()
            .replace("$userUuid", owner.getUuid().toString())
            .replace("$rubyTaken", String.valueOf(2))
            .replace("$sapphireTaken", String.valueOf(1))
            .replace("$emeraldTaken", String.valueOf(1))
            .replace("$diamondTaken", String.valueOf(0))
            .replace("$onyxTaken", String.valueOf(0))
            .replace("$rubyReturned", String.valueOf(0))
            .replace("$sapphireReturned", String.valueOf(0))
            .replace("$emeraldReturned", String.valueOf(0))
            .replace("$diamondReturned", String.valueOf(0))
            .replace("$onyxReturned", String.valueOf(0));

        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        GetTokens getTokens = new GetTokens(owner.getConnectionHashCode(), receivedMessage, messenger, this.database);
        receivedMessage.parseDataToClass(GetTokens.DataDTO.class);
        getTokens.react();

        assertEquals(1, messenger.getMessages().size());
        assertEquals(owner.getConnectionHashCode(), messenger.getMessages().get(0).getReceiverHashcode());

        String expectedJsonString = this.newBaseErrorMessage()
                .replace("$error", "Your token choice is wrong");

        String reply = messenger.getMessages().get(0).getMessage();
        
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(reply);

        assertTrue(actualJson.getAsJsonObject().get("data").getAsJsonObject().has("error"));
        assertEquals(expectedJson, actualJson);
    }
}
