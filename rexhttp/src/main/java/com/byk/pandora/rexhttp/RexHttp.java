package com.byk.pandora.rexhttp;

import android.content.Context;

import com.byk.pandora.rexhttp.model.ApiHeaders;
import com.byk.pandora.rexhttp.model.ApiParams;
import com.byk.pandora.rexhttp.request.CustomRequest;
import com.byk.pandora.rexhttp.request.DeleteRequest;
import com.byk.pandora.rexhttp.request.DownloadRequest;
import com.byk.pandora.rexhttp.request.GetRequest;
import com.byk.pandora.rexhttp.request.PostRequest;
import com.byk.pandora.rexhttp.request.PutRequest;
import com.byk.pandora.rexhttp.rule.CertifyManager;

import java.io.InputStream;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import io.reactivex.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Byk on 2017/12/7.
 *
 * @author Byk
 */
public class RexHttp {

    private static final String TAG = RexHttp.class.getSimpleName();

    private static final int DEF_TIMEOUT = 15_000;

    public static final int DEF_RETRY_COUNT = 3;
    public static final int DEF_RETRY_INCREASE_DELAY = 0;
    public static final int DEF_RETRY_DELAY = 1000;

    private static volatile RexHttp sInstance;

    private int mRetryCount = DEF_RETRY_COUNT;
    private int mRetryDelay = DEF_RETRY_DELAY;
    private int mRetryIncreaseDelay = DEF_RETRY_INCREASE_DELAY;

    private OkHttpClient.Builder mHttpClientBuilder;
    private Retrofit.Builder mRetrofitBuilder;

    private String mTag;
    private boolean mDebug;
    private String mGlobalBaseUrl;
    private ApiHeaders mDefHeader;
    private ApiParams mDefParam;

    private RexHttp() {
        mTag = TAG;

        mHttpClientBuilder = new OkHttpClient.Builder();
        mHttpClientBuilder.connectTimeout(DEF_TIMEOUT, TimeUnit.MILLISECONDS);
        mHttpClientBuilder.readTimeout(DEF_TIMEOUT, TimeUnit.MILLISECONDS);
        mHttpClientBuilder.writeTimeout(DEF_TIMEOUT, TimeUnit.MILLISECONDS);

        mRetrofitBuilder = new Retrofit.Builder();
    }

    public static synchronized RexHttp getInstance() {
        if (sInstance == null) {
            synchronized (RexHttp.class) {
                if (sInstance == null) {
                    sInstance = new RexHttp();
                }
            }
        }
        return sInstance;
    }

    public static RexHttp create() {
        return getInstance();
    }

    public static OkHttpClient getHttpClient() {
        return create().mHttpClientBuilder.build();
    }

    public static OkHttpClient.Builder getHttpClientBuilder() {
        return create().mHttpClientBuilder;
    }

    public static Retrofit getRetrofit() {
        return create().mRetrofitBuilder.baseUrl(getBaseUrl())
                                        .build();
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return create().mRetrofitBuilder;
    }

    public static RexHttp debug(boolean enable) {
        return create().enableDebug(enable);
    }

    public static boolean isDebug() {
        return create().mDebug;
    }

    public static String getGlobalTag() {
        return create().mTag;
    }

    /** Cancel Subscription Task */
    public static void cancelTask(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public static String getBaseUrl() {
        return create().mGlobalBaseUrl;
    }

    public RexHttp setBaseUrl(String baseUrl) {
        if (baseUrl != null) {
            mGlobalBaseUrl = baseUrl;
        }
        return this;
    }

    public RexHttp addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            mHttpClientBuilder.addInterceptor(interceptor);
        }
        return this;
    }

    public RexHttp setGlobalTag(String tag) {
        mTag = tag;
        return this;
    }

    public RexHttp enableDebug(boolean enable) {
        mDebug = enable;
        return this;
    }

    public RexHttp addNetworkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            mHttpClientBuilder.addNetworkInterceptor(interceptor);
        }
        return this;
    }

    public RexHttp setProxy(Proxy proxy) {
        if (proxy != null) {
            mHttpClientBuilder.proxy(proxy);
        }
        return this;
    }

    public RexHttp setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    public RexHttp setCertificates(InputStream... certificates) {
        CertifyManager.SslParams sslParams = CertifyManager.getSslSocketFactory(null, null, certificates);
        mHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    public RexHttp setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        CertifyManager.SslParams sslParams = CertifyManager.getSslSocketFactory(bksFile, password, certificates);
        mHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    public RexHttp setReadTimeOut(long readTimeOut) {
        mHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    public RexHttp setWriteTimeOut(long writeTimeout) {
        mHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public RexHttp setConnectTimeout(long connectTimeout) {
        mHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public RexHttp setRetryCount(int retryCount) {
        if (retryCount > -1) {
            mRetryCount = retryCount;
        }
        return this;
    }

    public static int getRetryCount() {
        return create().mRetryCount;
    }

    public RexHttp setRetryDelay(int retryDelay) {
        if (retryDelay > -1) {
            mRetryDelay = retryDelay;
        }
        return this;
    }

    public static int getRetryDelay() {
        return create().mRetryDelay;
    }

    public RexHttp setRetryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay > -1) {
            mRetryIncreaseDelay = retryIncreaseDelay;
        }
        return this;
    }

    public static int getRetryIncreaseDelay() {
        return create().mRetryIncreaseDelay;
    }

    public ApiParams getDefParam() {
        return mDefParam;
    }

    public RexHttp addDefParam(ApiParams param) {
        if (param == null) {
            mDefParam = new ApiParams();
        }
        mDefParam.put(param);
        return this;
    }

    public ApiHeaders getDefaultHeader() {
        return mDefHeader;
    }

    public RexHttp addDefaultHeader(ApiHeaders header) {
        if (header == null) {
            mDefHeader = new ApiHeaders();
        }
        mDefHeader.put(header);
        return this;
    }

    public static GetRequest get(Context context, String url) {
        return new GetRequest(context, url);
    }

    public static PostRequest post(Context context, String url) {
        return new PostRequest(context, url);
    }

    public static DeleteRequest delete(Context context, String url) {
        return new DeleteRequest(context, url);
    }

    public static CustomRequest custom(Context context) {
        return new CustomRequest(context);
    }

    public static DownloadRequest downLoad(Context context, String url) {
        return new DownloadRequest(context, url);
    }

    public static PutRequest put(Context context, String url) {
        return new PutRequest(context, url);
    }

}
