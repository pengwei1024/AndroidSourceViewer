package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.download.inteface.FileDownload;
import com.apkfuns.androidsourceviewer.entity.ClassFile;
import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.util.Pair;
import com.intellij.platform.templates.github.DownloadUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XrefDownload extends FileDownload {

    // Android 下载链接
    private static final String DOWNLOAD_BASE_PATH = "http://androidxref.com/%s/raw/frameworks/base/core/java/%s";

    // androidXref 版本映射表
    private static final Map<String, String> VERSION_MAP = new HashMap<>();
    static {
        VERSION_MAP.put("9.0", "9.0.0_r3");
        VERSION_MAP.put("8.1", "8.1.0_r33");
        VERSION_MAP.put("8.0", "8.0.0_r4");
        VERSION_MAP.put("7.1", "7.1.1_r6");
        VERSION_MAP.put("7.0", "7.0.0_r1");
        VERSION_MAP.put("6.0", "6.0.1_r10");
        VERSION_MAP.put("5.1", "5.1.1_r6");
        VERSION_MAP.put("5.0", "5.0.0_r2");
        VERSION_MAP.put("4.4", "4.4.4_r1");
        VERSION_MAP.put("4.3", "4.3_r1");
        VERSION_MAP.put("4.2", "4.2.2_r1");
        VERSION_MAP.put("4.1", "4.1.1_r1");
        VERSION_MAP.put("4.0", "4.0.3_r1");
        VERSION_MAP.put("2.3", "2.3.7");
        VERSION_MAP.put("2.2", "2.2.3");
        VERSION_MAP.put("2.0", "2.1");
        VERSION_MAP.put("1.6", "1.6");
    }

    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull DownloadTask[] tasks, @NotNull File outputFolder) {
        List<File> fileList = new ArrayList<>();
        for (DownloadTask task : tasks) {
            String version = Utils.matchVersion(VERSION_MAP, task.getVersionName());
            String url = String.format(DOWNLOAD_BASE_PATH, version, task.getFullPath());
            ClassFile outFile = new ClassFile(outputFolder, task.getCustomFileName(version));
            if (outFile.exists()) {
                fileList.add(outFile);
                continue;
            }
            try {
                DownloadUtil.downloadAtomically(null, url, outFile);
            } catch (IOException e) {
                Log.e(e);
            }
            if (outFile.exists()) {
                fileList.add(outFile);
            }
        }
        return Pair.create(tasks.length == fileList.size(), fileList);
    }
}
