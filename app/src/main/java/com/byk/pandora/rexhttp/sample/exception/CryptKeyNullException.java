package com.byk.pandora.rexhttp.sample.exception;

import java.io.IOException;

/**
 * Created by Byk on 2018/1/4.
 *
 * @author Byk
 */
public class CryptKeyNullException extends IOException {

    private int errCode;
    private String message;

    public CryptKeyNullException(int errCode, String msg) {
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
