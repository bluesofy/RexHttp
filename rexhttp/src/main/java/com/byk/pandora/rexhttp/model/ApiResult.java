package com.byk.pandora.rexhttp.model;

import java.util.List;

/**
 * Created by Byk on 2017/12/8.
 *
 * @author Byk
 */
public class ApiResult<T> {

    public static final int RET_OK = 0;

    private int code;
    private String msg;
    private String content;
    private T data;
    private List<T> datas;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            content = "";
        }
        this.content = content;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public boolean isOk() {
        return code == RET_OK;
    }

    @Override
    public String toString() {
        return "ApiResult{" + "Code=" + code + ";Msg=" + msg + ";data=" + data + "}";
    }

}
