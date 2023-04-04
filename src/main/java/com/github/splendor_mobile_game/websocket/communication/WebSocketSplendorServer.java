package com.github.splendor_mobile_game.websocket.communication;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Message;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.UserRequestType;
import com.github.splendor_mobile_game.websocket.handlers.connection.ConnectionChecker;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.CustomException;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.github.splendor_mobile_game.websocket.utils.reflection.Reflection;


/** WebSocket server for the Splendor game. Handles incoming messages and sends responses to the clients. */
public class WebSocketSplendorServer extends WebSocketServer {

    // TODO: Make key to be enum
    /** Map of message types to their corresponding Reaction classes. */
    private Map<String, Class<? extends Reaction>> reactions;
    
    /** Map of WebSocket connection hashcodes to their corresponding ConnectionHandler threads. */
    private Map<Integer, Thread> connectionHandlers = new HashMap<>();
    
    /** Map of WebSocket connection hashcodes to their corresponding WebSocket instances. */
    private Map<Integer, WebSocket> connections = new HashMap<>();
    
    /** The ConnectionHandler class to use for new connections. */
    private Class<? extends ConnectionChecker> outerConnectionHandlerClass;
    
    /** Interval in milliseconds at which to send ping messages to clients. */
    private int pingIntervalMs;
    
    /** Interval in seconds at which to check if client connections are still alive. */
    private int connectionCheckInterval;
    
    /** The database instance to use for handling database interactions. */
    private Database database;
    
    /**
     * Constructs a new WebSocketSplendorServer instance.
     * 
     * @param address the address to listen on
     * @param reactions a map of message types to their corresponding Reaction classes
     * @param outerConnectionHandlerClass the ConnectionHandler class to use for new connections
     * @param pingIntervalMs the interval in milliseconds at which to send ping messages to clients
     * @param connectionCheckInterval the interval in seconds at which to check if client connections are still alive
     * @param database the database instance to use for handling database interactions
     * 
     * @throws ConnectionCheckerWithoutDefaultConstructorException if the specified ConnectionHandler class does not have a constructor with a WebSocket parameter
     */
    public WebSocketSplendorServer(
        InetSocketAddress address,
        Map<String, Class<? extends Reaction>> reactions,
        Class<? extends ConnectionChecker> outerConnectionHandlerClass,
        int pingIntervalMs,
        int connectionCheckInterval,
        Database database
    ) throws ConnectionCheckerWithoutDefaultConstructorException {
        
        super(address);

        this.reactions = reactions;
        this.pingIntervalMs = pingIntervalMs;
        this.connectionCheckInterval = connectionCheckInterval;
        this.database = database;
        
        // Check that the specified ConnectionHandler class has a constructor with a WebSocket parameter
        if (!Reflection.hasOneParameterConstructor(outerConnectionHandlerClass, WebSocket.class)) {
            throw new ConnectionCheckerWithoutDefaultConstructorException(
                outerConnectionHandlerClass.getName() + " doesn't have constructor with WebSocket as argument, but it's required!"
            );
        }
        
        this.outerConnectionHandlerClass = outerConnectionHandlerClass;
    }

    /** Called when the WebSocket server has started. */
    @Override
    public void onStart() {

    }

    /**
     * Called when a new WebSocket connection is opened.
     * 
     * @param webSocket the WebSocket instance for the new connection
     * @param clientHandshake the handshake data from the client
    */
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Log.DEBUG("New connection" + webSocket.hashCode() + " from " + webSocket.getRemoteSocketAddress());

        // Make new instance of given ConnectionHandler in constructor
        // It have callbacks that our WebSocketConnectionHandler will be invoking
        ConnectionChecker outerConnectionHandlerInstance;
        try {
            Constructor<? extends ConnectionChecker> constructor = this.outerConnectionHandlerClass
                    .getDeclaredConstructor(WebSocket.class);
            outerConnectionHandlerInstance = constructor.newInstance(webSocket);
        } catch (Exception e) {
            // This exception won't ever happen, because we check for that in the constructor of this class
            Log.ERROR("How did that happen?");
            e.printStackTrace();
            return;
        }

        // Create new thread for it our ConnectionHandler and start it
        Thread t = new Thread(new WebSocketConnectionChecker(
                webSocket,
                this.pingIntervalMs,
                this.connectionCheckInterval, outerConnectionHandlerInstance));
        t.start();

        // Save reference to it, it'd be deleted on connection close
        connectionHandlers.put(webSocket.hashCode(), t);
        connections.put(webSocket.hashCode(), webSocket);
    }

    /**
     * Called after the websocket connection has been closed.
     *
     * @param conn   The <tt>WebSocket</tt> instance this event is occurring on.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote host.
     **/
    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        // Log the connection end with the code, reason and whether it was closed remotely or locally
        Log.DEBUG("WebSocket connection closed with remote address " + webSocket.getRemoteSocketAddress() + 
            ". Close code: " + code + ". Reason: " + reason + ". Remote: " + remote + "."
        );
        
        // Remove the reference to the connection handler and WebSocket instance associated with the closed connection
        connectionHandlers.remove(webSocket.hashCode());
        connections.remove(webSocket.hashCode());
    }

    /**
     * Callback for string messages received from the remote host
     *
     * @param connection    The <tt>WebSocket</tt> instance this event is occurring on.
     * @param message       The UTF-8 decoded message that was received.
     **/
    @Override
    public void onMessage(WebSocket connection, String message) {
        Log.TRACE("Message received from (" +
            connection.hashCode() + ":" + connection.getRemoteSocketAddress() + "): " + message
        );

        // Parse the message
        UserMessage receivedMessage = new UserMessage(message);

        // Get the type of the message
        UserRequestType type = receivedMessage.getType();

        // Find appropriate reaction to the message type received
        // TODO: It could be more readable if we use some ReactionRepository with get method that would throws exception instead of null
        Class<? extends Reaction> reactionClass = reactions.get(type.toString());

        if (reactionClass == null) {
            Log.TRACE("Unknown reaction type: " + type);
            ErrorResponse response = new ErrorResponse(Result.FAILURE, "This message type has not been found!");
            connection.send(response.ToJson());
            return;
        }

        // Parse the data given in the message
        Class<?> dataClass = Reflection.findClassWithAnnotationWithinClass(reactionClass, DataClass.class);

        if (dataClass == null && receivedMessage.getData() != null) {
            Log.WARNING(connection.hashCode() + 
                " provided data to the message, but the message type reaction class doesn't require any data!"
            );
        } else {
            receivedMessage.parseDataToClass(dataClass);
        }

        // Create messenger class for storing messages
        Messenger messenger = new Messenger();

        // Create instance of this reactionClass
        Reaction reactionInstance = (Reaction) Reflection.createInstanceOfClass(
            reactionClass, connection.hashCode(), receivedMessage, messenger, this.database
        );

        // Use it to react appropriately
        reactionInstance.react();

        // And send it to the users
        for (Message messageToSend : messenger.getMessages()) {
            WebSocket receiver = this.connections.get(messageToSend.getReceiverHashcode());
            String text = messageToSend.getMessage();
            receiver.send(text);
            Log.DEBUG("Message sent to (" +
                connection.hashCode() + ":" + connection.getRemoteSocketAddress() + "): " + text
            );
        }

    }

    /**
     * This method is called when an error occurs in the WebSocket connection.
     * If the exception is of type CustomException, it sends a response with the error message in JSON format,
     * otherwise, it sends a generic error response with the exception message.
     * 
     * @param webSocket The WebSocket connection that encountered an error.
     * @param exception The exception that was thrown.
     */
    @Override
    public void onError(WebSocket webSocket, Exception exception) {
        
        // TODO: Message type and message id in the response should be included if possible
        
        // Check if the exception is of type CustomException
        if (exception.getClass().isAssignableFrom(CustomException.class)) {
            // If it is, cast it to CustomException and log the error message
            CustomException customException = (CustomException) exception;
            Log.ERROR(customException.toString());
            webSocket.send(customException.toJsonResponse());
        } else {
            // If it's not a CustomException, log the error message and send a generic ErrorResponse object to the client
            Log.ERROR("Server error: " + exception.getMessage());
            ErrorResponse response = new ErrorResponse(Result.ERROR, exception.getMessage());
            webSocket.send(response.ToJson());
        }
    }

}
