package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiEditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.*;

/**
 * Created by pengwei on 2017/11/5.
 */
public class Utils {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
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
     * 打开浏览器
     *
     * @param url
     */
    public static void openUrl(String url) {
        if (SystemInfo.isWindows) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                URI uri = new URI(url);
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                if (desktop != null) {
                    desktop.browse(uri);
                }
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
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
            java.awt.Desktop.getDesktop().open(file);
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
     * @param packageName
     * @return
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
     * @param url 请求链接
     * @return
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
}
