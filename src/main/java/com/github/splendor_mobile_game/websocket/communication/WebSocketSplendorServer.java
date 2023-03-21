package com.github.splendor_mobile_game.websocket.communication;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Message;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.connection.ConnectionHandler;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.github.splendor_mobile_game.websocket.utils.reflection.CannotCreateInstanceException;
import com.github.splendor_mobile_game.websocket.utils.reflection.Reflection;

public class WebSocketSplendorServer extends WebSocketServer {

    private Map<String, Class<? extends Reaction>> reactions;

    private Map<Integer, Thread> connectionHandlers = new HashMap<>();

    private Map<Integer, WebSocket> connections = new HashMap<>();

    private Class<? extends ConnectionHandler> outerConnectionHandlerClass;

    private int pingIntervalMs;

    private int connectionCheckInterval;

    private Database database;

    public WebSocketSplendorServer(
            InetSocketAddress address,
            Map<String, Class<? extends Reaction>> reactions,
            Class<? extends ConnectionHandler> outerConnectionHandlerClass, int pingIntervalMs,
            int connectionCheckInterval,
            Database database)
            throws ConnectionHandlerWithoutDefaultConstructorException {
        super(address);
        this.reactions = reactions;
        this.pingIntervalMs = pingIntervalMs;
        this.connectionCheckInterval = connectionCheckInterval;
        this.database = database;

        if (!Reflection.hasOneParameterConstructor(outerConnectionHandlerClass, WebSocket.class)) {
            throw new ConnectionHandlerWithoutDefaultConstructorException(outerConnectionHandlerClass.getName()
                    + " doesn't have constructor with WebSocket as argument, but it's required!");
        }

        this.outerConnectionHandlerClass = outerConnectionHandlerClass;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Log.DEBUG("New connection" + webSocket.hashCode() + " from " + webSocket.getRemoteSocketAddress());

        // Make new instance of given ConnectionHandler in constructor
        // It have callbacks that our WebSocketConnectionHandler will be invoking
        ConnectionHandler outerConnectionHandlerInstance;
        try {
            Constructor<? extends ConnectionHandler> constructor = this.outerConnectionHandlerClass
                    .getDeclaredConstructor(WebSocket.class);
            outerConnectionHandlerInstance = constructor.newInstance(webSocket);
        } catch (Exception e) {
            // This exception won't ever happen, because we check for that in the constructor of this class
            Log.ERROR("How did that happen?");
            e.printStackTrace();
            return;
        }

        // Create new thread for it our ConnectionHandler and start it
        Thread t = new Thread(new WebSocketConnectionHandler(
                webSocket,
                this.pingIntervalMs,
                this.connectionCheckInterval, outerConnectionHandlerInstance));
        t.start();

        // Save reference to it, it'd be deleted on connection close
        connectionHandlers.put(webSocket.hashCode(), t);
        connections.put(webSocket.hashCode(), webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Log.DEBUG("Connection ended: " + webSocket.getRemoteSocketAddress() + " Cause: " + i + " " + s + " " + b);
        connectionHandlers.remove(webSocket.hashCode());
        connections.remove(webSocket.hashCode());
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        Log.TRACE("Message received from (" +
                webSocket.hashCode() + ":" + webSocket.getRemoteSocketAddress() + "): " +
                message);

        // Parse the message
        ReceivedMessage receivedMessage;
        try {
            receivedMessage = new ReceivedMessage(message);
        } catch (InvalidReceivedMessage e) {
            Log.ERROR(e.toString());
            webSocket.send(e.toJsonResponse());
            return;
        }

        // Get the type of the message
        String type = receivedMessage.getType();

        // Find appropriate reaction to the message type received
        Class<? extends Reaction> reactionClass = reactions.get(type);

        if (reactionClass == null) {
            Log.TRACE("Unknown reaction type: " + type);
            ErrorResponse response = new ErrorResponse(Result.FAILURE, "This message type has not been found!");
            webSocket.send(response.ToJson());
            return;
        }

        // Parse the data given in the message
        Class<?> dataClass = Reflection.findClassWithAnnotationWithinClass(reactionClass, DataClass.class);
        try {
            receivedMessage.parseDataToClass(dataClass);
        } catch (InvalidReceivedMessage e) {
            Log.ERROR(e.toString());
            webSocket.send(e.toJsonResponse());
            return;
        }

        Messenger messenger = new Messenger();

        // Create instance of this reactionClass
        Reaction reactionInstance;
        try {
            reactionInstance = (Reaction) Reflection.createInstanceOfClass(reactionClass, webSocket.hashCode(), messenger, this.database);
        } catch (CannotCreateInstanceException e) {
            Log.ERROR(e.getMessage());
            e.printStackTrace();
            return;
        }

        // Use it to obtain appropriate reply
        reactionInstance.react(receivedMessage);

        // And send it to the users
        for (Message messageToSend : messenger.getMessages()) {
            WebSocket receiver = this.connections.get(messageToSend.getReceiverHashcode());
            String text = messageToSend.getMessage();
            receiver.send(text);
            Log.DEBUG("Message sent to (" +
                webSocket.hashCode() + ":" + webSocket.getRemoteSocketAddress() + "): " +
                text
            );
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Log.ERROR("Server error: " + e.getMessage());
    }

}
