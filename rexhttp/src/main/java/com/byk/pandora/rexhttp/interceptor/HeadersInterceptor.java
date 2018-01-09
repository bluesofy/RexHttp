package com.byk.pandora.rexhttp.interceptor;

import android.support.annotation.NonNull;

import com.byk.pandora.rexhttp.model.ApiHeaders;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Byk on 2017/12/11.
 *
 * @author Byk
 */
public class HeadersInterceptor implements Interceptor {

    private ApiHeaders headers;

    public HeadersInterceptor(ApiHeaders headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                                       .newBuilder();
        if (headers.getHeadersMap()
                   .isEmpty()) {
            return chain.proceed(builder.build());
        }

        try {
            for (Map.Entry<String, String> entry : headers.getHeadersMap()
                                                          .entrySet()) {
                builder.header(entry.getKey(), entry.getValue())
                       .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chain.proceed(builder.build());
    }
}
