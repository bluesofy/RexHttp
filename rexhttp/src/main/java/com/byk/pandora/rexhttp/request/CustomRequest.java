package com.byk.pandora.rexhttp.request;

import android.content.Context;

import com.byk.pandora.rexhttp.model.ApiResult;
import com.byk.pandora.rexhttp.model.DataResult;
import com.byk.pandora.rexhttp.rx.RxUtils;
import com.byk.pandora.rexhttp.rx.function.ResponseParserFunction;
import com.byk.pandora.rexhttp.rx.function.ResultToDataFunction;
import com.byk.pandora.rexhttp.rx.function.ThrowableResolveFunction;
import com.byk.pandora.rexhttp.rx.observer.SimpleObserver;
import com.byk.pandora.rexhttp.watcher.ResponseWatcher;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/21.
 *
 * @author Byk
 */
public class CustomRequest extends BaseRequest {

    public CustomRequest(Context context) {
        super(context, "");
    }

    public <T> Observable<T> call(Observable<T> observable) {
        checkValidate();
        return observable.compose(RxUtils.<T>dispatch())
                         .compose(RxUtils.<T>transError())
                         .retryWhen(new ThrowableResolveFunction(retryCount, retryDelay, retryIncreaseDelay));
    }

    public <T> void call(Observable<DataResult<T>> observable, ResponseWatcher<T> watcher) {
        call(observable, new SimpleObserver<>(iContext, watcher));
    }

    public <T> void call(Observable<T> observable, Observer<T> subscriber) {
        observable.compose(RxUtils.<T>dispatch())
                  .subscribe(subscriber);
    }

    public <T> Observable<DataResult<T>> apiCall(Observable<ApiResult<DataResult<T>>> observable) {
        checkValidate();
        return observable.map(new ResultToDataFunction<T>())
                         .compose(RxUtils.<DataResult<T>>dispatch())
                         .compose(RxUtils.<DataResult<T>>transError())
                         .retryWhen(new ThrowableResolveFunction(retryCount, retryDelay, retryIncreaseDelay));
    }

    public <T> Disposable apiCall(Observable<ResponseBody> observable, ResponseWatcher<T> watcher,
                                  ResponseParserFunction<T> parser) {
        return call(observable, watcher, parser);
    }

    public <T> Disposable call(Observable<ResponseBody> observable, ResponseWatcher<T> watcher,
                               ResponseParserFunction<T> parser) {
        init();
        return getObservable(observable, parser).subscribeWith(new SimpleObserver<>(iContext, watcher));
    }

    private <T> Observable<DataResult<T>> getObservable(Observable<ResponseBody> observable,
                                                        ResponseParserFunction<T> parser) {
        return observable.map(parser)
                         .compose(RxUtils.<T>dispatch(isSyncRequest, isSyncResponse))
                         .retryWhen(new ThrowableResolveFunction(retryCount, retryDelay, retryIncreaseDelay));
    }

    @Override
    protected Observable<ResponseBody> doRequest() {
        return null;
    }
}
