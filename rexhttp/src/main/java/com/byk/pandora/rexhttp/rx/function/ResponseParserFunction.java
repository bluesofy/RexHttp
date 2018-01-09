package com.byk.pandora.rexhttp.rx.function;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.byk.pandora.rexhttp.exception.ApiErrorCode;
import com.byk.pandora.rexhttp.model.ApiResult;

import java.io.IOException;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public class ResponseParserFunction<T> implements Function<ResponseBody, ApiResult<T>> {

    private static final String CODE = "code";
    private static final String MSG = "msg";
    private static final String DATA = "data";
    private static final String LIST = "list";

    private Class<T> mType;
    private boolean mIsListType;

    public ResponseParserFunction(Class<T> type, boolean isListType) {
        mType = type;
        mIsListType = isListType;
    }

    public Class<T> getType() {
        return mType;
    }

    public boolean isListType() {
        return mIsListType;
    }

    @Override
    public ApiResult<T> apply(ResponseBody responseBody) throws Exception {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(ApiErrorCode.DEFAULT_ERROR);

        try {
            String json = parseBody(responseBody);
            if (!TextUtils.isEmpty(json)) {
                parseApiResult(json, apiResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }

        return apiResult;
    }

    protected String parseBody(ResponseBody body) throws Exception {
        return body.string();
    }

    protected String parseData(JSONObject jsonObject, ApiResult<T> apiResult) throws JSONException {
        String content = jsonObject.getString(DATA);
        if (content == null) {
            apiResult.setData(jsonObject.toJavaObject(mType));
        } else {
            apiResult.setData(JSON.parseObject(content, mType));
        }
        return content;
    }

    /** For List */
    protected String parseDatas(JSONObject jsonObject, ApiResult<T> apiResult) throws JSONException {
        String content = jsonObject.getString(LIST);
        apiResult.setDatas(JSON.parseArray(content, mType));
        return content;
    }

    protected void parseCode(JSONObject jsonObject, ApiResult<T> apiResult) throws JSONException {
        apiResult.setCode(jsonObject.getIntValue(CODE));
    }

    protected void parseMsg(JSONObject jsonObject, ApiResult<T> apiResult) throws JSONException {
        apiResult.setMsg(jsonObject.getString(MSG));
    }

    private void parseApiResult(String json, ApiResult<T> apiResult) throws JSONException {
        if (TextUtils.isEmpty(json)) {
            return;
        }

        JSONObject jsonObject = JSON.parseObject(json);
        parseCode(jsonObject, apiResult);
        parseMsg(jsonObject, apiResult);
        if (isListType()) {
            apiResult.setContent(parseDatas(jsonObject, apiResult));
        } else {
            apiResult.setContent(parseData(jsonObject, apiResult));
        }
    }
}
