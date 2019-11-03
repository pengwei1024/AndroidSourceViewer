package com.apkfuns.androidsourceviewer.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 */
public interface DownloadResult<T> {
    void onSuccess(@NotNull List<T> output);
    void onFailure(@NotNull String msg, @Nullable Throwable throwable);
}
