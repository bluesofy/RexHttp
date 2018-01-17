package com.byk.pandora.rexhttp.interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.byk.pandora.rexhttp.RexUtils;
import com.byk.pandora.rexhttp.model.ApiParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;

/**
 * Created by Byk on 2017/12/26.
 *
 * @author Byk
 */
public class RequestInterceptor implements Interceptor {

    private String mTag = RequestInterceptor.class.getSimpleName();

    protected ApiParams iParams;

    private boolean mEnableLog;
    private boolean mEnableRequestLog = true;
    private boolean mEnableResponseLog = true;

    public RequestInterceptor setParams(ApiParams params) {
        iParams = params;
        return this;
    }

    public RequestInterceptor tag(String tag) {
        mTag = tag;
        return this;
    }

    public RequestInterceptor enableLog(boolean enable) {
        mEnableLog = enable;
        return this;
    }

    public RequestInterceptor enableRequestLog(boolean enable) {
        mEnableRequestLog = enable;
        return this;
    }

    public RequestInterceptor enableResponseLog(boolean enable) {
        mEnableResponseLog = enable;
        return this;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        // Request
        Request.Builder builder = request.newBuilder();
        String url = request.url()
                            .toString();
        builder.method(request.method(), parseRequestBody(request, builder));

        print("Http Action Start->" + url);
        long startTime = System.nanoTime();

        // Response : No Customer Parser
        Request newRequest = builder.build();
        Response response = chain.proceed(newRequest);
        if (!mEnableLog && !parseResponse()) {
            return response;
        }

        // Response : Customer Parser
        Response cloneResp = response.newBuilder()
                                     .build();
        ResponseBody cloneRespBody = cloneResp.body();
        byte[] newRespBytes = null;
        if (HttpHeaders.hasBody(cloneResp) && cloneRespBody != null) {
            newRespBytes = parseResponseBytes(cloneResp, cloneRespBody.bytes());
            cloneRespBody = ResponseBody.create(cloneRespBody.contentType(), newRespBytes);
        }

        // Log
        if (mEnableLog) {
            long costTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            logResponse(cloneResp, costTime, cloneRespBody.contentType(), newRespBytes,
                        logRequest(newRequest, chain.connection(), url));
        }

        print("Http Action Complete-<");

        return response.newBuilder()
                       .body(cloneRespBody)
                       .build();
    }

    protected boolean parseResponse() {
        return false;
    }

    protected RequestBody parseRequestBody(Request request, Request.Builder builder) throws IOException {
        return request.body();
    }

    protected byte[] parseResponseBytes(Response response, byte[] body) {
        return body;
    }
    
    protected void log(String msg) {
        Log.d(mTag, msg);
    }

    private StringBuilder logRequest(Request request, Connection connection, String url) {
        StringBuilder sBuilder = new StringBuilder(
                "<HttpLog>\n<Http Request------------------------------------------------>");

        if (mEnableRequestLog) {
            sBuilder.append("\nProtocol->")
                    .append((connection == null) ? Protocol.HTTP_1_1 : connection.protocol());

            sBuilder.append("\nUrl->")
                    .append(url);

            sBuilder.append("\nMethod->")
                    .append(request.method());

            sBuilder.append("\nHeaders->");
            Headers headers = request.headers();
            if (headers != null) {
                for (int i = 0, count = headers.size(); i < count; i++) {
                    sBuilder.append(headers.name(i))
                            .append(":")
                            .append(headers.value(i))
                            .append(";");
                }
            }

            sBuilder.append("\nRequest Body->");
            if (iParams != null) {
                sBuilder.append(JSON.toJSONString(iParams.urlParamMap));
            }
        }

        return sBuilder.append("\n<Http Request------------------------------------------------<\n");
    }

    private void logResponse(Response response, long costTime, MediaType mediaType, byte[] body, StringBuilder sBuilder)
            throws IOException {
        sBuilder.append("<Http Response----------------------------------------------->");

        if (mEnableResponseLog) {
            sBuilder.append("\nStatus Code->")
                    .append(response.code());

            sBuilder.append("\nStatus Msg->")
                    .append(response.message());

            sBuilder.append("\nCost Time->")
                    .append(costTime)
                    .append("ms");

            sBuilder.append("\nHeaders->");
            Headers headers = response.headers();
            if (headers != null) {
                for (int i = 0, count = headers.size(); i < count; i++) {
                    sBuilder.append(headers.name(i))
                            .append(":")
                            .append(headers.value(i))
                            .append(";");
                }
            }

            sBuilder.append("\nResponse Body->");
            if (body != null) {
                if (RexUtils.isPlainText(mediaType)) {
                    sBuilder.append(RexUtils.transBytes(body, mediaType));
                } else {
                    sBuilder.append("[FilePart]");
                }
            }
        }

        sBuilder.append("\n<Http Response-----------------------------------------------<\n<HttpLog>");
        print(sBuilder.toString());
    }

    private void print(String msg) {
        if (mEnableLog) {
            log(msg);
        }
    }

}
