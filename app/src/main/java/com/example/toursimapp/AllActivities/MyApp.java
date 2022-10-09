package com.example.toursimapp.AllActivities;

import android.app.Application;

import com.example.toursimapp.Adapters.ConnectivityReceiver;

public class MyApp extends Application {
    private static MyApp mInstance;

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public void setConnectivityListner(ConnectivityReceiver.ConnectivityReceiverListner listner) {
        ConnectivityReceiver.connectivityReceiverListner = listner;
    }
}
