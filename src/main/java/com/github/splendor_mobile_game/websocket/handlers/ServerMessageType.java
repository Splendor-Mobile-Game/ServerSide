package com.github.splendor_mobile_game.websocket.handlers;

/** Enum representing the different types of messages that can be send by the server. */
public enum ServerMessageType {

    /** Indicates that an error occurred while processing a request. */
    ERROR,
    
    UNKNOWN,
    CREATE_ROOM_RESPONSE,
    JOIN_ROOM_RESPONSE,
    START_GAME_RESPONSE,
    DEBUG_GET_RANDOM_CARD_RESPONSE,
    LEAVE_ROOM_RESPONSE,
    GET_TOKENS_RESPONSE,
    NEW_ROOM_OWNER

}
