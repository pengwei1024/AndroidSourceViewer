package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.app.Constant;

import java.io.*;
import java.util.Properties;

/**
 * 全局存储工具类
 * Created by pengwei08 on 2015/7/23.
 */
public class GlobalStorageUtil {

    private static String fileName = Constant.GLOBAL_CONFIG_FILE;
    private static Properties props = new Properties();

    static {
        initialization();
    }

    /**
     * 初始化
     */
    private static void initialization() {
        try {
            File proFile = new File(fileName);
            File parentFile = proFile.getParentFile();
            if (!parentFile.exists() && parentFile.mkdirs()) {
                Log.debug("create folder " + parentFile.getAbsolutePath());
            }
            if (!proFile.exists() && proFile.createNewFile()) {
                Log.debug("create file " + proFile.getName());
            }
            props.load(new FileInputStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存
     */
    public static void put(String key, String value) {
        try {
            final OutputStream fos = new FileOutputStream(fileName);
            props.setProperty(key, value);
            props.store(fos, "");
        } catch (IOException e) {
            initialization();
            e.printStackTrace();
        }
    }

    /**
     * 读取
     */
    public static String get(String key, String defaultValue) {
        String value = props.getProperty(key);
        return value == null ? defaultValue : value;
    }
}
