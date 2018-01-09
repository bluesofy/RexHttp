package com.byk.pandora.rexhttp.rx.function;

import android.util.Log;

import com.byk.pandora.rexhttp.RexHttp;
import com.byk.pandora.rexhttp.exception.ApiErrorCode;
import com.byk.pandora.rexhttp.exception.ApiException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * Created by Byk on 2017/12/18.
 *
 * @author Byk
 */
public class ThrowableResolveFunction implements Function<Observable<? extends Throwable>, Observable<?>> {

    private static final String TAG = ThrowableResolveFunction.class.getSimpleName();

    private int mCount = RexHttp.DEF_RETRY_COUNT;
    private long mDelay = RexHttp.DEF_RETRY_DELAY;
    private long mIncreaseDelay = RexHttp.DEF_RETRY_INCREASE_DELAY;

    public ThrowableResolveFunction() {}

    public ThrowableResolveFunction(int count, long delay) {
        mCount = count;
        mDelay = delay;
    }

    public ThrowableResolveFunction(int count, long delay, long increaseDelay) {
        mCount = count;
        mDelay = delay;
        mIncreaseDelay = increaseDelay;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        return observable.zipWith(Observable.range(1, mCount + 1), Wrapper.createFunction())
                         .flatMap(new Function<Wrapper, ObservableSource<?>>() {
                             @Override
                             public ObservableSource<?> apply(@NonNull Wrapper wrapper) throws Exception {
                                 if (wrapper.index > 1) {
                                     Log.i(TAG, "Retry Count:" + (wrapper.index));
                                 }

                                 int errCode = 0;
                                 if (wrapper.throwable instanceof ApiException) {
                                     ApiException exception = (ApiException) wrapper.throwable;
                                     errCode = exception.getCode();
                                 }
                                 boolean apiErr = errCode == ApiErrorCode.NETWORK_ERROR ||
                                                  errCode == ApiErrorCode.TIMEOUT_ERROR;

                                 boolean notOverRetry = wrapper.index < mCount + 1;

                                 boolean serverError = wrapper.throwable instanceof ConnectException ||
                                                       wrapper.throwable instanceof SocketTimeoutException ||
                                                       wrapper.throwable instanceof TimeoutException;

                                 boolean needRetry = (serverError || apiErr) && notOverRetry;
                                 if (needRetry) {
                                     return Observable.timer(mDelay + (wrapper.index - 1) * mIncreaseDelay,
                                                             TimeUnit.MILLISECONDS);
                                 }
                                 return Observable.error(wrapper.throwable);
                             }
                         });
    }

    private static class Wrapper {

        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }

        public static BiFunction<Throwable, Integer, Wrapper> createFunction() {
            return new BiFunction<Throwable, Integer, Wrapper>() {
                @Override
                public Wrapper apply(Throwable throwable, Integer integer) throws Exception {
                    return new Wrapper(throwable, integer);
                }
            };
        }
    }
}
