package com.github.splendor_mobile_game.websocket.handlers.reactions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.InvalidReceivedMessage;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CreateRoomTest {
    
    @Test
    public void validRequest1() throws InvalidReceivedMessage {
        
        // 1. Setup data for the test
        String messageContextId = "80bdc250-5365-4caf-8dd9-a33e709a0116";
        String messageType = "CREATE_ROOM";
        String userUuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
        String userName = "James";
        String roomName = "TajnyPokoj";
        String roomPassword = "kjashjkasd";

        String message = """
        {
            "contextId": "$messageContextId",
            "type": "$type",
            "data": {
                "userDTO": {
                    "uuid": "$userId",
                    "name": "$userName"
                },
                "roomDTO": {
                    "name": "$roomName",
                    "password": "$roomPassword"
                }
            }
        }""".replace("$messageContextId", messageContextId)
            .replace("$type", messageType)
            .replace("$userId", userUuid)
            .replace("$userName", userName)
            .replace("$roomName", roomName)
            .replace("$roomPassword", roomPassword);
        
        Log.DEBUG(message);

        int clientConnectionHashCode = 714239;
        UserMessage receivedMessage = new UserMessage(message);
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();
        CreateRoom createRoom = new CreateRoom(clientConnectionHashCode, receivedMessage, messenger, database);
        
        // Parse the data given in the message
        receivedMessage.parseDataToClass(CreateRoom.DataDTO.class);
        int receivedMessageHashCode = receivedMessage.hashCode();

        
        // 2. Call the function you are testing
        createRoom.react();


        // 3. Check that return value and side effects of this call is correct
        
        // One sent message
        assertThat(messenger.getMessages().size()).isEqualTo(1);
        
        // Receiver of this message is the client that sent request to the server
        assertThat(messenger.getMessages().get(0).getReceiverHashcode()).isEqualTo(clientConnectionHashCode);

        
        // One user has been added to the database
        assertThat(database.getAllUsers().size()).isEqualTo(1);
        
        User user = database.getAllUsers().get(0);
        
        // User's connection hashcode is the same as the client send the request
        assertThat(user.getConnectionHashCode()).isEqualTo(clientConnectionHashCode);
        
        // Check for name
        assertThat(user.getName()).isEqualTo("James");
        
        // Check for UUID
        assertThat(user.getUuid().toString()).isEqualTo("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
        
        // Check that receivedMessage has not been modified
        assertThat(receivedMessage.hashCode()).isEqualTo(receivedMessageHashCode);

        // Check that reply is json, has all necessary fields, and they are valid etc.

        // First let's create json we expect
        String expectedJsonString = """
        {
            "contextId":"$messageContextId",
            "type":"$type_RESPONSE",
            "result":"$result",
            "data":{
                "user":{
                    "id":"$userId",
                    "name":"$userName"
                },
                "room":{
                    "name":"$roomName"
                }
            }
        }""".replace("$messageContextId", messageContextId)
            .replace("$type", messageType)
            .replace("$result", Result.OK.toString())
            .replace("$userId", userUuid)
            .replace("$userName", userName)
            .replace("$roomName", roomName);

        // Get response from the server
        String serverReply = messenger.getMessages().get(0).getMessage();
        
        // Parse those strings to json
        JsonElement expectedJson = JsonParser.parseString(expectedJsonString);
        JsonElement actualJson = JsonParser.parseString(serverReply);

        // I check if actual json has room uuid field
        assertThat(actualJson.getAsJsonObject().get("data").getAsJsonObject().get("room").getAsJsonObject().has("uuid")).isTrue();
        
        // I check if this is valid uuid
        assertThat(UUID.fromString(actualJson.getAsJsonObject().get("data").getAsJsonObject().get("room").getAsJsonObject().get("uuid").getAsString())).isNotNull();
        
        // I remove this field, so I can compare all other fields in one go
        actualJson.getAsJsonObject().get("data").getAsJsonObject().get("room").getAsJsonObject().remove("uuid");
        actualJson.getAsJsonObject().get("data").getAsJsonObject().get("room").getAsJsonObject().remove("enterCode");
        
        // Check if they are equal
        assertThat(actualJson).isEqualTo(expectedJson);
        // Here I checked if they are sematically identical
        // If we cannot know upfront the value of fields, 
        // then we have a little more work to do and check each field one by one
        // or do something like with room uuid
    }
}
