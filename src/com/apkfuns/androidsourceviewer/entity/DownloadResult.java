package com.apkfuns.androidsourceviewer.entity;

import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 */
public interface DownloadResult<T> {
    void onSuccess(List<T> output);
    void onFailure(String msg, Throwable throwable);
}
