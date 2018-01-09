package com.byk.pandora.rexhttp.watcher;

/**
 * Created by Byk on 2017/12/11.
 *
 * @author Byk
 */
public interface ProgressResponseWatcher {

    /**
     * Trans Progress
     *
     * @param bytesWritten  Written.
     * @param contentLength Total Length.
     * @param done          Whether Finished.
     */
    void onUiThreadProgress(long bytesWritten, long contentLength, boolean done);
}
