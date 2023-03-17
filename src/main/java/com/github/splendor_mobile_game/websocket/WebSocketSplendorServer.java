package com.github.splendor_mobile_game.websocket;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.splendor_mobile_game.handlers.DataClass;
import com.github.splendor_mobile_game.handlers.Reaction;
import com.github.splendor_mobile_game.handlers.connection.ConnectionHandler;
import com.github.splendor_mobile_game.utils.CustomException;
import com.github.splendor_mobile_game.utils.Log;
import com.github.splendor_mobile_game.utils.Reflection;
import com.github.splendor_mobile_game.utils.json.JsonParser;
import com.github.splendor_mobile_game.utils.json.exceptions.JsonParserException;
import com.google.gson.JsonObject;

public class WebSocketSplendorServer extends WebSocketServer {

    private Map<String, Class<? extends Reaction>> reactions;

    private Map<Integer, Thread> connectionHandlers = new HashMap<>();

    private Class<? extends ConnectionHandler> outerConnectionHandlerClass;

    private int pingIntervalMs;

    private int connectionCheckInterval;

    public WebSocketSplendorServer(
            InetSocketAddress address,
            Map<String, Class<? extends Reaction>> reactions,
            Class<? extends ConnectionHandler> outerConnectionHandlerClass, int pingIntervalMs,
            int connectionCheckInterval)
            throws ConnectionHandlerWithoutDefaultConstructorException {
        super(address);
        this.reactions = reactions;
        this.pingIntervalMs = pingIntervalMs;
        this.connectionCheckInterval = connectionCheckInterval;

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
        Log.DEBUG("New connection: " + webSocket.getRemoteSocketAddress());
        Log.DEBUG("Hashcode: " + webSocket.hashCode());

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

        Thread t = new Thread(new WebSocketConnectionHandler(webSocket, this.pingIntervalMs,
                this.connectionCheckInterval, outerConnectionHandlerInstance));
        t.start();

        connectionHandlers.put(webSocket.hashCode(), t);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Log.DEBUG("Connection ended: " + webSocket.getRemoteSocketAddress() + " Cause: " + i + " " + s + " " + b);
        // connectionHandlers.get(webSocket.hashCode()).interrupt();
        connectionHandlers.remove(webSocket.hashCode());
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        Log.DEBUG("[DEBUG] Message received from (" + webSocket.hashCode() + ":" + webSocket.getRemoteSocketAddress()
                + "): " + message);

        // TODO: Better message parsing. Combine two message classes
        // Parse the message
        ReceivedMessage receivedMessage;
        try {
            receivedMessage = ReceivedMessage.fromJson(message);
        } catch (CustomException e) {
            Log.ERROR(e.toString());
            webSocket.send(e.toJsonResponse());
            return;
        }

        // Get the type of the message
        String type = receivedMessage.getType();

        // Find appropriate reaction to the message type received
        Class<? extends Reaction> reactionClass = reactions.get(type);

        if (reactionClass == null) {
            // TODO: Send appropriate error message
            webSocket.send("This message type has not been found!");
            return;
        }

        Object object = null;
        for (Class<?> declaredClass : reactionClass.getDeclaredClasses()) {

            if (declaredClass.isAnnotationPresent(DataClass.class)) {

                JsonObject jsonObject = receivedMessage.getData();

                try {
                    object = JsonParser.parseJson(jsonObject.toString(), declaredClass);
                } catch (JsonParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

        ParsedMessage parsedMessage;
        try {
            parsedMessage = ParsedMessage.fromJson(message);
        } catch (InvalidReceivedMessage e) {
            return;
        }

        parsedMessage.setData(object);

        Reaction reaction;
        try {
            Constructor<? extends Reaction> constructor = reactionClass.getDeclaredConstructor(int.class);
            reaction = constructor.newInstance(webSocket.hashCode());
        } catch (Exception e) {
            Log.ERROR(reactionClass.getName() + " has no default constructor some way!");
            e.printStackTrace();
            return;
        }

        // Use it to obtain appropriate reply
        String reply = reaction.getReply(parsedMessage);

        // And send it to the user
        webSocket.send(reply);
        Log.DEBUG("[DEBUG] Message sent to (" + webSocket.hashCode() + ":" + webSocket.getRemoteSocketAddress()
                + "): " + reply);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Server error: " + e.getMessage());
    }

}
