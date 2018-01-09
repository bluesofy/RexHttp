package com.byk.pandora.rexhttp.exception;

/**
 * Created by Byk on 2017/12/14.
 *
 * @author Byk
 */
public class ApiErrorCode {

    /**
     * 下载错误
     */
    public static final int DOWNLOAD_ERROR = 100;

    /**
     * 默认错误
     */
    public static final int DEFAULT_ERROR = -1;

    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = UNKNOWN + 1;
    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = PARSE_ERROR + 1;
    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = NETWORK_ERROR + 1;

    /**
     * 证书出错
     */
    public static final int SSL_ERROR = HTTP_ERROR + 1;

    /**
     * 连接超时
     */
    public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

    /**
     * 调用错误
     */
    public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
    /**
     * 类转换错误
     */
    public static final int CAST_ERROR = INVOKE_ERROR + 1;
    /**
     * 请求取消
     */
    public static final int REQUEST_CANCEL = CAST_ERROR + 1;
    /**
     * 未知主机错误
     */
    public static final int UNKNOWN_HOST_ERROR = REQUEST_CANCEL + 1;

    /**
     * 空指针错误
     */
    public static final int NULL_POINTER_EXCEPTION = UNKNOWN_HOST_ERROR + 1;

    /**
     * 流关闭错误
     */
    public static final int STEAM_CLOSED = NULL_POINTER_EXCEPTION + 1;

    /**
     * 线程错误
     */
    public static final int THREAD_ERROR = STEAM_CLOSED + 1;

    /**
     * 请求参数为空
     */
    public static final int NULL_REQUEST_ERROR = THREAD_ERROR + 1;
}
