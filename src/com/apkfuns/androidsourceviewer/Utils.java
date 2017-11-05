package com.apkfuns.androidsourceviewer;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.templates.github.DownloadUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 */
public class Utils {

    /**
     * 下载文件
     *
     * @param fileUrl
     * @param outputFolder
     * @param result
     */
    public static void download(String[] fileUrl, File outputFolder, DownloadResult result) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fileUrl != null) {
                        List<File> fileList = new ArrayList<File>();
                        for (String url : fileUrl) {
                            if (url == null) {
                                continue;
                            }
                            String filename = url.substring(url.lastIndexOf("/") + 1);
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
        });
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
                    try {
                        file.setWritable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
}
