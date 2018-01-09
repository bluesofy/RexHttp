package com.byk.pandora.rexhttp.request;

import android.content.Context;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/20.
 *
 * @author Byk
 */
public class PutRequest extends BodyRequest {

    public PutRequest(Context context, String url) {
        super(context, url);
    }

    @Override
    protected Observable<ResponseBody> doRequest() {
        if (iObject == null) {
            return apiManager.put(url, params.urlParamMap);
        } else {
            return apiManager.putBody(url, iObject);
        }
    }
}
