package com.qzb.socket

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.ref.WeakReference

/**
 * 生活白科技-居家小确幸
 *
 * @ClassName: ServerListener
 * @Author: Leon.Qin
 * @Date: 2022/8/23 15:12
 * @Description:
 */
abstract class ServerListener {

    open fun onOpen(webSocketServer: WebSocketServer, conn: WebSocket?, handshake: ClientHandshake?) {
        Log.d(TAG, "ServerListener onOpen")
    }

    open fun onClose(webSocketServer: WebSocketServer, conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "ServerListener onClose")
    }

    open fun onMessage(webSocketServer: WebSocketServer, conn: WebSocket?, message: String?) {
        Log.d(TAG, "ServerListener onMessage:$message")
    }

    open fun onError(webSocketServer: WebSocketServer, conn: WebSocket?, ex: Exception?) {
        Log.d(TAG, "ServerListener onError:${ex?.message}")
    }

    open fun onStart(webSocketServer: WebSocketServer) {
        Log.d(TAG, "ServerListener onStart")
    }
}