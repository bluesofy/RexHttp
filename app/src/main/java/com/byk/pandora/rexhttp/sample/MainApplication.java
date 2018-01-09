package com.byk.pandora.rexhttp.sample;

import android.app.Application;

import com.byk.pandora.rexhttp.RexHttp;

/**
 * Created by Byk on 2018/1/9.
 *
 * @author Byk
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RexHttp.debug(true)
               .setBaseUrl("http://xxx");
    }
}
