package com.byk.pandora.rexhttp.rx.function;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by Byk on 2018/1/2.
 *
 * @author Byk
 */
public class CustomRetryFunction implements Function<Observable<? extends Throwable>, Observable<?>> {

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        return observable;
    }
}
