package com.byk.pandora.rexhttp.exception;

/**
 * Created by Byk on 2017/12/20.
 *
 * @author Byk
 */
public class ServerErrorCode {

    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int METHOD_NOT_ALLOWED = 405;
    private static final int NOT_ACCEPTABLE = 406;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int RESOURCES_GONE = 410;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
}
