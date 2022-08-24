package com.qzb.scoketlink.bean;

import android.net.nsd.NsdServiceInfo;

import java.io.Serializable;

public class DeviceItem implements Serializable {
    public DeviceItem(NsdServiceInfo nsdServiceInfo, int port, boolean isConnected) {
        this.nsdServiceInfo = nsdServiceInfo;
        this.port = port;
        this.isConnected = isConnected;
    }

    private NsdServiceInfo nsdServiceInfo = null;
    private boolean isConnected = false;
    private int port;

    public NsdServiceInfo getNsdServiceInfo() {
        return nsdServiceInfo;
    }

    public void setNsdServiceInfo(NsdServiceInfo nsdServiceInfo) {
        this.nsdServiceInfo = nsdServiceInfo;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
