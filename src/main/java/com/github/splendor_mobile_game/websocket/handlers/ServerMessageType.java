package com.github.splendor_mobile_game.websocket.handlers;

import com.github.splendor_mobile_game.websocket.handlers.reactions.BuyMine;

// TODO: Java doc required
public enum ServerMessageType {

    UNKNOWN,
    CREATE_ROOM_RESPONSE,
    JOIN_ROOM_RESPONSE,
    DEBUG_GET_RANDOM_CARD_RESPONSE,
    BUY_MINE_RESPONSE,
    LEAVE_ROOM_RESPONSE,
    NEW_ROOM_OWNER
    
}
