package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.templates.github.DownloadUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 下载文件
     *
     * @param classEntities 需要下载的类
     * @param outputFolder  保存文件夹
     * @param result        下载回调
     * @param needRetry     失败是否需要重试
     */
    public static void downloadFile(@NotNull ClassEntity[] classEntities, @NotNull File outputFolder,
                                    DownloadResult<File> result, boolean needRetry) {
        try {
            List<File> fileList = new ArrayList<File>();
            for (int i = 0; i < classEntities.length; i++) {
                String url = classEntities[i].getDownloadUrl();
                if (url == null) {
                    continue;
                }
                String filename;
                if (isEmpty(classEntities[i].getSaveName())) {
                    filename = url.substring(url.lastIndexOf("/") + 1);
                } else {
                    filename = classEntities[i].getSaveName();
                }
                File outFile = new File(outputFolder, filename);
                if (outFile.exists()) {
                    fileList.add(outFile);
                    continue;
                }
                DownloadUtil.downloadAtomically(null, url, outFile);
                if (outFile.exists()) {
                    fileList.add(outFile);
                }
            }
            if (result != null) {
                result.onSuccess(fileList);
            }
        } catch (IOException e) {
            // Android 重试一次
            if (needRetry) {
                String newUrl = androidOnlineSearch(classEntities[0]);
                Log.debug( "重试Url:" + newUrl + "  错误原因:" + e.getMessage());
                if (!isEmpty(newUrl)) {
                    for (ClassEntity entity : classEntities) {
                        entity.setDownloadUrl(newUrl);
                    }
                    downloadFile(classEntities, outputFolder, result, false);
                    return;
                }
            }
            if (result != null) {
                result.onFailure(e.getMessage(), e);
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
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
                if (file != null && file.isValid()) {
                    FileEditorProvider[] providers = FileEditorProviderManager.getInstance()
                            .getProviders(project, file);
                    if (providers.length != 0) {
                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                        FileEditorManager.getInstance(project).openTextEditor(descriptor, false);
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
     * Get 请求
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public static String syncGet(String uri) throws Exception {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        String result = new String(StreamUtil.loadFromStream(inStream));
        return result;
    }

    /**
     * Android 在线搜索源码
     * 解决像 JsonObject 和其他 Android 源码不在一个目录下
     * @param classEntity
     * @return 源码下载地址
     */
    public static String androidOnlineSearch(final ClassEntity classEntity) {
        try {
            String requestPath = classEntity.getPackageName().replaceAll("\\.", "%2F");
            String url = String.format(Constant.ANDROID_SEARCH, classEntity.getVersionName(), requestPath);
            Log.debug("url=" + url);
            String result = Utils.syncGet(url);
            result = result.replace("\n", "");
            Pattern pattern = Pattern.compile("id=\"results\".*class=\"f\"><a href=\"(.*?)\">");
            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                String res = "http://androidxref.com" + matcher.group(1);
                return res.replace("/xref/", "/raw/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
