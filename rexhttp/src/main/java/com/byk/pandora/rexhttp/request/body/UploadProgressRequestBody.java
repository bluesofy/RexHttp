package com.byk.pandora.rexhttp.request.body;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.byk.pandora.rexhttp.watcher.ProgressResponseWatcher;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Byk on 2017/12/14.
 *
 * @author Byk
 */
public class UploadProgressRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private ProgressResponseWatcher mProgressWatcher;

    private CountingSink mCountingSink;

    public UploadProgressRequestBody(ProgressResponseWatcher watcher) {
        this(null, watcher);
    }

    public UploadProgressRequestBody(RequestBody requestBody, ProgressResponseWatcher watcher) {
        mRequestBody = requestBody;
        mProgressWatcher = watcher;
    }

    public UploadProgressRequestBody setRequestBody(RequestBody requestBody) {
        mRequestBody = requestBody;
        return this;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return mRequestBody.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        mCountingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(mCountingSink);

        mRequestBody.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private static final int REFRESH_INTERVAL = 100;

        private long bytesWritten = 0;
        private long contentLength = 0;
        private long lastRefreshUiTime;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (contentLength <= 0) {
                contentLength = contentLength();
            }

            bytesWritten += byteCount;

            long curTime = System.currentTimeMillis();
            if (curTime - lastRefreshUiTime >= REFRESH_INTERVAL || bytesWritten == contentLength) {
                mProgressWatcher.onUiThreadProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                lastRefreshUiTime = System.currentTimeMillis();
            }
        }
    }
}
