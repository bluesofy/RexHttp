package com.byk.pandora.rexhttp.watcher;

import com.byk.pandora.rexhttp.exception.ApiException;
import com.byk.pandora.rexhttp.model.DataResult;

/**
 * Created by Byk on 2017/12/12.
 *
 * @author Byk
 */
public class ResponseWatcher<T> {

    public void onStart() {}

    public void onCompleted() {}

    public void onError(ApiException e) {}

    public void onSuccess(DataResult<T> result) {}
}
