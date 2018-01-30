package com.byk.pandora.rexhttp.rx.function;

import com.byk.pandora.rexhttp.exception.ApiException;
import com.byk.pandora.rexhttp.exception.ServerException;
import com.byk.pandora.rexhttp.model.ApiResult;
import com.byk.pandora.rexhttp.model.DataResult;

import io.reactivex.functions.Function;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public class ResultToDataFunction<T> implements Function<ApiResult<DataResult<T>>, DataResult<T>> {

    @Override
    public DataResult<T> apply(ApiResult<DataResult<T>> tApiResult) throws Exception {
        if (ApiException.isOk(tApiResult)) {
            return tApiResult.getData();
        } else {
            throw new ServerException(tApiResult.getCode(), tApiResult.getMsg());
        }
    }
}
