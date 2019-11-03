package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.action.base.BaseSourceAction;
import com.apkfuns.androidsourceviewer.entity.CPPDescriptor;
import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.apkfuns.androidsourceviewer.app.Constant;
import com.apkfuns.androidsourceviewer.util.*;
import com.apkfuns.androidsourceviewer.widget.PopListView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * java native方法源码定位
 */
public class FindNativeMethodAction extends BaseSourceAction implements PopListView.OnItemClickListener {
    // Native Method
    private final PsiMethod psiMethod;

    public FindNativeMethodAction(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    @Override
    protected void onClassSelected(AnActionEvent event, String packageName) {
        new PopListView(event).createList("Choose Source Version", data, this);
    }

    @Override
    public void OnItemClick(int position, final String value) {
        String title = "Download Native File for " + psiMethod.getName();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                final DownloadTask downloadTask = new DownloadTask(new CPPDescriptor(packageName), value);
                DownloadManager.getInstance().searchFile(new DownloadTask[]{downloadTask},
                        new File(Constant.CACHE_PATH, downloadTask.getParentPath()),
                        new DownloadResult<File>() {
                            @Override
                            public void onSuccess(@NotNull List<File> output) {
                                if (!output.isEmpty()) {
                                    Utils.openFileInPanel(output.get(0).getPath(), project);
                                } else {
                                    NotificationUtils.errorNotification("Error: Download " + downloadTask);
                                }
                            }

                            @Override
                            public void onFailure(@NotNull String msg, @Nullable Throwable throwable) {
                                NotificationUtils.errorNotification("Error:" + msg);
                            }
                        }, true, true);
            }
        });
    }

    @Override
    protected String[] versionList() {
        return Constant.ANDROID_VERSION_LIST;
    }
}
