package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.app.Constant;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Map;

/**
 * Created by pengwei on 2017/11/5.
 */
public class Utils {

    public static boolean isEmpty(String text) {
        return StringUtil.isEmpty(text);
    }

    @Nullable
    public static String getClassPath(@NotNull AnActionEvent event) {
        String packageName = null;
        PsiElement element = event.getData(LangDataKeys.PSI_ELEMENT);
        if (element == null) {
            return packageName;
        }
        if (element instanceof PsiClass) {
            PsiClass cls = (PsiClass) element;
            if (cls.getContainingClass() != null) {
                // 排除内部类的情况
                packageName = cls.getContainingClass().getQualifiedName();
            } else {
                packageName = cls.getQualifiedName();
            }
            Log.debug("class => " + packageName);
        } else if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            Log.debug("method => " + method.getName() + " # "
                    + method.getContainingClass().getQualifiedName());
            packageName = method.getContainingClass().getQualifiedName();
        } else if (element instanceof PsiVariable) {
            PsiVariable psiVariable = (PsiVariable) element;
            packageName = psiVariable.getType().getCanonicalText();
            // 去除泛型
            if (!Utils.isEmpty(packageName)) {
                packageName = packageName.replaceAll("<.*>", "");
            }
            // FIXME: 2017/11/11 变量对应类是内部类会有问题
            Log.debug("PsiVariable:" + psiVariable.getType().getCanonicalText());
        } else {
            Log.debug("cls = " + element.getClass());
        }
        return packageName;
    }

    /**
     * 打开类文件
     *
     * @param filePath
     * @param project
     */
    public static void openFileInPanel(final String filePath, final Project project) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(filePath);
                if (file != null && file.isValid()) {
                    FileEditorProvider[] providers = FileEditorProviderManager.getInstance()
                            .getProviders(project, file);
                    if (providers.length != 0) {
                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                        FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
                    }
                }
            }
        });
    }

    /**
     * 根据包名前缀选择版本展示列表
     *
     * @param packageName
     * @return
     */
    @Nullable
    public static String[] getVersionList(@NotNull String packageName) {
        for (String prefix : Constant.ANDROID_PACKAGE_PREFIX) {
            if (packageName.startsWith(prefix)) {
                return Constant.ANDROID_VERSION_LIST;
            }
        }
        for (String prefix : Constant.JAVA_PACKAGE_PREFIX) {
            if (packageName.startsWith(prefix)) {
                return Constant.JAVA_VERSION_LIST;
            }
        }
        return null;
    }

    /**
     * 下载链接
     *
     * @param packageName
     * @param classPath
     * @param version
     * @return
     */
    public static String getDownloadUrl(String packageName, String classPath, String version) {
        for (String prefix : Constant.ANDROID_PACKAGE_PREFIX) {
            if (packageName.startsWith(prefix)) {
                return String.format(Constant.DOWNLOAD_BASE_PATH, version, classPath);
            }
        }
        for (String prefix : Constant.JAVA_PACKAGE_PREFIX) {
            if (packageName.startsWith(prefix)) {
                return String.format(Constant.JAVA_DOWNLOAD_BASE_PATH, version, classPath);
            }
        }
        return null;
    }

    /**
     * 打开文件夹
     */
    public static void openDirectory(String directory) {
        File file = new File(directory);
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建可点击的链接
     *
     * @param path
     * @return
     */
    public static String createClickableUrl(String path) {
        return "<a href=\"file://open?path=" + path + "\">打开文件夹</a>";
    }

    /**
     * 是否为 Android 类
     *
     * @param packageName 包名
     * @return bool
     */
    public static boolean isAndroidClass(String packageName) {
        for (String prefix : Constant.ANDROID_PACKAGE_PREFIX) {
            if (packageName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测服务是否正常
     *
     * @param url 请求链接
     * @return bool
     */
    public static boolean isConnected(String url) {
        try {
            HttpURLConnection conn =
                    (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 匹配最合适的版本
     *
     * @param version 8.1.0_r3
     * @return 8.0.0_r4
     */
    public static String matchVersion(Map<String, String> matchMap, String version) {
        // 优先全匹配
        for (String key: matchMap.keySet()) {
            if (version.startsWith(key)) {
                return matchMap.get(key);
            }
        }
        // 配置不到的情况下选择大版本, 如8.1配置8.0.0
        String[] versionGroup = version.split("\\.");
        if (versionGroup.length > 0) {
            for (String key: matchMap.keySet()) {
                if (key.startsWith(versionGroup[0])) {
                    return matchMap.get(key);
                }
            }
        }
        return version;
    }
}
