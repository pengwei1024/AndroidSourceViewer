package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.apkfuns.androidsourceviewer.widget.GlobalSearchDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.platform.templates.github.DownloadUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class GlobalSearchAction extends AnAction implements GlobalSearchDialog.SearchFinishResult {
    private Project project;
    @Override
    public void actionPerformed(AnActionEvent event) {
        this.project = event.getProject();
        new GlobalSearchDialog(this).setVisible(true);
    }

    @Override
    public void OnResult(String result) {
        int extPos = result.lastIndexOf('/');
        if (extPos < 0 && extPos != result.length() - 1) {
            return;
        }
        String fileName = result.substring(extPos + 1);
        String title = "Download：" + fileName;
        File downloadFile = new File(Constant.CACHE_PATH + "search/" + fileName);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    DownloadUtil.downloadAtomically(null, result, downloadFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (downloadFile.exists()) {
                    Utils.openFileInPanel(downloadFile.getPath(), project);
                }
            }
        });

    }
}
