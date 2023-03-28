package com.github.splendor_mobile_game.websocket.response;

/**
 * The Result enum represents the possible outcomes of an operation that the server sends back to the client.
 * 
 * <p>When the client sends a request to the server, the server will process the request and send back a message indicating
 * the outcome of the operation. This enum represents the possible outcomes that the server can send back to the client.</p>
 */
public enum Result {
    /** The operation was successful. */
    OK,

    /** The operation failed due to a mistake made by the client. */
    FAILURE,

    /** The operation failed due to a mistake made by the server. */
    ERROR
}
