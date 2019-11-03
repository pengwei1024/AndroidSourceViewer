package com.apkfuns.androidsourceviewer.download.inteface;

import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.apkfuns.androidsourceviewer.util.Log;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * 文件下载
 */
public abstract class FileDownload implements IDownload<File>, Comparable<FileDownload> {

    // 下载优先级
    private int priority = 0;
    // 平均时间
    private long costTimeAverage = 0;

    @Override
    public abstract Pair<Boolean, List<File>> onDownload(@NotNull DownloadTask[] tasks,
                                                         @NotNull File outputFolder);

    /**
     * 更新优先级
     *
     * @param isSuccess 是否下载成功
     * @param costTime  下载花费时间
     */
    public void updatePriority(boolean isSuccess, long costTime) {
        Log.debug("result:" + isSuccess + ", costTime:" + costTime + ", class:" + getClass()
                + ", priority:" + priority + ", average:" + costTimeAverage);
        priority = isSuccess ? priority + 1 : priority - 1;
        if (isSuccess && costTime > 0) {
            if (costTimeAverage == 0) {
                costTimeAverage = costTime;
            } else {
                costTimeAverage = (costTimeAverage + costTime) / 2;
            }
        }
    }

    @Override
    public int compareTo(@NotNull FileDownload o) {
        // > 0 是大数往后排
        if (Math.abs(Math.abs(priority) - Math.abs(o.priority)) < 3 && costTimeAverage > 0
                && o.costTimeAverage > 0) {
            return Long.compare(costTimeAverage, o.costTimeAverage);
        }
        return -Integer.compare(priority, o.priority);
    }

    // 暂停机制, 失败次数过多，暂停使用
    public boolean enable() {
        return this.priority >= -3;
    }
}
