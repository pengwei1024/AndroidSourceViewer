package com.apkfuns.androidsourceviewer.download.inteface;

import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public interface IDownload<T> {
    /**
     * @param tasks        待下载的任务
     * @param outputFolder 下载保存的路径
     * @return {下载完成,下载结果集合}
     */
    Pair<Boolean, List<T>> onDownload(@NotNull DownloadTask[] tasks, @NotNull File outputFolder);
}
