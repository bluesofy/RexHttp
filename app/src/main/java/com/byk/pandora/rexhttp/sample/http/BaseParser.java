package com.byk.pandora.rexhttp.sample.http;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.byk.pandora.rexhttp.model.ApiResult;
import com.byk.pandora.rexhttp.rx.function.ResponseParserFunction;

/**
 * Created by Byk on 2017/12/25.
 *
 * @author Byk
 */
public class BaseParser<T> extends ResponseParserFunction<T> {

    private static final String RET = "ret";

    public BaseParser(Class<T> type, boolean isListType) {
        super(type, isListType);
    }

    @Override
    protected void parseCode(JSONObject jsonObject, ApiResult<T> apiResult) throws JSONException {
        if (jsonObject.containsKey(RET)) {
            apiResult.setCode(jsonObject.getIntValue(RET));
        } else {
            apiResult.setCode(-1);
        }
    }
}
