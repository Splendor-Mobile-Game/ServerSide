package com.github.splendor_mobile_game.websocket.handlers.reactions;

import java.util.UUID;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.ReactionName;

@ReactionName("LEAVE_ROOM")
public class LeaveRoom extends Reaction{

    public LeaveRoom(int connectionHashCode, ReceivedMessage receivedMessage, Messenger messenger, Database database) {
        super(connectionHashCode, receivedMessage, messenger, database);

    }
    private class UserDTO{
        public UUID uuid;
        public String name;
    }
    @DataClass
    private class DataDTO{
        private UserDTO userDTO;
    }
    @Override
    public void react() {

        DataDTO dataDTO = (DataDTO) receivedMessage.getData();
        
    }


}