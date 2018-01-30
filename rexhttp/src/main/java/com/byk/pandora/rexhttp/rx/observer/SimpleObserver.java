package com.byk.pandora.rexhttp.rx.observer;

import android.content.Context;

import com.byk.pandora.rexhttp.exception.ApiException;
import com.byk.pandora.rexhttp.model.DataResult;
import com.byk.pandora.rexhttp.watcher.ProgressDialogWatcher;
import com.byk.pandora.rexhttp.watcher.ResponseWatcher;

import io.reactivex.annotations.NonNull;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public class SimpleObserver<T> extends BaseObserver<DataResult<T>> {

    public ResponseWatcher<T> mWatcher;

    public SimpleObserver(Context context, ResponseWatcher<T> watcher) {
        super(context);
        mWatcher = watcher;
        if (watcher instanceof ProgressDialogWatcher) {
            ((ProgressDialogWatcher) watcher).subscription(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mWatcher != null) {
            mWatcher.onStart();
        }
    }

    @Override
    public void onError(ApiException e) {
        if (mWatcher != null) {
            mWatcher.onError(e);
        }
    }

    @Override
    public void onNext(@NonNull DataResult<T> t) {
        super.onNext(t);
        if (mWatcher != null) {
            mWatcher.onSuccess(t);
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (mWatcher != null) {
            mWatcher.onCompleted();
        }
    }
}
