package com.apkfuns.androidsourceviewer.entity;

/**
 * Created by pengwei on 2017/11/5.
 */
public interface Constant {

    String TITLE = "Android Source Viewer";

    // 用户目录
    String USER_HOME = System.getProperty("user.home");
    // 缓存目录
    String CACHE_PATH = USER_HOME + "/.AndroidSourceViewerCache/";

    // 版本分布
    String[] ANDROID_VERSION_LIST = {"Oreo", "Oreo - 8.0.0_r4", "Nougat", "Nougat - 7.1.1_r6", "Nougat - 7.0.0_r1",
            "Marshmallow", "Marshmallow - 6.0.1_r10", "Marshmallow - 6.0.0_r5", "Marshmallow - 6.0.0_r1",
            "Lollipop", "Lollipop - 5.1.1_r6", "Lollipop - 5.1.0_r1", "Lollipop - 5.0.0_r2", "KitKat", "KitKat - 4.4.4_r1",
            "KitKat - 4.4.3_r1.1", "KitKat - 4.4.2_r2", "KitKat - 4.4.2_r1", "KitKat - 4.4",
            "Other", "Gingerbread - 2.3.7", "ICS - 4.0.3", "JellyBean - 4.3", "JellyBean - 4.1.2",
            "JellyBean - 4.2.2", "JellyBean - 4.2", "Froyo - 2.2.3", "Eclair - 2.1",
            "Donut - 1.6"};

    String[] JAVA_VERSION_LIST = {"Java8", "Java-8-b132", "Java-8u40-b25", "Java7", "Java-7-b147", "Java-7u40-b43",
            "Java6", "Java-6-b27", "Java-6-b14"};

    // 包名前缀
    String[] JAVA_PACKAGE_PREFIX = {"com.oracle", "com.sun", "java", "javax", "sun", "sunw",
            "org.xml", "org.w3c", "org.relaxng", "org.omg", "org.jcp", "org.ietf", "jdk.internal"};

    String[] ANDROID_PACKAGE_PREFIX = {"android", "org.chromium", "frameworks", "com.android",
            "com.google", "com.googlecode", "com.example.android", "org.json", "org.xml", "org.xmlpull",
            "org.w3c", "org.apache", "dalvik", "junit.framework", "junit.runner"};

    // Android 下载链接
    String DOWNLOAD_BASE_PATH = "http://androidxref.com/%s/raw/frameworks/base/core/java/%s";

    // Java 下载
    String JAVA_DOWNLOAD_BASE_PATH = "http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/%s/%s/?v=source&disposition=attachment";

    // Android 搜索
    String ANDROID_SEARCH = "http://androidxref.com/%s/search?q=&defs=&refs=&path=%s&hist=&project=art&project=bionic&project=bootable&project=build&project=cts&project=dalvik&project=developers&project=development&project=device&project=docs&project=external&project=frameworks&project=hardware&project=kernel&project=libcore&project=libnativehelper&project=packages&project=pdk&project=platform_testing&project=prebuilts&project=sdk&project=system&project=test&project=toolchain&project=tools";
}
