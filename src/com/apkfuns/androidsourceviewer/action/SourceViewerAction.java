package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.action.base.BaseSourceAction;
import com.apkfuns.androidsourceviewer.util.DownloadManager;
import com.apkfuns.androidsourceviewer.entity.DownloadTask;
import com.apkfuns.androidsourceviewer.app.Constant;
import com.apkfuns.androidsourceviewer.util.DownloadResult;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.apkfuns.androidsourceviewer.widget.PopListView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 * 查看源码
 */
public class SourceViewerAction extends BaseSourceAction implements PopListView.OnItemClickListener {

    @Override
    protected void onClassSelected(AnActionEvent event, String packageName) {
        new PopListView(event).createList("Choose Source Version", data, this);
    }

    @Override
    public void OnItemClick(int position, final String version) {
        String title = "Download：" + version + " - " + packageName;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                final DownloadTask task = new DownloadTask(packageName, version);
                DownloadManager.getInstance().downloadFile(new DownloadTask[]{task},
                        new File(Constant.CACHE_PATH + task.getParentPath()),
                        new DownloadResult<File>() {
                            @Override
                            public void onSuccess(@NotNull List<File> output) {
                                Log.debug("DownloadResult=" + output);
                                if (output.isEmpty()) {
                                    NotificationUtils.errorNotification("Error: Download " + task);
                                    return;
                                }
                                Utils.openFileInPanel(output.get(0).getPath(), SourceViewerAction.this.project);
                            }

                            @Override
                            public void onFailure(@NotNull String msg, Throwable throwable) {
                                NotificationUtils.errorNotification("Error:" + msg);
                            }
                        }, true);
                progressIndicator.setFraction(0.5);
            }
        });
    }
}
