package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
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
                final ClassEntity entity = new ClassEntity(packageName, version);
                Utils.downloadFile(new ClassEntity[]{entity},
                        new File(Constant.CACHE_PATH + entity.getParentPath()),
                        new DownloadResult<File>() {
                            @Override
                            public void onSuccess(List<File> output) {
                                Log.debug("success: length=" + output.size());
                                if (output.isEmpty()) {
                                    NotificationUtils.errorNotification("Error: Download " + entity.getPackageName()
                                    + " Failure");
                                    return;
                                }
                                Utils.openFileInPanel(output.get(0).getPath(), SourceViewerAction.this.project);
                            }

                            @Override
                            public void onFailure(String msg, Throwable throwable) {
                                NotificationUtils.errorNotification("Error:" + msg);
                            }
                        }, entity.isAndroidClass());
                progressIndicator.setFraction(0.5);
            }
        });
    }
}
