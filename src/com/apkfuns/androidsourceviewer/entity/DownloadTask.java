package com.apkfuns.androidsourceviewer.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 下载任务描述类
 */
public class DownloadTask {
    // 文件分隔
    private static final String SEPARATOR = System.getProperty("file.separator");

    // 类描述
    @NotNull
    private final ClassDescriptor classDescriptor;
    // 完整的版本名称 8.0.0_r4
    private final String fullVersionName;
    // Android 版本 8.0.0
    private String androidVersion;
    // 源码版本 r4
    private String sourceVersion;

    public DownloadTask(@NotNull String className, @NotNull String versionName) {
        this(new ClassDescriptor(className), versionName);
    }

    public DownloadTask(@NotNull ClassDescriptor descriptor, @NotNull String versionName) {
        this.classDescriptor = descriptor;
        this.fullVersionName = versionName;
        String[] versionGroup = fullVersionName.split("_");
        if (versionGroup.length == 2) {
            this.androidVersion = versionGroup[0];
            this.sourceVersion = versionGroup[1];
        }
    }

    /**
     * 获取包名对应的路径
     *
     * @return java/util
     */
    public String getParentPath() {
        return classDescriptor.getPackageName().replaceAll("\\.", SEPARATOR);
    }

    /**
     * 获取类的完整路径
     *
     * @return java/util/List.java
     */
    public String getFullPath() {
        return getParentPath() + SEPARATOR + classDescriptor.getFileName();
    }

    /**
     * 获取版本名称
     *
     * @return 版本
     */
    @NotNull
    public String getVersionName() {
        return fullVersionName;
    }

    /**
     * Android版本
     */
    public String getAndroidVersion() {
        return androidVersion;
    }

    /**
     * 源码版本
     */
    public String getSourceVersion() {
        return sourceVersion;
    }

    /**
     * 获取本地保存的文件名
     *
     * @return 文件名
     */
    public String getSaveFileName() {
        return getVersionName() + "-" + classDescriptor.getFileName();
    }

    /**
     * 传入版本号获取自定义文件名
     *
     * @param versionName 版本名 8.1.0_r33
     * @return 文件名
     */
    public String getCustomFileName(String versionName) {
        return versionName + "-" + classDescriptor.getFileName();
    }

    /**
     * 获取class 描述
     *
     * @return ClassDescriptor
     */
    @NotNull
    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }

    /**
     * 是否为 Android 的类
     *
     * @return bool
     */
    public boolean isAndroidClass() {
        return classDescriptor.isAndroidClass();
    }

    @Override
    public String toString() {
        return "class=" + classDescriptor.getClassName() + ", version=" + fullVersionName;
    }

    /**
     * 检查下载链接是否符合预期
     *
     * @param targetUrl 下载链接
     * @return bool
     */
    public boolean checkUrlAvailable(@Nullable String targetUrl) {
        if (targetUrl == null) {
            return false;
        }
        for (String ext : classDescriptor.getExt()) {
            if (targetUrl.contains(classDescriptor.getClassPath() + ext)) {
                return true;
            }
        }
        return false;
    }
}
