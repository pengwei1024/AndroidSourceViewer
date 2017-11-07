package com.apkfuns.androidsourceviewer;

/**
 * Created by pengwei on 2017/11/5.
 */
public interface Constant {

    String TITLE = "Android Source Viewer";

    // 版本分布
    String[] ANDROID_VERSION_LIST = {"Oreo - 8.0.0_r4", "Nougat - 7.1.1_r6", "Nougat - 7.0.0_r1",
            "Marshmallow - 6.0.1_r10", "Marshmallow - 6.0.0_r5", "Marshmallow - 6.0.0_r1",
            "Lollipop - 5.1.1_r6", "Lollipop - 5.1.0_r1", "Lollipop - 5.0.0_r2", "KitKat - 4.4.4_r1",
            "KitKat - 4.4.3_r1.1", "KitKat - 4.4.2_r2", "KitKat - 4.4.2_r1", "KitKat - 4.4",
            "Gingerbread - 2.3.7", "ICS - 4.0.3", "JellyBean - 4.3", "JellyBean - 4.1.2",
            "JellyBean - 4.2.2", "JellyBean - 4.2", "Froyo - 2.2.3", "Eclair - 2.1",
            "Donut - 1.6"};

    String[] JAVA_VERSION_LIST = {"Java-8-b132", "Java-8u40-b25", "Java-7-b147", "Java-7u40-b43",
            "Java-6-b27", "Java-6-b14"};

    // 包名前缀
    String[] JAVA_PACKAGE_PREFIX = {"com.oracle", "com.sun", "java", "javax", "sun", "sunw",
            "org.xml", "org.w3c", "org.relaxng", "org.omg", "org.jcp", "org.ietf", "jdk.internal"};

    String[] ANDROID_PACKAGE_PREFIX = {"android", "org.chromium", "frameworks", "com.android",
            "com.google", "com.googlecode", "com.example.android"};

    // Android 下载链接
    String DOWNLOAD_BASE_PATH = "http://androidxref.com/%s/raw/frameworks/base/core/java/%s";

    String JAVA_DOWNLOAD_BASE_PATH = "http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/%s/%s/?v=source&disposition=attachment";
}
