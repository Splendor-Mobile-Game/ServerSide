package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JoinRoom extends Reaction {

    public JoinRoom(int connectionHashCode, Messenger messenger, Database database) {
        super(connectionHashCode, messenger, database);
    }

    @DataClass
    private class Data {

        private User user;

    }

    @Override
    public void react(ReceivedMessage parsedMessage) {

        Data data = (Data) parsedMessage.getData();

        JsonObject player = new JsonObject();
        player.addProperty("id", connectionHashCode);
        player.addProperty("name", "Jacuś");

        JsonObject room = new JsonObject();
        room.addProperty("name", "Tajny pokój");
        room.addProperty("password", "alshdjklasd2asd");
        room.add("player", player);

        JsonObject response = new JsonObject();
        response.addProperty("messageContextId", parsedMessage.getMessageContextId());
        response.addProperty("type", "JoinRoomResponse");
        response.addProperty("result", "OK");
        response.add("data", room);

        messenger.addMessageToSend(this.connectionHashCode, (new Gson()).toJson(response));
    }

}

// { "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116", "type": "JoinRoom", "data": { "player": { "name": "James" } } }