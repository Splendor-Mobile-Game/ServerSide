package com.github.splendor_mobile_game.websocket;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;

import com.github.splendor_mobile_game.handlers.connection.ConnectionHandler;

public class WebSocketConnectionHandler implements Runnable {
    private WebSocket connection;
    private Timer timer;
    private int checkIntervalMs;
    private ConnectionHandler outerConnectionReactor;

    public WebSocketConnectionHandler(WebSocket connection, int pingIntervalMs, int healthCheckIntervalMs,
            ConnectionHandler outerConnectionReactor) {
        this.connection = connection;
        this.outerConnectionReactor = outerConnectionReactor;
        this.timer = new Timer();
        this.checkIntervalMs = healthCheckIntervalMs;

        this.startPinging(pingIntervalMs);
    }

    @Override
    public void run() {
        while (!connection.isClosed()) {

            try {
                Thread.sleep(this.checkIntervalMs);
            } catch (InterruptedException e) {
                return;
            }

            long timeSinceLastPongMs = (System.nanoTime() - this.getLastPong(connection)) / 1000000;
            this.outerConnectionReactor.onConnectionCheck(timeSinceLastPongMs);
        }

        this.onClose();
    }

    private void onClose() {
        timer.cancel();
        this.outerConnectionReactor.onConnectionClose();
    }

    private void startPinging(long pingIntervalMs) {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (connection.isClosed()) {
                    this.cancel();
                    return;
                }

                connection.sendPing();
            }

        }, pingIntervalMs, pingIntervalMs);
    }

    private long getLastPong(WebSocket webSocket) {
        try {
            Field f = ((WebSocketImpl) webSocket).getClass().getDeclaredField("lastPong");
            f.setAccessible(true);
            return (long) f.get(webSocket);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
}
