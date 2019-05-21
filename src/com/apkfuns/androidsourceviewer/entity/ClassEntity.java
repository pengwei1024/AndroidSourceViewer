package com.apkfuns.androidsourceviewer.entity;

import com.apkfuns.androidsourceviewer.util.Utils;

public class ClassEntity {

    public static final String EXT = ".java";
    public static final String SEPARATOR = System.getProperty("file.separator");

    // 完整包名+类名 => java.util.List
    private final String packageName;
    // 文件名 => List.java
    private String fileName;
    // 文件名上一级包名 => java.util
    private String parentPackage;
    private String versionName;
    // 下载链接
    private String downloadUrl;

    public ClassEntity(String packageName, String versionName) {
        this.packageName = packageName;
        this.versionName = versionName.substring(versionName.indexOf("-") + 1).trim();
        int index = this.packageName.lastIndexOf(".");
        if (index >= 0) {
            fileName = this.packageName.substring(index + 1) + EXT;
            parentPackage = this.packageName.substring(0, index);
        } else {
            fileName = fileName + EXT;
            parentPackage = null;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getParentPackage() {
        return parentPackage;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getParentPath() {
        return parentPackage.replace(".", SEPARATOR);
    }

    public String getLocalPath() {
        return getParentPath() + SEPARATOR + fileName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getDownloadUrl() {
        if (!Utils.isEmpty(downloadUrl)) {
            return downloadUrl;
        }
        return Utils.getDownloadUrl(packageName, getLocalPath(), versionName);
    }

    public String getSaveName() {
        return getVersionName() + "-" + getFileName();
    }

    /**
     * 是否为 Android 的类
     * @return
     */
    public boolean isAndroidClass() {
        return Utils.isAndroidClass(packageName);
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
