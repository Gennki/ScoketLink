package com.qzb.scoketlink.websocket.listener;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public abstract class SocketServerListener {
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    public void onMessage(WebSocket conn, String message) {
    }

    public void onError(WebSocket conn, Exception ex) {

    }

    public void onStart() {
    }

}
