package com.github.splendor_mobile_game.websocket.communication;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;

import com.github.splendor_mobile_game.websocket.handlers.connection.ConnectionChecker;

/**
 * This class monitors WebSocket connections by sending ping messages at regular intervals 
 * and checking the health status of the connection. It executes a function provided 
 * in the constructor at fixed intervals. It can be used to react if the client 
 * has not responded within a certain amount of time.
 */
public class WebSocketConnectionChecker implements Runnable {

    private WebSocket connection;

    /** A Timer object used to send ping messages at regular intervals. */
    private Timer timer;

    /** The interval (in milliseconds) for checking the health status of the connection. */
    private int checkIntervalMs;

    /** A ConnectionChecker object that handles connection events. */
    private ConnectionChecker connectionChecker;

    /**
     * Constructor for WebSocketConnectionChecker objects.
     * 
     * @param connection The WebSocket connection to handle
     * @param pingIntervalMs The interval (in milliseconds) for sending ping messages
     * @param healthCheckIntervalMs The  interval (in milliseconds) at which the health status 
     * of the WebSocket connection is checked using the function provided in the constructor of the ConnectionChecker.
     * @param connectionChecker A ConnectionChecker object that checks connection in fixed interval
     */
    public WebSocketConnectionChecker(WebSocket connection, int pingIntervalMs, int healthCheckIntervalMs, ConnectionChecker connectionChecker) {
        this.connection = connection;
        this.connectionChecker = connectionChecker;
        this.timer = new Timer();
        this.checkIntervalMs = healthCheckIntervalMs;

        // Start sending ping messages
        this.startPinging(pingIntervalMs);
    }

    /**
     * The run method runs in a separate thread and checks the health status of the connection
     * at regular intervals.
     */
    @Override
    public void run() {
        // Loop until the connection is closed
        while (!connection.isClosed()) {

            try {
                // Sleep for the specified interval
                Thread.sleep(this.checkIntervalMs);
            } catch (InterruptedException e) {
                return;
            }

            // Calculate the time since the last pong message was received
            long timeSinceLastPongMs = (System.nanoTime() - this.getLastPong(connection)) / 1000000;

            // Call the outerConnectionReactor's onConnectionCheck method to handle the connection check
            this.connectionChecker.onConnectionCheck(timeSinceLastPongMs);
        }

        // Call the onClose method to handle connection closure
        this.onClose();
    }

    /**
     * The onClose method cancels the timer and calls the connectionChecker's onConnectionClose method
     * to propagate the connection closure event.
     */
    private void onClose() {
        timer.cancel();
        this.connectionChecker.onConnectionClose();
    }

    /**
     * The startPinging method schedules a TimerTask to send ping messages at regular intervals.
     * 
     * @param pingIntervalMs The interval (in milliseconds) for sending ping messages
     */
    private void startPinging(long pingIntervalMs) {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // If the connection is closed, cancel the TimerTask and return
                if (connection.isClosed()) {
                    this.cancel();
                    return;
                }

                // Send a ping message
                connection.sendPing();
            }

        }, pingIntervalMs, pingIntervalMs);
    }

    /**
     * The getLastPong method uses reflection to access the lastPong field of the WebSocketImpl class
     * and returns the timestamp of the last pong message received.
     * 
     * @param webSocket The WebSocket connection
     * @return The timestamp (in nanoseconds) of the last pong message received
     */
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
