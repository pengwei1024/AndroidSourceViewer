package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.download.inteface.FileDownload;
import com.apkfuns.androidsourceviewer.entity.ClassFile;
import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.apkfuns.androidsourceviewer.util.HttpUtil;
import com.apkfuns.androidsourceviewer.util.Log;
import com.intellij.openapi.progress.util.StatusBarProgress;
import com.intellij.openapi.util.Pair;
import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AndroidOS 源码搜索
 * https://www.androidos.net.cn/androidossearch?query=ArrayMap.java&from=code
 */
public class AndroidOSSearch extends FileDownload {

    // 域名
    private static final String HOST = "https://www.androidos.net.cn";
    // 请求路径
    private static final String REQUEST_PATH = "https://www.androidos.net.cn/androidossearch?query=%S&from=code";

    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull DownloadTask[] tasks, @NotNull File outputFolder) {
        List<File> files = new ArrayList<>();
        for (DownloadTask task : tasks) {
            String url = String.format(REQUEST_PATH, task.getClassDescriptor().getFileName());
            try {
                String content = null;
                try {
                    content = HttpUtil.syncGet(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (content == null) {
                    continue;
                }
                Pattern pattern = Pattern.compile("<a target=\"_blank\" class=\"cleanurl\" href=\"(.*?)\"><b>.*</b></a>");
                Matcher matcher = pattern.matcher(content);
                String backup = null;
                String selectClass = null;
                while (matcher.find()) {
                    String searchItem = matcher.group(1);
                    if (task.checkUrlAvailable(searchItem)) {
                        if (backup == null) {
                            backup = searchItem;
                        }
                        if (searchItem.contains(task.getAndroidVersion())) {
                            selectClass = searchItem;
                            break;
                        }
                    }
                }
                if (selectClass == null && backup != null) {
                    selectClass = backup;
                }
                if (selectClass == null) {
                    break;
                }
                Log.debug("download:" + task + ", result:" + selectClass);
                String downUrl = HOST + selectClass.replace("/xref/", "/download/");
                // 匹配出实际的版本
                Pattern versionPattern = Pattern.compile("/android/(.*)/xref/");
                Matcher versionMatcher = versionPattern.matcher(selectClass);
                String versionName = task.getVersionName();
                if (versionMatcher.find()) {
                    versionName = versionMatcher.group(1);
                }
                ClassFile outputFile = new ClassFile(outputFolder, task.getCustomFileName(versionName));
                outputFile.setDownloadTask(task);
                if (outputFile.exists()) {
                    files.add(outputFile);
                    continue;
                }
                HttpRequests.request(downUrl).saveToFile(outputFile, new StatusBarProgress());
                if (outputFile.exists()) {
                    files.add(outputFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Pair.create(files.size() == tasks.length, files);
    }
}
