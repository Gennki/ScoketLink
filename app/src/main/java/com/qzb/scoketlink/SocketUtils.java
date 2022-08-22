package com.qzb.scoketlink;

import androidx.annotation.Nullable;

import com.qzb.scoketlink.websocket.listener.ClientListener;
import com.qzb.scoketlink.websocket.listener.SocketServerListener;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SocketUtils {
    private static ArrayList<WebSocketClient> clientList = new ArrayList<>();
    private static WebSocketServer webSocketServer;
    private static ArrayList<WebSocket> webSocketList = new ArrayList<>();

    public static void startServer(int port, @Nullable SocketServerListener listener) {
        webSocketServer = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                webSocketList.add(conn);
                if (listener != null) {
                    listener.onOpen(conn, handshake);
                }
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                webSocketList.remove(conn);
                if (listener != null) {
                    listener.onClose(conn, code, reason, remote);
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                if (listener != null) {
                    listener.onMessage(conn, message);
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
                if (listener != null) {
                    listener.onError(conn, ex);
                }
            }

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }
        };
        webSocketServer.start();
    }

    public static void stopServer() {
        try {
            if (webSocketServer != null) {
                webSocketServer.stop();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        webSocketServer = null;
    }

    public static WebSocketClient connect(String ip, @Nullable ClientListener listener) {
        URI uri = URI.create("ws://" + ip + ":" + 10086);
        WebSocketClient client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                if (listener != null) {
                    listener.onOpen(handshakedata);
                }
            }

            @Override
            public void onMessage(String message) {
                if (listener != null) {
                    listener.onMessage(message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (listener != null) {
                    listener.onClose(code, reason, remote);
                }
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                if (listener != null) {
                    listener.onError(ex);
                }
            }
        };
        if (!client.isOpen()) {
            client.connect();
        }
        if (!clientList.contains(client)) {
            clientList.add(client);
        }
        return client;
    }


    public static void disconnect(WebSocketClient client) {
        if (client.isOpen()) {
            client.close();
        }
        clientList.remove(client);
    }


    public static WebSocketClient getWebSocketClient(String ipAddress) {
        for (WebSocketClient client : clientList) {
            InetAddress inetAddress = client.getSocket().getInetAddress();
            if (inetAddress != null && ipAddress.equals(inetAddress.getHostAddress())) {
                return client;
            }
        }
        return null;
    }
}
