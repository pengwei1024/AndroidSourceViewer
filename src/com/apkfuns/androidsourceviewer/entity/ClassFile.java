package com.apkfuns.androidsourceviewer.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;

/**
 * class 文件
 */
public class ClassFile extends File {

    // 对应的下载任务
    @Nullable
    private DownloadTask downloadTask;

    public ClassFile(@NotNull String pathname) {
        super(pathname);
    }

    public ClassFile(String parent, @NotNull String child) {
        super(parent, child);
    }

    public ClassFile(File parent, @NotNull String child) {
        super(parent, child);
    }

    public ClassFile(@NotNull URI uri) {
        super(uri);
    }

    @Nullable
    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public void setDownloadTask(@Nullable DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }
}
