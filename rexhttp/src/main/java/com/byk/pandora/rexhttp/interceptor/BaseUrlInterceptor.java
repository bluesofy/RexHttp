package com.byk.pandora.rexhttp.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Byk on 2017/12/27.
 *
 * @author Byk
 */
public class BaseUrlInterceptor implements Interceptor {

    private volatile String mBaseUrl;

    public void setUrl(String url) {
        mBaseUrl = url;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (TextUtils.isEmpty(mBaseUrl)) {
            return chain.proceed(request);
        } else {
            HttpUrl newUrl = request.url()
                                    .newBuilder()
                                    .host(mBaseUrl)
                                    .build();
            return chain.proceed(request.newBuilder()
                                        .url(newUrl)
                                        .build());
        }
    }
}
