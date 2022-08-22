package com.qzb.scoketlink.websocket.listener;

import org.java_websocket.handshake.ServerHandshake;

public abstract class ClientListener {
    public void onOpen(ServerHandshake handshakedata) {

    }

    public void onMessage(String message) {

    }

    public void onClose(int code, String reason, boolean remote) {

    }

    public void onError(Exception ex) {

    }
}
