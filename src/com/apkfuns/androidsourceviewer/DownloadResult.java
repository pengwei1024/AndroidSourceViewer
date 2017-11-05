package com.apkfuns.androidsourceviewer;

import java.io.File;
import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 */
public interface DownloadResult {
    void onSuccess(List<File> output);
    void onFailure(String msg, Throwable throwable);
}
