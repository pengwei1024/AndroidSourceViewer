package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class DownloadManager {
    private static volatile DownloadManager singleton;
    private long lastDetectTime = 0;
    private Status status = Status.NONE;

    enum Status {
        NONE, SUCCESS, WAIT
    }

    // 网络测探
    private final String NET_DETECTOR = "http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/Activity.java";
    private final static long MAX_WAIT_STAMP = 30 * 60 * 1000;

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
     * 下载文件
     *
     * @param classEntities 需要下载的类
     * @param outputFolder  保存文件夹
     * @param result        下载回调
     */
    public void downloadFile(@NotNull ClassEntity[] classEntities, @NotNull File outputFolder,
                             @NotNull DownloadResult<File> result) {
        if (classEntities[0].isAndroidClass()) {
            switch (status) {
                case NONE:
                    boolean netResult = Utils.isConnected(NET_DETECTOR);
                    status = netResult ? Status.SUCCESS : Status.WAIT;
                    lastDetectTime = System.currentTimeMillis();
                    Log.debug("net detector:" + status + "," + lastDetectTime);
                    downloadFile(classEntities, outputFolder, result);
                    break;
                case SUCCESS:
                    Pair<Boolean, List<File>> pair = new XrefDownload().onDownload(classEntities, outputFolder);
                    if (pair.first) {
                        result.onSuccess(pair.second);
                    } else {
                        pair = new SearchDownload().setTimeoutListener(new Runnable() {
                            @Override
                            public void run() {
                                status = Status.NONE;
                            }
                        }).onDownload(classEntities, outputFolder);
                        result.onSuccess(pair.second);
                    }
                    break;
                case WAIT:
                    if (System.currentTimeMillis() - lastDetectTime > MAX_WAIT_STAMP) {
                        downloadFile(classEntities, outputFolder, result);
                        status = Status.NONE;
                    } else {
                        result.onSuccess(new GoogleSourceDownload().onDownload(classEntities, outputFolder).second);
                    }
                    break;
                default:
                    break;
            }
        } else {
            result.onSuccess(new XrefDownload().onDownload(classEntities, outputFolder).second);
        }
    }
}
