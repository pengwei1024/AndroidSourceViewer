package com.apkfuns.androidsourceviewer.download;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public interface IDownload<T> {
    /**
     *
     * @param classEntities
     * @param outputFolder
     * @return 下载完成 / 下载结果集合
     */
    Pair<Boolean, List<T>> onDownload(@NotNull ClassEntity[] classEntities, @NotNull File outputFolder);
}
