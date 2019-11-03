package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.download.AndroidOSDownload;
import com.apkfuns.androidsourceviewer.download.AndroidOSSearch;
import com.apkfuns.androidsourceviewer.download.XrefSearch;
import com.apkfuns.androidsourceviewer.download.XrefDownload;
import com.apkfuns.androidsourceviewer.download.inteface.FileDownload;
import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Pair;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 下载管理
 * 执行下载策略
 */
public class DownloadManager {
    // 单例
    private static volatile DownloadManager singleton;

    // 源码下载引擎List
    private List<FileDownload> sourceEngine = Arrays.asList(new AndroidOSDownload(), new XrefDownload());
    // 源码搜索引擎List
    private List<FileDownload> searchEngine = Arrays.asList(new AndroidOSSearch(), new XrefSearch());

    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        if (singleton == null) {
            synchronized (DownloadManager.class) {
                if (singleton == null) {
                    singleton = new DownloadManager();
                }
            }
        }
        return singleton;
    }

    /**
     * 通过搜索下载源码
     *
     * @param downloadTasks 下载任务
     * @param outputFolder  保存的文件夹
     * @param result        下载结果回调
     * @param retryIfError  是否错误时重试
     * @param isSync        是否同步执行
     */
    public void searchFile(final DownloadTask[] downloadTasks, @NotNull final File outputFolder,
                           @NonNull final DownloadResult<File> result, final boolean retryIfError,
                           final boolean isSync) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (callEngineDownload(searchEngine, downloadTasks, outputFolder, result, new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !retryIfError;
                    }
                })) {
                    return;
                }
                result.onFailure("download fail", null);
            }
        };
        if (isSync) {
            runnable.run();
        } else {
            ApplicationManager.getApplication().executeOnPooledThread(runnable);
        }
    }

    /**
     * 下载源码
     *
     * @param downloadTasks 下载任务
     * @param outputFolder  保存文件夹
     * @param result        下载回调
     * @param isSync        是否异步
     */
    public void downloadFile(final DownloadTask[] downloadTasks, @NotNull final File outputFolder,
                               @NonNull final DownloadResult<File> result, final boolean isSync) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (callEngineDownload(sourceEngine, downloadTasks, outputFolder, result, null)) {
                        return;
                    }
                    searchFile(downloadTasks, outputFolder, result, false, true);
                } catch (Exception e) {
                    result.onFailure("download fail:" + e.getMessage(), e);
                }
            }
        };
        if (isSync) {
            runnable.run();
        } else {
            ApplicationManager.getApplication().executeOnPooledThread(runnable);
        }
    }

    /**
     * 调用下载引擎进行下载
     *
     * @param sourceEngine  引擎列表
     * @param downloadTasks 下载任务
     * @param outputFolder  保存文件夹
     * @param result        下载回调
     * @param callable      是否打断运行
     * @return 是否下载完成
     */
    private boolean callEngineDownload(List<FileDownload> sourceEngine, DownloadTask[] downloadTasks, @NotNull File outputFolder,
                                 @NonNull DownloadResult<File> result, Callable<Boolean> callable) {
        Collections.sort(sourceEngine);
        int index = 0;
        while (index < sourceEngine.size()) {
            FileDownload engine = sourceEngine.get(index);
            // 永远尝试第一个, 后续失败过多引擎不尝试, 避免浪费时间
            if (index > 0 && !engine.enable()) {
                index++;
                continue;
            }
            long startTime = System.currentTimeMillis();
            Pair<Boolean, List<File>> resultPair = engine.onDownload(downloadTasks, outputFolder);
            long costTime = System.currentTimeMillis() - startTime;
            engine.updatePriority(resultPair.first, costTime);
            if (resultPair.first) {
                result.onSuccess(resultPair.second);
                return true;
            }
            // 允许 callable 回调打断执行
            try {
                if (callable != null && callable.call()) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            index++;
        }
        return false;
    }
}
