package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
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
 */
public class SourceViewerAction extends BaseSourceAction implements PopListView.OnItemClickListener {

    @Override
    protected void onClassSelected(AnActionEvent event, String packageName) {
        new PopListView(event).createList("选择源码版本", data, this);
    }

    @Override
    public void OnItemClick(int position, final String version) {
        String title = "正在下载：" + version + " - " + packageName;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                ClassEntity entity = new ClassEntity(packageName, version);
                Utils.downloadFile(new String[]{entity.getDownloadUrl()},
                        new File("/Users/baidu/Desktop/AndroidSourceViewer/"
                                + entity.getParentPath()), new String[]{entity.getSavePath()}, new DownloadResult<File>() {
                            @Override
                            public void onSuccess(List<File> output) {
                                System.out.println("success: length=" + output.size());
                                if (output.isEmpty()) {
                                    return;
                                }
                                Utils.openFileInPanel(output.get(0).getPath(), SourceViewerAction.this.project);
                            }

                            @Override
                            public void onFailure(String msg, Throwable throwable) {
                                System.out.println("onFailure=>" + msg);
                            }
                        });
                progressIndicator.setFraction(0.5);
            }
        });
    }
}
