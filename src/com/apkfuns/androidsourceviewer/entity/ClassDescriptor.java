package com.apkfuns.androidsourceviewer.entity;

import com.apkfuns.androidsourceviewer.util.Utils;

/**
 * Java 类描述
 */
public class ClassDescriptor {
    // 文件分隔
    protected static final String SEPARATOR = System.getProperty("file.separator");
    // 扩展名
    private static final String[] EXT_ARRAY = {".java"};

    // 类的完整路径 => java.util.List
    protected String className;
    // 文件名 => List.java
    protected String fileName;
    // 包名 => java.util
    protected String packageName;

    public ClassDescriptor(Class source) {
        this.className = source.getName();
        this.init();
    }

    public ClassDescriptor(String className) {
        this.className = className;
        this.init();
    }

    /**
     * 初始化
     */
    protected void init() {
        int index = this.className.lastIndexOf(".");
        if (index >= 0) {
            fileName = this.className.substring(index + 1) + getExt()[0];
            packageName = this.className.substring(0, index);
        } else {
            fileName = className + getExt()[0];
            packageName = "";
        }
    }

    /**
     * 获取文件名
     * @return EG. List.java
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取包名
     * @return EG. java.util
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * class 名称
     *
     * @return 名称
     */
    public String getClassName() {
        return className;
    }

    /**
     * 是否为 Android 的类
     * @return bool
     */
    public boolean isAndroidClass() {
        return Utils.isAndroidClass(className);
    }

    /**
     * 获取扩展名
     *
     * @return 扩展名
     */
    protected String[] getExt() {
        return EXT_ARRAY;
    }

    /**
     * class 路径 java/util/List
     * @return string
     */
    public String getClassPath() {
        return className.replaceAll("\\.", SEPARATOR);
    }
}
