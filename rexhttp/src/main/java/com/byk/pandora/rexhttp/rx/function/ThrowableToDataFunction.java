package com.byk.pandora.rexhttp.rx.function;

import com.byk.pandora.rexhttp.exception.ApiException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public class ThrowableToDataFunction<T> implements Function<Throwable, Observable<T>> {

    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ApiException.handleException(throwable));
    }
}
