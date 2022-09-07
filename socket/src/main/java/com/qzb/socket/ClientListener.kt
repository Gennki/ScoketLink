package com.qzb.socket

import android.util.Log
import org.java_websocket.handshake.ServerHandshake
import java.lang.ref.WeakReference

/**
 * 生活白科技-居家小确幸
 *
 * @ClassName: ServerListener
 * @Author: Leon.Qin
 * @Date: 2022/8/23 15:12
 * @Description:
 */
abstract class ClientListener {

    open fun onOpen(handshakedata: ServerHandshake?) {
        Log.d(TAG, "ClientListener onOpen")
    }

    open fun onMessage(message: String?) {
        Log.d(TAG, "ClientListener onMessage:$message")
    }

    open fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "ClientListener onClose:code=$code,reason=$reason")
    }

    open fun onError(ex: Exception?) {
        Log.d(TAG, "ClientListener onError:${ex?.message}")
    }
}