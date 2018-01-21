package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.util.HttpUtil;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchDownload implements IDownload<File> {

    private Runnable timeoutListener;

    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull ClassEntity[] classEntities, @NotNull File outputFolder) {
        String newUrl = null;
        try {
            newUrl = androidOnlineSearch(classEntities[0]);
        } catch (ConnectException | UnknownHostException e1) {
            if (timeoutListener != null) {
                timeoutListener.run();
            }
        } catch (Exception e) {
            Log.e(e);
        }
        if (!Utils.isEmpty(newUrl)) {
            for (ClassEntity entity : classEntities) {
                entity.setDownloadUrl(newUrl);
            }
            Log.debug("SearchDownload => " + newUrl);
            return new XrefDownload().onDownload(classEntities, outputFolder);
        }
        return Pair.create(false, Collections.<File>emptyList());
    }

    public SearchDownload setTimeoutListener(Runnable timeoutListener) {
        this.timeoutListener = timeoutListener;
        return this;
    }

    public static String androidOnlineSearch(final ClassEntity classEntity) throws Exception {
        return androidOnlineSearch(classEntity, true);
    }

    @Nullable
    public static String androidOnlineSearch(final ClassEntity classEntity, boolean format) throws Exception {
        String requestPath = classEntity.getPackageName();
        if (format) {
            requestPath = requestPath.replaceAll("\\.", "%2F");
        }
        String url = String.format(Constant.ANDROID_SEARCH, classEntity.getVersionName(), requestPath);
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
     * @param classEntity
     * @param format
     * @return
     * @throws Exception
     */
    @NotNull
    public static List<String> onlineSearch(final ClassEntity classEntity, boolean format) throws Exception {
        String requestPath = classEntity.getPackageName();
        if (format) {
            requestPath = requestPath.replaceAll("\\.", "%2F");
        }
        List<String> arrays = new ArrayList<>();
        String url = String.format(Constant.ANDROID_SEARCH, classEntity.getVersionName(), requestPath);
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
