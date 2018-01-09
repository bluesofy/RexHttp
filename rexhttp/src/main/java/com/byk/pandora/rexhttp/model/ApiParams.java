package com.byk.pandora.rexhttp.model;

import com.byk.pandora.rexhttp.watcher.ProgressResponseWatcher;

import java.io.File;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * Created by Byk on 2017/12/11.
 *
 * @author Byk
 */
public class ApiParams {

    /** For Normal Params */
    public LinkedHashMap<String, Object> urlParamMap;

    /** For File Params */
    public LinkedHashMap<String, List<FileWrapper>> fileParamMap;

    public int contentType;

    public ApiParams() {
        init();
    }

    public ApiParams(String key, String value) {
        init();
        put(key, value);
    }

    private void init() {
        urlParamMap = new LinkedHashMap<>();
        fileParamMap = new LinkedHashMap<>();
        contentType = ApiHeaders.CONTENT_TYPE_JSON_NO_TOKEN;
    }

    public ApiParams contentType(int type) {
        contentType = type;
        return this;
    }

    public void put(ApiParams params) {
        if (params != null) {
            if (params.urlParamMap != null && !params.urlParamMap.isEmpty()) {
                urlParamMap.putAll(params.urlParamMap);
            }

            if (params.fileParamMap != null && !params.fileParamMap.isEmpty()) {
                fileParamMap.putAll(params.fileParamMap);
            }
        }
    }

    public void put(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        urlParamMap.putAll(params);
    }

    public void put(String key, String value) {
        urlParamMap.put(key, value);
    }

    public void put(String key, int value) {
        urlParamMap.put(key, value);
    }

    public <T extends File> void put(String key, T file, ProgressResponseWatcher responseCallBack) {
        put(key, file, file.getName(), responseCallBack);
    }

    public <T extends File> void put(String key, T file, String fileName, ProgressResponseWatcher responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    public <T extends InputStream> void put(String key, T file, String fileName,
                                            ProgressResponseWatcher responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    public void put(String key, byte[] bytes, String fileName, ProgressResponseWatcher responseCallBack) {
        put(key, bytes, fileName, guessMimeType(fileName), responseCallBack);
    }

    public void put(String key, FileWrapper fileWrapper) {
        if (key != null && fileWrapper != null) {
            put(key, fileWrapper.file, fileWrapper.fileName, fileWrapper.contentType, fileWrapper.responseWatcher);
        }
    }

    public <T> void put(String key, T content, String fileName, MediaType contentType,
                        ProgressResponseWatcher responseCallBack) {
        if (key != null) {
            List<FileWrapper> fileWrappers = fileParamMap.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParamMap.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper<>(content, fileName, contentType, responseCallBack));
        }
    }

    public <T extends File> void putFileParams(String key, List<T> files, ProgressResponseWatcher responseCallBack) {
        if (key != null && files != null && !files.isEmpty()) {
            for (File file : files) {
                put(key, file, responseCallBack);
            }
        }
    }

    public void putFileWrapperParams(String key, List<FileWrapper> fileWrappers) {
        if (key != null && fileWrappers != null && !fileWrappers.isEmpty()) {
            for (FileWrapper fileWrapper : fileWrappers) {
                put(key, fileWrapper);
            }
        }
    }

    public void removeUrl(String key) {
        urlParamMap.remove(key);
    }

    public void removeFile(String key) {
        fileParamMap.remove(key);
    }

    public void remove(String key) {
        removeUrl(key);
        removeFile(key);
    }

    public void clear() {
        urlParamMap.clear();
        fileParamMap.clear();
    }

    private MediaType guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        path = path.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return MediaType.parse(contentType);
    }

    public static class FileWrapper<T> {

        public T file;
        public String fileName;
        public MediaType contentType;
        public long fileSize;
        public ProgressResponseWatcher responseWatcher;

        public FileWrapper(T file, String fileName, MediaType contentType, ProgressResponseWatcher responseWatcher) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            if (file instanceof File) {
                this.fileSize = ((File) file).length();
            } else if (file instanceof byte[]) {
                this.fileSize = ((byte[]) file).length;
            }
            this.responseWatcher = responseWatcher;
        }

        @Override
        public String toString() {
            return "FileWrapper{" + "content=" + file + ", fileName='" + fileName + ", contentType=" + contentType +
                   ", fileSize=" + fileSize + '}';
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParamMap.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey())
                  .append("=")
                  .append(entry.getValue());
        }
        for (ConcurrentHashMap.Entry<String, List<FileWrapper>> entry : fileParamMap.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey())
                  .append("=")
                  .append(entry.getValue());
        }
        return result.toString();
    }
}
