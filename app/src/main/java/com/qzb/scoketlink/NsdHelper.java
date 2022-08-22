package com.qzb.scoketlink;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.provider.Settings;
import android.util.Log;

import com.qzb.scoketlink.websocket.listener.ScanServiceListener;

public class NsdHelper {

    public static final String TAG = "NsdHelper";
    private NsdManager.RegistrationListener registrationListener;
    private static Context applicationContext;
    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;

    private static final class HelperHolder {
        static final NsdHelper helper = new NsdHelper();
    }

    public static NsdHelper get(Context context) {
        NsdHelper.applicationContext = context.getApplicationContext();
        return HelperHolder.helper;
    }


    public NsdManager init(String name, int port) {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
//                serviceName = nsdServiceInfo.getServiceName();
                Log.d(TAG, nsdServiceInfo.getServiceName() + " has onServiceRegistered");
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed. Put debugging code here to determine why.
            }
        };

        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName(name);
        serviceInfo.setServiceType("_tineco._tcp.");
        serviceInfo.setPort(port);
        nsdManager = (NsdManager) applicationContext.getSystemService(Context.NSD_SERVICE);
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
        return nsdManager;
    }


    public void startDiscover(NsdManager.DiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
        nsdManager.discoverServices("_tineco._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stopDiscover() {
        if (discoveryListener != null) {
            nsdManager.stopServiceDiscovery(discoveryListener);
            discoveryListener = null;
        }
    }

    public void release() {
        nsdManager.unregisterService(registrationListener);
        stopDiscover();
    }

    public void scanServer(ScanServiceListener listener) {
        startDiscover(new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "onStartDiscoveryFailed:" + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "onStopDiscoveryFailed:" + errorCode);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "onDiscoveryStarted");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "onDiscoveryStopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "onServiceFound:" + serviceInfo.getServiceName());
                if ("_tineco._tcp.".equals(serviceInfo.getServiceType()) && !getSelfServiceName().equals(serviceInfo.getServiceName())) {
                    resolveService(serviceInfo, listener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "onServiceLost:" + serviceInfo.getServiceName());
            }
        });
    }


    private void resolveService(NsdServiceInfo service, ScanServiceListener listener) {
        nsdManager.resolveService(service, new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                if (listener != null) {
                    listener.onFailed(serviceInfo, errorCode);
                }
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                if (listener != null) {
                    listener.onSuccess(serviceInfo);
                }
            }
        });
    }

    public static String getSelfServiceName() {
        return Settings.Secure.getString(applicationContext.getContentResolver(), "bluetooth_name");
    }


}
