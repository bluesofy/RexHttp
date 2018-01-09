/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.byk.pandora.rexhttp.interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.byk.pandora.rexhttp.RexUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Byk on 2017/12/11.
 *
 * @author Byk
 */
public abstract class AbstractSignInterceptor implements Interceptor {

    private static final String TAG = AbstractSignInterceptor.class.getSimpleName();

    private HttpUrl httpUrl;

    private boolean isSign = false;
    private boolean timeStamp = false;
    private boolean accessToken = false;

    public AbstractSignInterceptor() {}

    public boolean isSign() {
        return isSign;
    }

    public AbstractSignInterceptor sign(boolean sign) {
        isSign = sign;
        return this;
    }

    public boolean isTimeStamp() {
        return timeStamp;
    }

    public AbstractSignInterceptor timeStamp(boolean timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public AbstractSignInterceptor accessToken(boolean accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public boolean isAccessToken() {
        return accessToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.method()
                   .equals(RexUtils.METHOD_GET)) {
            this.httpUrl = HttpUrl.parse(parseUrl(request.url()
                                                         .url()
                                                         .toString()));
            request = addGetParamsSign(request);
        } else if (request.method()
                          .equals(RexUtils.METHOD_POST)) {
            this.httpUrl = request.url();
            request = addPostParamsSign(request);
        }
        return chain.proceed(request);
    }

    public HttpUrl getHttpUrl() {
        return httpUrl;
    }

    private Request addGetParamsSign(Request request) throws UnsupportedEncodingException {
        HttpUrl httpUrl = request.url();
        HttpUrl.Builder newBuilder = httpUrl.newBuilder();

        Set<String> nameSet = httpUrl.queryParameterNames();
        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(nameSet);

        TreeMap<String, String> oldParams = new TreeMap<>();
        for (int i = 0, size = nameList.size(); i < size; i++) {
            String name = nameList.get(i);
            List<String> list = httpUrl.queryParameterValues(name);
            String value = list != null && list.size() > 0 ? list.get(0) : "";
            oldParams.put(name, value);
        }

        String nameKeys = Collections.singletonList(nameList)
                                     .toString();
        TreeMap<String, String> newParams = dynamic(oldParams);
        if (newParams != null) {
            for (Map.Entry<String, String> entry : newParams.entrySet()) {
                String urlValue = URLEncoder.encode(entry.getValue(), RexUtils.CHARSET_UTF8.name());
                if (!nameKeys.contains(entry.getKey())) {
                    newBuilder.addQueryParameter(entry.getKey(), urlValue);
                }
            }
        }

        httpUrl = newBuilder.build();
        request = request.newBuilder()
                         .url(httpUrl)
                         .build();
        return request;
    }

    private Request addPostParamsSign(Request request) throws UnsupportedEncodingException {
        if (request.body() instanceof FormBody) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            FormBody formBody = (FormBody) request.body();

            if (formBody != null) {
                TreeMap<String, String> oldParams = new TreeMap<>();
                for (int i = 0, size = formBody.size(); i < size; i++) {
                    oldParams.put(formBody.encodedName(i), formBody.encodedValue(i));
                }

                TreeMap<String, String> newParams = dynamic(oldParams);
                if (newParams != null) {
                    for (Map.Entry<String, String> entry : newParams.entrySet()) {
                        String value = URLDecoder.decode(entry.getValue(), RexUtils.CHARSET_UTF8.name());
                        bodyBuilder.addEncoded(entry.getKey(), value);
                    }
                    String url = RexUtils.createUrlFromParams(httpUrl.url()
                                                                     .toString(), newParams);
                    Log.i(TAG, url);
                }
            }

            formBody = bodyBuilder.build();
            request = request.newBuilder()
                             .post(formBody)
                             .build();
        } else if (request.body() instanceof MultipartBody) {
            MultipartBody multipartBody = (MultipartBody) request.body();
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            List<MultipartBody.Part> newParts = new ArrayList<>();
            if (multipartBody != null) {
                newParts.addAll(multipartBody.parts());
            }

            TreeMap<String, String> oldParams = new TreeMap<>();
            TreeMap<String, String> newParams = dynamic(oldParams);
            for (Map.Entry<String, String> stringStringEntry : newParams.entrySet()) {
                MultipartBody.Part part = MultipartBody.Part.createFormData(stringStringEntry.getKey(),
                                                                            stringStringEntry.getValue());
                newParts.add(part);
            }
            for (MultipartBody.Part part : newParts) {
                bodyBuilder.addPart(part);
            }
            multipartBody = bodyBuilder.build();
            request = request.newBuilder()
                             .post(multipartBody)
                             .build();
        }
        return request;
    }

    private String parseUrl(String url) {
        if (!"".equals(url) && url.contains(RexUtils.CHAR_QUERY)) {
            url = url.substring(0, url.indexOf('?'));
        }
        return url;
    }


    /**
     * Insert Custom Params
     *
     * @param dynamicMap Old Dynamic Params
     * @return New Dynamic Params
     */
    public abstract TreeMap<String, String> dynamic(TreeMap<String, String> dynamicMap);
}
