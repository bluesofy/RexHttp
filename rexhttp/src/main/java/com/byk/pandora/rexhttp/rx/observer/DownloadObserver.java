package com.byk.pandora.rexhttp.rx.observer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.byk.pandora.rexhttp.RexUtils;
import com.byk.pandora.rexhttp.exception.ApiErrorCode;
import com.byk.pandora.rexhttp.exception.ApiException;
import com.byk.pandora.rexhttp.watcher.BaseProgressWatcher;
import com.byk.pandora.rexhttp.watcher.ResponseWatcher;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/21.
 *
 * @author Byk
 */
public class DownloadObserver<T extends ResponseBody> extends BaseObserver<T> {

    private static final String TAG = DownloadObserver.class.getSimpleName();

    private static final long REFRESH_INTERVAL = 50;

    private Context mContext;
    private String mPath;
    private String mName;
    private long mLastRefreshUiTime;

    private String mFileSuffix = "";

    private ResponseWatcher mWatcher;

    public DownloadObserver(Context context, String path, String name, ResponseWatcher watcher) {
        super(context);
        mContext = context;
        mPath = path;
        mName = name;
        mWatcher = watcher;

        mLastRefreshUiTime = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mWatcher != null) {
            mWatcher.onStart();
        }
    }

    @Override
    protected void onError(ApiException e) {
        sendError(e);
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
        Log.d(TAG, "DownloadObserver:>>>> onNext");
        writeToDisk(mContext, mPath, mName, t);
    }

    private void writeToDisk(Context context, String path, String name, ResponseBody body) {
        String type = "";
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
            type = mediaType.toString();
        }
        Log.d(TAG, "DownloadObserver-ContentType:>>>>" + type);

        // Define File Name
        if (TextUtils.isEmpty(name) || mediaType == null) {
            name = System.currentTimeMillis() + mFileSuffix;
        } else {
            switch (type) {
                case RexUtils.CONTENT_TYPE_APK:
                    mFileSuffix = ".apk";
                    break;
                case RexUtils.CONTENT_TYPE_JPG:
                    mFileSuffix = ".jpg";
                    break;
                case RexUtils.CONTENT_TYPE_PNG:
                    mFileSuffix = ".png";
                    break;
                default:
                    mFileSuffix = "." + mediaType.subtype();
                    break;
            }
            name += mFileSuffix;
        }

        // Define File Save Dir
        if (path == null) {
            path = context.getExternalFilesDir(null) + File.separator + name;
        } else {
            File file = new File(path);
            boolean valid = true;
            if (!file.exists()) {
                valid = file.mkdirs();
            }

            if (valid) {
                path = (path + File.separator + name).replaceAll("//", "/");
            } else {
                path = context.getExternalFilesDir(null) + File.separator + name;
            }
        }
        Log.d(TAG, "DownloadObserver-SavedDir:>>>>" + path);

        File outputFile = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            final long fileSize = body.contentLength();
            Log.d(TAG, "DownloadObserver-FileLength:>>>>" + fileSize);

            byte[] fileReader = new byte[4096];
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();
            outputStream = new FileOutputStream(outputFile);

            int read;
            while ((read = inputStream.read(fileReader)) != -1) {
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                Log.i(TAG, "DownloadObserver-FileDownload:>>>>" + fileSizeDownloaded + " of " + fileSize);

                float progress = fileSizeDownloaded * 1.0f / fileSize;
                long curTime = System.currentTimeMillis();
                if (curTime - mLastRefreshUiTime >= REFRESH_INTERVAL || progress == 1.0f) {
                    if (mWatcher != null) {
                        Observable.just(fileSizeDownloaded)
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new Consumer<Long>() {
                                      @Override
                                      public void accept(Long size) throws Exception {
                                          if (mWatcher instanceof BaseProgressWatcher) {
                                              int progress = (int) (size * 100 / fileSize);
                                              ((BaseProgressWatcher) mWatcher).update(size, fileSize, progress,
                                                                                      size == fileSize);
                                          }
                                      }
                                  });
                    }
                    mLastRefreshUiTime = System.currentTimeMillis();
                }
            }

            outputStream.flush();
            Log.i(TAG, "DownloadObserver-FileDownload:>>>>" + fileSizeDownloaded + " of " + fileSize);

            if (mWatcher != null) {
                Observable.just(path)
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new Consumer<String>() {
                              @Override
                              public void accept(String path) throws Exception {
                                  if (mWatcher instanceof BaseProgressWatcher) {
                                      ((BaseProgressWatcher) mWatcher).onDownloaded(path);
                                  }
                              }
                          });

                Log.i(TAG, "DownloadObserver-FileDownload:>>>>" + fileSizeDownloaded + " of " + fileSize);
                Log.i(TAG, "DownloadObserver-FileDownload:>>>>Success");
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendError(e);
        } finally {
            closeStream(inputStream, outputStream);
        }
    }

    private void sendError(Exception exp) {
        Log.d(TAG, "DownloadObserver:>>>> onError:" + exp.getMessage());
        if (mWatcher == null) {
            return;
        }

        Observable.just(new ApiException(exp, ApiErrorCode.DOWNLOAD_ERROR))
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Consumer<ApiException>() {
                      @Override
                      public void accept(ApiException e) throws Exception {
                          if (mWatcher != null) {
                              mWatcher.onError(e);
                          }
                      }
                  });
    }

    private void closeStream(Closeable... streams) {
        try {
            for (Closeable stream : streams) {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendError(e);
        }
    }
}
