package com.github.splendor_mobile_game.websocket.handlers.reactions;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.database.InMemoryDatabase;
import com.github.splendor_mobile_game.websocket.communication.InvalidReceivedMessage;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;

public class CreateRoomTest {
    
    // TODO: Names of the test should be more specific
    @Test
    public void validRequest1() throws InvalidReceivedMessage {
        
        // Given
        String message = """
        {
            "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
            "type": "CreateRoom",
            "data": {
                "userDTO": {
                    "id": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
                    "name": "James"
                },
                "roomDTO": {
                    "name": "TajnyPokoj",
                    "password": "kjashjkasd"
                }
            }
        }
        """;

        int clientConnectionHashCode = 714239;
        ReceivedMessage receivedMessage = new ReceivedMessage(message);
        Messenger messenger = new Messenger();
        Database database = new InMemoryDatabase();
        CreateRoom createRoom = new CreateRoom(clientConnectionHashCode);

        // Parse the data given in the message
        receivedMessage.parseDataToClass(CreateRoom.DataDTO.class);

        // When
        createRoom.react(receivedMessage, messenger, database);

        // Then
        
        // One sent message
        assertThat(messenger.getMessages().size()).isEqualTo(1);
        
        // Receiver of this message is the client that sent request to the server
        assertThat(messenger.getMessages().get(0).getReceiverHashcode()).isEqualTo(clientConnectionHashCode);
        
        // Other assertions checking that reply is valid
        String reply = messenger.getMessages().get(0).getMessage();
        // ...
        // ...

    }
}
