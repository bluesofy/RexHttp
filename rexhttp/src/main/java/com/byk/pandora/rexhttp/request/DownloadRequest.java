package com.byk.pandora.rexhttp.request;

import android.content.Context;

import com.byk.pandora.rexhttp.rx.RxUtils;
import com.byk.pandora.rexhttp.rx.function.ThrowableResolveFunction;
import com.byk.pandora.rexhttp.rx.observer.DownloadObserver;
import com.byk.pandora.rexhttp.watcher.ResponseWatcher;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/20.
 *
 * @author Byk
 */
public class DownloadRequest extends BaseRequest {

    private String mSavePath;
    private String mSaveName;

    public DownloadRequest(Context context, String url) {
        super(context, url);
    }

    /**
     * Default: /storage/emulated/0/Android/data/$PkgName/files/
     */
    public DownloadRequest savePath(String savePath) {
        mSavePath = savePath;
        return this;
    }

    /**
     * Default: Named By Timestamp
     */
    public DownloadRequest saveName(String saveName) {
        mSaveName = saveName;
        return this;
    }

    public <T> Disposable start(ResponseWatcher<T> watcher) {
        return init().doRequest()
                     .compose(new ObservableTransformer<ResponseBody, ResponseBody>() {
                         @Override
                         public ObservableSource<ResponseBody> apply(@NonNull Observable<ResponseBody> upstream) {
                             if (isSyncRequest) {
                                 return upstream;
                             } else {
                                 return upstream.subscribeOn(Schedulers.io())
                                                .unsubscribeOn(Schedulers.io())
                                                .observeOn(Schedulers.io());
                             }
                         }
                     })
                     .compose(RxUtils.<ResponseBody>transError())
                     .retryWhen(new ThrowableResolveFunction(retryCount, retryDelay, retryIncreaseDelay))
                     .subscribeWith(new DownloadObserver<>(iContext, mSavePath, mSaveName, watcher));
    }

    @Override
    protected Observable<ResponseBody> doRequest() {
        return apiManager.downloadFile(url);
    }
}
