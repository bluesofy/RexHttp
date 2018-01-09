package com.byk.pandora.rexhttp.sample.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Byk on 2017/12/27.
 *
 * @author Byk
 */
public class ApiCenter {

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "token")
    private String token;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
