package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.util.FileUtil;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.util.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 从 Google Source 下载源码
 * https://android.googlesource.com/platform/frameworks/base/+/android-7.0.0_r30/core/java/android/app/Activity.java?format=TEXT
 */
public class GoogleSourceDownload implements IDownload<File> {
    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull ClassEntity[] classEntities, @NotNull File outputFolder) {
        List<File> fileList = new ArrayList<>();
        for (ClassEntity classEntity : classEntities) {
            String version = classEntity.getVersionName();
            if (!version.contains("_r")) {
                version += "_r1";
            }
            String url = String.format(Locale.getDefault(), Constant.DOWNLOAD_GOOGLE_SOURCE,
                    version, classEntity.getLocalPath());
            Log.debug("GoogleSourceDownload => " + url);
            String filename;
            if (Utils.isEmpty(classEntity.getSaveName())) {
                filename = url.substring(url.lastIndexOf("/") + 1);
            } else {
                filename = classEntity.getSaveName();
            }
            File outFile = new File(outputFolder, filename);
            try {
                if (outFile.exists() && outFile.length() > 0) {
                    fileList.add(outFile);
                    continue;
                }
                String content = FileUtil.downloadContent(url);
                byte[] bytes = Base64.decodeBase64(content);
                FileUtils.write(outFile, new String(bytes), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outFile.exists() && outFile.length() > 0) {
                fileList.add(outFile);
            }
        }
        return Pair.create(classEntities.length == fileList.size(), fileList);
    }
}
