package com.byk.pandora.rexhttp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byk on 2018/1/29.
 *
 * @author Byk
 */
public class DataResult<T> {

    private Class<T> mType;
    private boolean mIsListType;

    private T mData;
    private List<T> mDatas = new ArrayList<>();

    public DataResult(Class<T> type, boolean isListType) {
        mType = type;
        mIsListType = isListType;
    }

    public Class<T> getType() {
        return mType;
    }

    public boolean isListType() {
        return mIsListType;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
    }
}
