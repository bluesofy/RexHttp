package com.byk.pandora.rexhttp.sample.task;

import android.content.Context;

import com.byk.pandora.rexhttp.RexHttp;
import com.byk.pandora.rexhttp.model.ApiHeaders;
import com.byk.pandora.rexhttp.model.ApiParams;
import com.byk.pandora.rexhttp.request.body.RequestBodyWrapper;
import com.byk.pandora.rexhttp.sample.bean.ApiCenter;
import com.byk.pandora.rexhttp.sample.http.parser.ConnectParser;
import com.byk.pandora.rexhttp.watcher.BaseWatcher;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Byk on 2018/1/9.
 *
 * @author Byk
 */
public class RequestBodyTask {

    private Context mContext;

    public RequestBodyTask(Context context) {
        mContext = context;
    }

    public void run(BaseWatcher<ApiCenter> watcher) {
        ApiParams params = getParams();
        RexHttp.post(mContext, "/xxx")
               .requestBody(getRequestBodyWrapper(params))
               .baseUrl(RexHttp.getBaseUrl())
               .start(watcher, new ConnectParser());
    }

    private ApiParams getParams() {
        ApiParams params = new ApiParams();
        params.put("oem_type", "");
        params.put("key", "");
        params.put("lang", "chs");
        params.put("serial_no", "sn-android-client");
        return params;
    }

    private RequestBodyWrapper getRequestBodyWrapper(ApiParams params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(ApiHeaders.CONTENT_TYPE_VALUE_RSA), "{}");
        return RequestBodyWrapper.build()
                                 .setParams(params)
                                 .setRequestBody(requestBody);
    }
}
