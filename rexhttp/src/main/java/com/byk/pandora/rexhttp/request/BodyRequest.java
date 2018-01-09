package com.byk.pandora.rexhttp.request;

import android.content.Context;
import android.support.annotation.Nullable;

import com.byk.pandora.rexhttp.model.ApiParams;
import com.byk.pandora.rexhttp.request.body.RequestBodyWrapper;
import com.byk.pandora.rexhttp.request.body.UploadProgressRequestBody;
import com.byk.pandora.rexhttp.watcher.ProgressResponseWatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.http.Body;

/**
 * Created by Byk on 2017/12/14.
 *
 * @author Byk
 */
public class BodyRequest extends BaseStartRequest {

    private String mTxt;
    private MediaType mTxtMediaType;

    private String mJson;
    private byte[] mBits;
    protected Object iObject;

    /** Customer RequestBody */
    private RequestBodyWrapper mRequestBodyWrapper;
    private RequestBody mRequestBody;

    /** 1-MultipartBody.Part, 2-Map RequestBody */
    public enum UploadType {
        PART, BODY
    }

    private UploadType mCurrentUploadType = UploadType.PART;

    public BodyRequest(Context context, String url) {
        super(context, url);
    }

    public BodyRequest requestBody(RequestBodyWrapper requestBodyWrapper) {
        mRequestBodyWrapper = requestBodyWrapper;
        mRequestBody = mRequestBodyWrapper.getRequestBody();
        params(mRequestBodyWrapper.getParams());
        return this;
    }

    public BodyRequest uploadType(UploadType uploadtype) {
        mCurrentUploadType = uploadtype;
        return this;
    }

    public BodyRequest upText(String txt) {
        mTxt = txt;
        mTxtMediaType = MediaType.parse("text/plain");
        return this;
    }

    public BodyRequest upText(String txt, String mediaType) {
        mTxt = txt;
        if (mediaType != null) {
            mTxtMediaType = MediaType.parse(mediaType);
        }
        return this;
    }

    public BodyRequest upObject(@Body Object object) {
        iObject = object;
        return this;
    }

    public BodyRequest upJson(String json) {
        mJson = json;
        return this;
    }

    public BodyRequest upBytes(byte[] bits) {
        mBits = bits;
        return this;
    }

    public BodyRequest params(String key, File file, ProgressResponseWatcher responseWatcher) {
        params.put(key, file, responseWatcher);
        return this;
    }

    public BodyRequest params(String key, InputStream stream, String fileName,
                              ProgressResponseWatcher responseWatcher) {
        params.put(key, stream, fileName, responseWatcher);
        return this;
    }

    public BodyRequest params(String key, byte[] bytes, String fileName, ProgressResponseWatcher responseWatcher) {
        params.put(key, bytes, fileName, responseWatcher);
        return this;
    }

    public BodyRequest params(String key, File file, String fileName, ProgressResponseWatcher responseWatcher) {
        params.put(key, file, fileName, responseWatcher);
        return this;
    }

    public <T> BodyRequest params(String key, T file, String fileName, MediaType contentType,
                                  ProgressResponseWatcher responseWatcher) {
        params.put(key, file, fileName, contentType, responseWatcher);
        return this;
    }

    public BodyRequest addFileParams(String key, List<File> files, ProgressResponseWatcher responseWatcher) {
        params.putFileParams(key, files, responseWatcher);
        return this;
    }

    public BodyRequest addFileWrapperParams(String key, List<ApiParams.FileWrapper> fileWrappers) {
        params.putFileWrapperParams(key, fileWrappers);
        return this;
    }

    @Override
    protected Observable<ResponseBody> doRequest() {
        if (mRequestBody != null) {
            return apiManager.postBody(url, mRequestBody);
        } else if (mJson != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mJson);
            return apiManager.postJson(url, body);
        } else if (iObject != null) {
            return apiManager.postBody(url, iObject);
        } else if (mTxt != null) {
            RequestBody body = RequestBody.create(mTxtMediaType, mTxt);
            return apiManager.postBody(url, body);
        } else if (mBits != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), mBits);
            return apiManager.postBody(url, body);
        }
        if (params.fileParamMap.isEmpty()) {
            return apiManager.post(url, params.urlParamMap);
        } else {
            if (mCurrentUploadType == UploadType.PART) {
                return uploadFilesWithPart();
            } else {
                return uploadFilesWithBody();
            }
        }
    }

    protected Observable<ResponseBody> uploadFilesWithPart() {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (Map.Entry<String, Object> mapEntry : params.urlParamMap.entrySet()) {
            parts.add(MultipartBody.Part.createFormData(mapEntry.getKey(), mapEntry.getValue()
                                                                                   .toString()));
        }

        for (Map.Entry<String, List<ApiParams.FileWrapper>> entry : params.fileParamMap.entrySet()) {
            List<ApiParams.FileWrapper> fileValues = entry.getValue();
            for (ApiParams.FileWrapper fileWrapper : fileValues) {
                MultipartBody.Part part = addFile(entry.getKey(), fileWrapper);
                parts.add(part);
            }
        }
        return apiManager.uploadFiles(url, parts);
    }

    protected Observable<ResponseBody> uploadFilesWithBody() {
        Map<String, RequestBody> mBodyMap = new HashMap<>();
        for (Map.Entry<String, Object> mapEntry : params.urlParamMap.entrySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), mapEntry.getValue()
                                                                                         .toString());
            mBodyMap.put(mapEntry.getKey(), body);
        }

        for (Map.Entry<String, List<ApiParams.FileWrapper>> entry : params.fileParamMap.entrySet()) {
            List<ApiParams.FileWrapper> fileValues = entry.getValue();
            for (ApiParams.FileWrapper fileWrapper : fileValues) {
                RequestBody requestBody = getBody(fileWrapper);
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody,
                                                                                                    fileWrapper.responseWatcher);
                mBodyMap.put(entry.getKey(), uploadProgressRequestBody);
            }
        }
        return apiManager.uploadFiles(url, mBodyMap);
    }

    private MultipartBody.Part addFile(String key, ApiParams.FileWrapper fileWrapper) {
        RequestBody requestBody = getBody(fileWrapper);
        if (requestBody == null) {
            return null;
        }

        RequestBody newRequestBody = (fileWrapper.responseWatcher != null) ?
                new UploadProgressRequestBody(requestBody, fileWrapper.responseWatcher) :
                requestBody;
        return MultipartBody.Part.createFormData(key, fileWrapper.fileName, newRequestBody);
    }

    private RequestBody getBody(final ApiParams.FileWrapper fileWrapper) {
        RequestBody requestBody = null;
        if (fileWrapper.file instanceof File) {
            requestBody = RequestBody.create(fileWrapper.contentType, (File) fileWrapper.file);
        } else if (fileWrapper.file instanceof InputStream) {
            requestBody = new RequestBody() {

                @Nullable
                @Override
                public MediaType contentType() {
                    return fileWrapper.contentType;
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    Source source = null;
                    try {
                        source = Okio.source((InputStream) fileWrapper.file);
                        sink.writeAll(source);
                    } finally {
                        Util.closeQuietly(source);
                    }
                }

                @Override
                public long contentLength() throws IOException {
                    try {
                        return ((InputStream) fileWrapper.file).available();
                    } catch (IOException e) {
                        return 0;
                    }
                }
            };
        } else if (fileWrapper.file instanceof byte[]) {
            requestBody = RequestBody.create(fileWrapper.contentType, (byte[]) fileWrapper.file);
        }
        return requestBody;
    }
}
