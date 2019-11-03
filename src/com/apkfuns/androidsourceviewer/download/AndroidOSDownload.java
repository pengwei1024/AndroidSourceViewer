package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.download.inteface.FileDownload;
import com.apkfuns.androidsourceviewer.entity.ClassFile;
import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.progress.util.StatusBarProgress;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AndroidOS 源码下载
 * https://www.androidos.net.cn/android/9.0.0_r8/download/frameworks/support/collection/src/main/java/androidx/collection/ArrayMap.java
 */
public class AndroidOSDownload extends FileDownload {

    // 请求链接
    private static final String REQUEST_URL = "https://www.androidos.net.cn/android/%s/download/frameworks/base/core/java/%s";

    // AndroidOS网 版本映射表
    private static final Map<String, String> VERSION_MAP = new HashMap<>();
    static {
        VERSION_MAP.put("10.0", "10.0.0_r6");
        VERSION_MAP.put("9.0", "9.0.0_r8");
        VERSION_MAP.put("8.0", "8.0.0_r4");
        VERSION_MAP.put("7.1", "7.1.1_r28");
        VERSION_MAP.put("7.0", "7.0.0_r31");
        VERSION_MAP.put("6.0", "6.0.1_r16");
        VERSION_MAP.put("5.1", "5.1.0_r3");
        VERSION_MAP.put("5.0", "5.0.1_r1");
        VERSION_MAP.put("4.4", "4.4w_r1");
        VERSION_MAP.put("4.3", "4.3_r1");
        VERSION_MAP.put("4.2", "4.2.2_r1");
        VERSION_MAP.put("4.1", "4.1.1_r1");
        VERSION_MAP.put("4.0", "4.0.4_r2.1");
        VERSION_MAP.put("2.3", "2.3.7_r1");
        VERSION_MAP.put("2.2", "2.2_r1");
        VERSION_MAP.put("2.0", "2.0_r1");
        VERSION_MAP.put("1.6", "1.6_r1");
    }

    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull DownloadTask[] tasks, @NotNull File outputFolder) {
        List<File> files = new ArrayList<>();
        for (DownloadTask task : tasks) {
            String version = Utils.matchVersion(VERSION_MAP, task.getVersionName());
            ClassFile outputFile = new ClassFile(outputFolder, task.getCustomFileName(version));
            outputFile.setDownloadTask(task);
            if (outputFile.exists()) {
                files.add(outputFile);
                continue;
            }
            String downUrl = String.format(REQUEST_URL, version, task.getFullPath());
            Log.debug("download:" + task + ", result:" + downUrl);
            try {
                HttpRequests.request(downUrl).saveToFile(outputFile, new StatusBarProgress());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (outputFile.exists()) {
                files.add(outputFile);
            }
        }
        return Pair.create(files.size() == tasks.length, files);
    }
}
