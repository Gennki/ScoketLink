package com.qzb.scoketlink.websocket.listener;

import android.net.nsd.NsdServiceInfo;

public abstract class ScanServiceListener {
    public void onFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    public void onSuccess(NsdServiceInfo serviceInfo) {
    }
}
