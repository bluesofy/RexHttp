package com.byk.pandora.rexhttp.watcher;

/**
 * Created by Byk on 2017/12/21.
 *
 * @author Byk
 */
public abstract class BaseProgressWatcher<T> extends ResponseWatcher<T> {

    /**
     * Download Progress
     *
     * @param bytesRead     Downloaded Size
     * @param contentLength File Total Size
     * @param progress      Download Progress
     * @param done          Download Complete
     */
    public abstract void update(long bytesRead, long contentLength, int progress, boolean done);

    /**
     * Download Complete
     *
     * @param path Saved Path
     */
    public abstract void onDownloaded(String path);

}
