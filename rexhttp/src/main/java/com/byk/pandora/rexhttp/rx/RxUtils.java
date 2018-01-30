package com.byk.pandora.rexhttp.rx;

import android.util.Log;

import com.byk.pandora.rexhttp.model.ApiResult;
import com.byk.pandora.rexhttp.model.DataResult;
import com.byk.pandora.rexhttp.rx.function.ResultToDataFunction;
import com.byk.pandora.rexhttp.rx.function.ThrowableToDataFunction;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public class RxUtils {

    private static final String TAG = RxUtils.class.getSimpleName();

    public static <T> ObservableTransformer<T, T> dispatch() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                               .unsubscribeOn(Schedulers.io())
                               .doOnSubscribe(new Consumer<Disposable>() {
                                   @Override
                                   public void accept(@NonNull Disposable disposable) throws Exception {
                                       Log.i(TAG, "doOnSubscribe" + disposable.isDisposed());
                                   }
                               })
                               .doFinally(new Action() {
                                   @Override
                                   public void run() throws Exception {
                                       Log.i(TAG, "doFinally");
                                   }
                               })
                               .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<ApiResult<DataResult<T>>, DataResult<T>> dispatch(
            final boolean requestInSync, final boolean responseInSync) {
        return new ObservableTransformer<ApiResult<DataResult<T>>, DataResult<T>>() {
            @Override
            public ObservableSource<DataResult<T>> apply(@NonNull Observable<ApiResult<DataResult<T>>> upstream) {
                Observable<ApiResult<DataResult<T>>> stream = upstream;

                // Thread Change
                if (!requestInSync) {
                    stream = upstream.subscribeOn(Schedulers.io())
                                     .unsubscribeOn(Schedulers.io());
                }
                if (!responseInSync) {
                    stream = stream.observeOn(AndroidSchedulers.mainThread());
                }

                return stream.map(new ResultToDataFunction<T>())
                             .doOnSubscribe(new Consumer<Disposable>() {
                                 @Override
                                 public void accept(@NonNull Disposable disposable) throws Exception {
                                     Log.i(TAG, "doOnSubscribe" + disposable.isDisposed());
                                 }
                             })
                             .doFinally(new Action() {
                                 @Override
                                 public void run() throws Exception {
                                     Log.i(TAG, "doFinally");
                                 }
                             })
                             .onErrorResumeNext(new ThrowableToDataFunction<DataResult<T>>());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> transError() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.onErrorResumeNext(new ThrowableToDataFunction<T>());
            }
        };
    }

}
