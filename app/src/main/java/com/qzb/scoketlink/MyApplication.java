package com.qzb.scoketlink;

import android.app.Application;
import android.net.nsd.NsdManager;
import android.provider.Settings;

public class MyApplication extends Application {

    private NsdManager nsdManager;


    @Override
    public void onCreate() {
        super.onCreate();
        NsdHelper nsdHelper = NsdHelper.get(this);
        nsdManager = nsdHelper.init(NsdHelper.getSelfServiceName(), 10001);
    }

    public NsdManager getMsdManager() {
        return nsdManager;
    }

}
