package com.byk.pandora.rexhttp.watcher;

import com.byk.pandora.rexhttp.model.ApiResult;

/**
 * Created by Byk on 2017/12/14.
 *
 * @author Byk
 */
public class ResponseWatcherWrapper<R extends ApiResult<T>, T> {

    ResponseWatcher<T> mWatcher;

    public ResponseWatcherWrapper(ResponseWatcher<T> callBack) {
        mWatcher = callBack;
    }

    public ResponseWatcher<T> getWatcher() {
        return mWatcher;
    }
}
