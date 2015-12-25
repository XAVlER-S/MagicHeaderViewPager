package com.culiu.mhvp.demo;

import android.app.Application;
import android.content.Context;

/**
 * @author Xavier-S
 * @date 2015.11.27 20:09
 */
public class APP extends Application {

    private static APP mApplication;

    private static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized(APP.this) {
            mApplicationContext = getApplicationContext();
            mApplication = this;
        }
    }

    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public static APP getInstance() {
        return mApplication;
    }
}
