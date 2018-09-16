package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.util.Pair;
import com.intellij.platform.templates.github.DownloadUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XrefDownload implements IDownload<File> {
    @Override
    public Pair<Boolean, List<File>> onDownload(@NotNull ClassEntity[] classEntities, @NotNull File outputFolder) {
        List<File> fileList = new ArrayList<>();
        for (ClassEntity classEntity : classEntities) {
            String url = classEntity.getDownloadUrl();
            if (url == null) {
                continue;
            }
            String filename;
            if (Utils.isEmpty(classEntity.getSaveName())) {
                filename = url.substring(url.lastIndexOf("/") + 1);
            } else {
                filename = classEntity.getSaveName();
            }
            File outFile = new File(outputFolder, filename);
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
        return Pair.create(classEntities.length == fileList.size(), fileList);
    }
}
