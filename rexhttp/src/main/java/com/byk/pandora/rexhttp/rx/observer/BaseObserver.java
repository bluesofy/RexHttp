package com.byk.pandora.rexhttp.rx.observer;

import android.content.Context;
import android.util.Log;

import com.byk.pandora.rexhttp.RexUtils;
import com.byk.pandora.rexhttp.exception.ApiException;

import java.lang.ref.WeakReference;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public abstract class BaseObserver<T> extends DisposableObserver<T> {

    private static final String TAG = BaseObserver.class.getSimpleName();

    private WeakReference<Context> mContextWeakReference;

    public BaseObserver() {}

    public BaseObserver(Context context) {
        if (context != null) {
            mContextWeakReference = new WeakReference<>(context);
        }
    }

    @Override
    protected void onStart() {
        print("-->Http is onStart");
        if (mContextWeakReference != null && mContextWeakReference.get() != null &&
            !RexUtils.isNetworkAvailable(mContextWeakReference.get())) {
            onComplete();
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        print("-->Http is onNext");
    }

    @Override
    public final void onError(Throwable e) {
        print("-->Http is onError");
        if (e instanceof ApiException) {
            print("--> ApiException err:" + e);
            onError((ApiException) e);
        } else {
            print("--> Normal Exception err:" + e);
            onError(ApiException.handleException(e));
        }
    }

    @Override
    public void onComplete() {
        print("-->Http is onComplete");
    }

    private void print(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * Handle Api Exception
     *
     * @param e Api Exception
     */
    protected abstract void onError(ApiException e);
}
