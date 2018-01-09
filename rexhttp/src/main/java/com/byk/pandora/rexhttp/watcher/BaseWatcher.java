package com.byk.pandora.rexhttp.watcher;

import com.byk.pandora.rexhttp.exception.ApiException;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public abstract class BaseWatcher<T> extends ResponseWatcher<T> {

    /**
     * OnError
     *
     * @param e exception
     */
    @Override
    public abstract void onError(ApiException e);

    /**
     * onSuccess
     *
     * @param data Response Data
     */
    @Override
    public abstract void onSuccess(T data);
}
