package com.byk.pandora.rexhttp.request.body;

import android.support.annotation.Nullable;

import com.byk.pandora.rexhttp.model.ApiParams;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Byk on 2017/12/22.
 *
 * @author Byk
 */
public class RequestBodyWrapper {

    private RequestBody mRequestBody;
    private ApiParams mParams;

    private RequestBodyWrapper() {}

    public static RequestBodyWrapper build() {
        return new RequestBodyWrapper();
    }

    public RequestBodyWrapper setRequestBody(RequestBody requestBody) {
        mRequestBody = requestBody;
        return this;
    }

    public RequestBody getRequestBody() {
        return mRequestBody;
    }

    public RequestBodyWrapper setParams(ApiParams params) {
        mParams = params;
        return this;
    }

    public ApiParams getParams() {
        return mParams;
    }

    public RequestBodyWrapper create(@Nullable MediaType contentType, byte[] content, int offset, int byteCount) {
        mRequestBody = RequestBody.create(contentType, content, offset, byteCount);
        return this;
    }
}
