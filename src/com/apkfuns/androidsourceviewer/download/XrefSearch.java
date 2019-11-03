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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XrefSearch extends FileDownload {

    // androidXref 搜索
    private static final String ANDROID_SEARCH = "http://androidxref.com/%s/search?q=&defs=&refs=&path=%s&hist=&project=art&project=bionic&project=bootable&project=build&project=cts&project=dalvik&project=developers&project=development&project=device&project=docs&project=external&project=frameworks&project=hardware&project=kernel&project=libcore&project=libnativehelper&project=packages&project=pdk&project=platform_testing&project=prebuilts&project=sdk&project=system&project=test&project=toolchain&project=tools";

    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull DownloadTask[] tasks, @NotNull File outputFolder) {
        List<File> files = new ArrayList<>();
        for (DownloadTask task : tasks) {
            ClassFile outputFile = new ClassFile(outputFolder, task.getSaveFileName());
            if (outputFile.exists()) {
                files.add(outputFile);
                continue;
            }
            try {
                List<String> urlList = multiSearch(task, true);
                for (String url: urlList) {
                    if (task.checkUrlAvailable(url)) {
                        HttpRequests.request(url).saveToFile(outputFile, new StatusBarProgress());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Pair.create(files.size() == tasks.length, files);
    }


    /**
     * 源码搜索，单条结果
     *
     * @param downloadTask DownloadTask
     * @param format       是否替换 '.' 为 '%2F'
     * @return String
     * @throws Exception e
     */
    @Nullable
    private String singleSearch(final DownloadTask downloadTask, boolean format) throws Exception {
        String requestPath = downloadTask.getClassDescriptor().getClassName();
        if (format) {
            requestPath = requestPath.replaceAll("\\.", "%2F");
        }
        String url = String.format(ANDROID_SEARCH, downloadTask.getVersionName(), requestPath);
        Log.debug("url=" + url);
        String result = HttpUtil.syncGet(url);
        result = result.replace("\n", "");
        Pattern pattern = Pattern.compile("id=\"results\".*class=\"f\"><a href=\"(.*?)\">");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            String res = "http://androidxref.com" + matcher.group(1);
            return res.replace("/xref/", "/raw/");
        }
        return null;
    }

    /**
     * 源码搜索， 支持多条结果
     *
     * @param downloadTask DownloadTask
     * @param format       是否替换 '.' 为 '%2F'
     * @return List<String>
     * @throws Exception e
     */
    @NotNull
    private List<String> multiSearch(final DownloadTask downloadTask, boolean format) throws Exception {
        String requestPath = downloadTask.getClassDescriptor().getClassName();
        if (format) {
            requestPath = requestPath.replaceAll("\\.", "%2F");
        }
        List<String> arrays = new ArrayList<>();
        String url = String.format(ANDROID_SEARCH, downloadTask.getVersionName(), requestPath);
        String result = HttpUtil.syncGet(url);
        result = result.replace("\n", "");
        Pattern pattern = Pattern.compile("class=\"f\"><a href=\"(.*?)\">");
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            arrays.add(matcher.group(1));
        }
        return arrays;
    }
}
