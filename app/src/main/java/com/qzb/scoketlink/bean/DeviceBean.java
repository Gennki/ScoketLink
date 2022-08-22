package com.qzb.scoketlink.bean;

import android.net.nsd.NsdServiceInfo;

import java.io.Serializable;

public class DeviceBean implements Serializable {
    public DeviceBean(NsdServiceInfo nsdServiceInfo, boolean isConnected) {
        this.nsdServiceInfo = nsdServiceInfo;
        this.isConnected = isConnected;
    }

    private NsdServiceInfo nsdServiceInfo = null;
    private boolean isConnected = false;

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
}
