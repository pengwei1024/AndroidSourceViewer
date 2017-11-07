package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.Constant;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.templates.github.DownloadUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.impl.source.PsiFieldImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("class => " + cls.getQualifiedName());
            packageName = cls.getQualifiedName();
        } else if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            System.out.println("method => " + method.getName() + " # "
                    + method.getContainingClass().getQualifiedName());
            packageName = method.getContainingClass().getQualifiedName();
        } else if (element instanceof PsiVariable) {
            PsiVariable psiVariable = (PsiVariable) element;
            packageName = psiVariable.getType().getCanonicalText();
            System.out.println("PsiVariable:" + psiVariable.getType().getCanonicalText());
        } else {
            System.out.println("cls = " + element.getClass());
        }
        return packageName;
    }

    /**
     * 下载文件
     *
     * @param fileUrl 下载链接
     * @param outputFolder 保存文件夹
     * @param fileNames 保存文件名
     * @param result 下载回调
     */
    public static void downloadFile(String[] fileUrl, @NotNull File outputFolder, @Nullable String[] fileNames,
                                    DownloadResult<File> result) {
        try {
            if (fileUrl != null) {
                List<File> fileList = new ArrayList<File>();
                for (int i = 0; i < fileUrl.length; i++) {
                    String url = fileUrl[i];
                    if (url == null) {
                        continue;
                    }
                    String filename;
                    if (fileNames == null || isEmpty(fileNames[i])) {
                        filename = url.substring(url.lastIndexOf("/") + 1);
                    } else {
                        filename = fileNames[i];
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
            }
        } catch (IOException e) {
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
     * @param path
     * @return
     */
    public static String createClickableUrl(String path) {
        return "<a href=\"file://open?path=" + path + "\">打开文件夹</a>";
    }
}
