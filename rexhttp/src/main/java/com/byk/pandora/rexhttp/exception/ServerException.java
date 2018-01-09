package com.byk.pandora.rexhttp.exception;

/**
 * Created by Byk on 2017/12/12.
 *
 * @author Byk
 */
public class ServerException extends Exception {

    private int errCode;
    private String message;

    public ServerException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
        this.message = msg;
    }

    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
