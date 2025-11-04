package org.jared.trujillo.controller;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class PriceUpdateSocketHandler {

    private static final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnWebSocketConnect
    public void onConnect(Session userSession) throws IOException {
        System.out.println("Client connected: " + userSession.hashCode());
        sessions.add(userSession);
    }

    @OnWebSocketClose
    public void onClose(Session userSession, int statusCode, String reason) {
        System.out.println("Client disconnected: " + userSession.hashCode());
        sessions.remove(userSession);
    }

    @OnWebSocketError
    public void onError(Session userSession, Throwable error) {
        System.err.println("WebSocket error for " + userSession.hashCode() + ": " + error.getMessage());
        sessions.remove(userSession);
    }

    @OnWebSocketMessage
    public void onMessage(Session userSession, String message) {
        System.out.println("Received (and ignored) message from " + userSession.hashCode() + ": " + message);
    }

    public static void broadcast(String jsonMessage) {
        System.out.println("Broadcasting update: " + jsonMessage);

        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(jsonMessage);
                } catch (IOException e) {
                    System.err.println("Error sending to client " + session.hashCode() + ": " + e.getMessage());
                }
            }
        }
    }

}
