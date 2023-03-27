package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;

import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.utils.RandomString;
import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.UserMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CreateServer extends Reaction {

    // This field is available due to abstract class
    // protected int connectionHashCode;

    public CreateServer(int connectionHashCode, UserMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);
    }

    public class Player {
        public String name;
    }

    @DataClass
    public class Data {
        public Player player;
    }

    @Override
    public void react() {
        // Received request from the client should be in the following format
        // {
        //     "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
        //     "type": "CreateServer",
        //     "data": {
        //         "player": {
        //             "name": "James"
        //         }
        //     }
        // }

        // Our response will have this exemplary format
        // {
        //     "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116"   // The same id, cause its reply to that message, the same context
        //     "type": "CreateServerResponse",
        //     "result": "OK",
        //     "data": {
        //         "roomCode": "HD9T54D",
        //         "sessionId": "cc3aeccd-6c81-4660-ba67-18d56941657b",
        //         "player": {
        //             "id": "beb58f8d-2522-4690-8119-9775c081316e",
        //             "name": "James"
        //         }
        //     }
        // }

        // Or in failure case
        // {
        //     "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116"   // The same id, cause its reply to that message, the same context
        //     "type": "CreateServerResponse",
        //     "result": "ERROR",
        //     "data": {
        //         "error": "Missing field playerName"
        //     }
        // }

        Data dataDTO = (Data) receivedMessage.getData();

        JsonObject player = new JsonObject();
        player.addProperty("id", UUID.randomUUID().toString());
        player.addProperty("name", dataDTO.player.name);

        JsonObject data = new JsonObject();
        data.addProperty("roomCode", RandomString.generateRandomString(5));
        data.addProperty("sessionId", UUID.randomUUID().toString());
        data.add("player", player);

        JsonObject response = new JsonObject();
        response.addProperty("messageContextId", receivedMessage.getMessageContextId());
        response.addProperty("type", "CreateServerResponse");
        response.addProperty("result", "OK");
        response.add("data", data);

        messenger.addMessageToSend(this.connectionHashCode, (new Gson()).toJson(response));
    }

}
