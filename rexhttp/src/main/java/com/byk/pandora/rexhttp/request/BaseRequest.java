package com.byk.pandora.rexhttp.request;

import android.content.Context;
import android.text.TextUtils;

import com.byk.pandora.rexhttp.RexHttp;
import com.byk.pandora.rexhttp.RexUtils;
import com.byk.pandora.rexhttp.api.ApiService;
import com.byk.pandora.rexhttp.interceptor.AbstractSignInterceptor;
import com.byk.pandora.rexhttp.interceptor.HeadersInterceptor;
import com.byk.pandora.rexhttp.interceptor.RequestInterceptor;
import com.byk.pandora.rexhttp.model.ApiHeaders;
import com.byk.pandora.rexhttp.model.ApiParams;
import com.byk.pandora.rexhttp.model.ApiResult;
import com.byk.pandora.rexhttp.model.DataResult;
import com.byk.pandora.rexhttp.rule.CertifyManager;
import com.byk.pandora.rexhttp.rx.function.ResponseParserFunction;
import com.byk.pandora.rexhttp.watcher.ResponseWatcher;
import com.byk.pandora.rexhttp.watcher.ResponseWatcherWrapper;

import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Byk on 2017/12/11.
 *
 * @author Byk
 */
public abstract class BaseRequest {

    protected String baseUrl;

    protected String url;

    protected long readTimeOut;
    protected long writeTimeOut;
    protected long connectTimeout;

    protected int retryCount;
    protected int retryDelay;
    protected int retryIncreaseDelay;

    /** Is Run in Synchronized. Default - False */
    protected boolean isSyncRequest;
    /** Is Response in Synchronized. Default - False */
    protected boolean isSyncResponse;

    protected List<Interceptor> networkInterceptors = new ArrayList<>();

    protected ApiHeaders headers = new ApiHeaders();
    protected ApiParams params = new ApiParams();

    protected Retrofit iRetrofit;
    protected ApiService apiManager;
    protected OkHttpClient okHttpClient;

    protected HttpUrl httpUrl;
    protected Proxy proxy;
    protected CertifyManager.SslParams mSslParams;
    protected HostnameVerifier hostnameVerifier;

    protected List<Interceptor> interceptors = new ArrayList<>();

    protected Context iContext;

    /** Need Sign */
    private boolean mHasSign = false;
    /** Need TimeStamp */
    private boolean mHasTimeStamp = false;
    /** Need Token */
    private boolean mHasAccessToken = false;

    private RequestInterceptor mRequestInterceptor;

    public BaseRequest(Context context, String url) {
        iContext = context;
        this.url = url;
        RexHttp config = RexHttp.getInstance();

        this.baseUrl = RexHttp.getBaseUrl();
        if (!TextUtils.isEmpty(this.baseUrl)) {
            httpUrl = HttpUrl.parse(baseUrl);
        }

        retryCount = RexHttp.getRetryCount();
        retryDelay = RexHttp.getRetryDelay();
        retryIncreaseDelay = RexHttp.getRetryIncreaseDelay();

        String acceptLanguage = ApiHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) {
            headers(ApiHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
        }

        String userAgent = ApiHeaders.getUserAgent(iContext);
        if (!TextUtils.isEmpty(userAgent)) {
            headers(ApiHeaders.HEAD_KEY_USER_AGENT, userAgent);
        }

        if (config.getDefParam() != null) {
            params.put(config.getDefParam());
        }
        if (config.getDefaultHeader() != null) {
            headers.put(config.getDefaultHeader());
        }

        mRequestInterceptor = new RequestInterceptor().enableLog(RexHttp.isDebug())
                                                      .tag(RexHttp.getGlobalTag());
    }

    public ApiParams getParam() {
        return this.params;
    }

    public BaseRequest setRequestInterceptor(RequestInterceptor interceptor) {
        mRequestInterceptor = interceptor;
        mRequestInterceptor.enableLog(RexHttp.isDebug())
                           .tag(RexHttp.getGlobalTag());
        return this;
    }

    public BaseRequest readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public BaseRequest writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public BaseRequest connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public BaseRequest baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!TextUtils.isEmpty(this.baseUrl)) {
            httpUrl = HttpUrl.parse(baseUrl);
        }
        return this;
    }

    public BaseRequest retryCount(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount must > 0");
        }
        this.retryCount = retryCount;
        return this;
    }

    public BaseRequest retryDelay(int retryDelay) {
        if (retryDelay < 0) {
            throw new IllegalArgumentException("retryDelay must > 0");
        }
        this.retryDelay = retryDelay;
        return this;
    }

    public BaseRequest retryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0) {
            throw new IllegalArgumentException("retryIncreaseDelay must > 0");
        }
        this.retryIncreaseDelay = retryIncreaseDelay;
        return this;
    }

    public BaseRequest addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
        return this;
    }

    public BaseRequest addNetworkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            networkInterceptors.add(interceptor);
        }
        return this;
    }

    public BaseRequest okProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public BaseRequest hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public BaseRequest certificates(InputStream... certificates) {
        this.mSslParams = CertifyManager.getSslSocketFactory(null, null, certificates);
        return this;
    }

    public BaseRequest certificates(InputStream bksFile, String password, InputStream... certificates) {
        this.mSslParams = CertifyManager.getSslSocketFactory(bksFile, password, certificates);
        return this;
    }

    public BaseRequest header(ApiHeaders header) {
        this.headers.put(header);
        return this;
    }

    public BaseRequest headers(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public BaseRequest removeHeader(String key) {
        headers.remove(key);
        return this;
    }

    public BaseRequest removeAllHeaders() {
        headers.clear();
        return this;
    }

    public BaseRequest params(ApiParams param) {
        this.params.put(param);
        return this;
    }

    public BaseRequest params(String key, String value) {
        params.put(key, value);
        return this;
    }

    public BaseRequest removeParam(String key) {
        params.remove(key);
        return this;
    }

    public BaseRequest removeAllParams() {
        params.clear();
        return this;
    }

    public BaseRequest sign(boolean sign) {
        this.mHasSign = sign;
        return this;
    }

    public BaseRequest timeStamp(boolean timeStamp) {
        this.mHasTimeStamp = timeStamp;
        return this;
    }

    public BaseRequest accessToken(boolean accessToken) {
        this.mHasAccessToken = accessToken;
        return this;
    }

    /** Default : Asynchronous */
    public BaseRequest syncRequest(boolean syncRequest) {
        this.isSyncRequest = syncRequest;
        return this;
    }

    /** Default : Asynchronous */
    public BaseRequest syncResponse(boolean syncResponse) {
        this.isSyncResponse = syncResponse;
        return this;
    }

    /**
     * Create OkHttp Client
     */
    private OkHttpClient.Builder generateHttpClient() {
        if (readTimeOut <= 0 && writeTimeOut <= 0 && connectTimeout <= 0 && mSslParams == null &&
            hostnameVerifier == null && proxy == null && headers.isEmpty()) {
            OkHttpClient.Builder builder = RexHttp.getHttpClientBuilder();
            for (Interceptor interceptor : builder.interceptors()) {
                if (interceptor instanceof AbstractSignInterceptor) {
                    ((AbstractSignInterceptor) interceptor).sign(mHasSign)
                                                           .timeStamp(mHasTimeStamp)
                                                           .accessToken(mHasAccessToken);
                }
            }
            return builder;
        } else {
            OkHttpClient.Builder newClientBuilder = RexHttp.getHttpClient()
                                                           .newBuilder();
            if (readTimeOut > 0) {
                newClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            }
            if (writeTimeOut > 0) {
                newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            }
            if (connectTimeout > 0) {
                newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            }
            if (hostnameVerifier != null) {
                newClientBuilder.hostnameVerifier(hostnameVerifier);
            }
            if (mSslParams != null) {
                newClientBuilder.sslSocketFactory(mSslParams.sSLSocketFactory, mSslParams.trustManager);
            }
            if (proxy != null) {
                newClientBuilder.proxy(proxy);
            }

            newClientBuilder.addInterceptor(new HeadersInterceptor(headers));

            for (Interceptor interceptor : interceptors) {
                newClientBuilder.addInterceptor(interceptor);
            }

            for (Interceptor interceptor : newClientBuilder.interceptors()) {
                if (interceptor instanceof AbstractSignInterceptor) {
                    ((AbstractSignInterceptor) interceptor).sign(mHasSign)
                                                           .timeStamp(mHasTimeStamp)
                                                           .accessToken(mHasAccessToken);
                }
            }

            if (networkInterceptors.size() > 0) {
                for (Interceptor interceptor : networkInterceptors) {
                    newClientBuilder.addNetworkInterceptor(interceptor);
                }
            }

            return newClientBuilder;
        }
    }

    /**
     * Create Retrofit
     */
    private Retrofit.Builder generateRetrofit() {
        return RexHttp.getRetrofitBuilder()
                      .baseUrl(baseUrl);
    }

    protected void checkValidate() {
        RexUtils.checkNotNull(iRetrofit, "Pls Invoke init() First");
    }

    public <T> T create(Class<T> service) {
        checkValidate();
        return iRetrofit.create(service);
    }

    protected BaseRequest init() {
        // Build Http Client
        OkHttpClient.Builder okHttpClientBuilder = generateHttpClient();
        okHttpClientBuilder.addInterceptor(mRequestInterceptor.setParams(params));
        okHttpClient = okHttpClientBuilder.build();

        // Build Retrofit
        Retrofit.Builder retrofitBuilder = generateRetrofit();
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        retrofitBuilder.client(okHttpClient);
        iRetrofit = retrofitBuilder.build();

        // Create Api Service
        apiManager = iRetrofit.create(ApiService.class);
        return this;
    }

    /**
     * Apply Request
     *
     * @return Observable<ResponseBody>
     */
    protected abstract Observable<ResponseBody> doRequest();

    public <T> Observable<DataResult<T>> start(ResponseParserFunction<T> parser) {
        return null;
    }

    public <T> Disposable start(ResponseWatcher<T> watcher, ResponseParserFunction<T> parser) {
        return null;
    }

    public <T> Disposable start(ResponseWatcherWrapper<? extends ApiResult<DataResult<T>>, T> watcherWrapper,
                                ResponseParserFunction<T> parser) {
        return null;
    }
}
