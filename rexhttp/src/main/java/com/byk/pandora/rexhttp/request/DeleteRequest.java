package com.byk.pandora.rexhttp.request;

import android.content.Context;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/20.
 *
 * @author Byk
 */
public class DeleteRequest extends BaseStartRequest {

    public DeleteRequest(Context context, String url) {
        super(context, url);
    }

    @Override
    protected Observable<ResponseBody> doRequest() {
        return apiManager.delete(url, params.urlParamMap);
    }
}
