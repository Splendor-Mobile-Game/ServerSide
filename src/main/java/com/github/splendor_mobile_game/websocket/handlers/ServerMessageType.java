package com.github.splendor_mobile_game.websocket.handlers;

/**
 * Enum representing the different types of messages that can be send by the
 * server.
 */
public enum ServerMessageType {

    /** Indicates that an error occurred while processing a request. */
    ERROR,

    UNKNOWN,
    CREATE_ROOM_RESPONSE,
    JOIN_ROOM_RESPONSE,
    MAKE_RESERVATION_FROM_DECK_RESPONSE,
    MAKE_RESERVATION_FROM_DECK_ANNOUNCEMENT,
    MAKE_RESERVATION_FROM_TABLE_RESPONSE,
    MAKE_RESERVATION_FROM_TABLE_ANNOUNCEMENT,
    START_GAME_RESPONSE,
    DEBUG_GET_RANDOM_CARD_RESPONSE,
    BUY_REVEALED_MINE_RESPONSE,
    BUY_REVEALED_MINE_ANNOUNCEMENT,
    BUY_RESERVED_MINE_RESPONSE,
    BUY_RESERVED_MINE_ANNOUNCEMENT,
    LEAVE_ROOM_RESPONSE,
    NEW_ROOM_OWNER,
    KICK_ANNOUNCEMENT,
    KICK_RESPONSE,
    NEW_TURN_ANNOUNCEMENT,
    END_TURN_RESPONSE,
    GET_TOKENS_RESPONSE,
    END_GAME_ANNOUNCEMENT
}
