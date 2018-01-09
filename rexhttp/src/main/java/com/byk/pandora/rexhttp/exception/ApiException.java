package com.byk.pandora.rexhttp.exception;

import android.net.ParseException;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.byk.pandora.rexhttp.model.ApiResult;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * Created by Byk on 2017/12/12.
 *
 * @author Byk
 */
public class ApiException extends Exception {

    private final int code;
    private String message;

    private String mDisplayMessage;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getDisplayMessage() {
        return mDisplayMessage;
    }

    public void setDisplayMessage(String msg) {
        this.mDisplayMessage = msg + "(code:" + code + ")";
    }

    public static boolean isOk(ApiResult apiResult) {
        return apiResult != null && apiResult.isOk();
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(httpException, httpException.code());
            ex.message = httpException.getMessage();
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.getErrCode());
            ex.message = resultException.getMessage();
            return ex;
        } else if (e instanceof org.json.JSONException || e instanceof JSONException ||
                   e instanceof NotSerializableException || e instanceof ParseException) {
            ex = new ApiException(e, ApiErrorCode.PARSE_ERROR);
            ex.message = getErrorMsg(ex, "解析错误");
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(e, ApiErrorCode.CAST_ERROR);
            ex.message = "类型转换错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ApiErrorCode.NETWORK_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ApiErrorCode.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(e, ApiErrorCode.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(e, ApiErrorCode.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, ApiErrorCode.UNKNOWN_HOST_ERROR);
            ex.message = "无法解析该域名";
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new ApiException(e, ApiErrorCode.NULL_POINTER_EXCEPTION);
            ex.message = "空指针异常";
            return ex;
        } else if (e instanceof IllegalStateException) {
            ex = new ApiException(e, ApiErrorCode.STEAM_CLOSED);
            ex.message = "流错误";
            return ex;
        } else if (e instanceof NetworkOnMainThreadException) {
            ex = new ApiException(e, ApiErrorCode.THREAD_ERROR);
            ex.message = "线程错误";
            return ex;
        } else if (e instanceof IllegalArgumentException) {
            ex = new ApiException(e, ApiErrorCode.NULL_REQUEST_ERROR);
            ex.message = "参数错误";
            return ex;
        } else {
            ex = new ApiException(e, ApiErrorCode.UNKNOWN);
            ex.message = getErrorMsg(e, "未知错误");
            return ex;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    private static String getErrorMsg(Throwable e, String customMsg) {
        String msg = e.getMessage();
        return TextUtils.isEmpty(msg) ? customMsg : msg;
    }
}
