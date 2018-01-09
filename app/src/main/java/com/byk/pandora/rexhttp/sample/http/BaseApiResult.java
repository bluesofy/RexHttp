package com.byk.pandora.rexhttp.sample.http;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Byk on 2017/12/25.
 *
 * @author Byk
 */
public class BaseApiResult {

    @JSONField(name="ret")
    private int ret;

    @JSONField(name="msg")
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
