package com.byk.pandora.rexhttp.request;

import android.content.Context;

import com.byk.pandora.rexhttp.model.ApiResult;
import com.byk.pandora.rexhttp.rx.RxUtils;
import com.byk.pandora.rexhttp.rx.function.CustomRetryFunction;
import com.byk.pandora.rexhttp.rx.function.ResponseParserFunction;
import com.byk.pandora.rexhttp.rx.function.ThrowableResolveFunction;
import com.byk.pandora.rexhttp.rx.observer.SimpleObserver;
import com.byk.pandora.rexhttp.watcher.ResponseWatcher;
import com.byk.pandora.rexhttp.watcher.ResponseWatcherWrapper;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/20.
 *
 * @author Byk
 */
public abstract class BaseStartRequest extends BaseRequest {

    private CustomRetryFunction mCustomRetryFunction = new CustomRetryFunction();

    public BaseStartRequest(Context context, String url) {
        super(context, url);
    }

    public BaseStartRequest setReChecker(CustomRetryFunction function) {
        mCustomRetryFunction = function;
        return this;
    }

    @Override
    public <T> Observable<T> start(ResponseParserFunction<T> parser) {
        init();
        return getObservable(doRequest(), parser);
    }

    @Override
    public <T> Disposable start(ResponseWatcher<T> watcher, ResponseParserFunction<T> parser) {
        return start(new ResponseWatcherWrapper<>(watcher), parser);
    }

    @Override
    public <T> Disposable start(ResponseWatcherWrapper<? extends ApiResult<T>, T> watcherWrapper,
                                ResponseParserFunction<T> parser) {
        init();
        return getObservable(doRequest(), parser).subscribeWith(
                new SimpleObserver<>(iContext, watcherWrapper.getWatcher()));
    }

    private <T> Observable<T> getObservable(Observable<ResponseBody> observable, ResponseParserFunction<T> parser) {
        return observable.map(parser)
                         .compose(RxUtils.<T>dispatch(isSyncRequest, isSyncResponse))
                         .retryWhen(new ThrowableResolveFunction(retryCount, retryDelay, retryIncreaseDelay))
                         .retryWhen(mCustomRetryFunction);
    }
}
