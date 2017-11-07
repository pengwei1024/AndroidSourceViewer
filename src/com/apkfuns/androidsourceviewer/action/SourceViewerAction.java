package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.entity.DownloadResult;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.apkfuns.androidsourceviewer.widget.PopListView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 */
public class SourceViewerAction extends AnAction implements PopListView.OnItemClickListener {

    private PopListView popListView;
    private String classPath;
    private Project project;

    @Override
    public void actionPerformed(AnActionEvent event) {
        this.project = event.getProject();
        popListView = new PopListView(event);
        classPath = Utils.getClassPath(event);
        if (Utils.isEmpty(classPath)) {
            return;
        }
        String[] data = Utils.getVersionList(classPath);
        if (data == null) {
            return;
        }
        popListView.createList("选择源码版本", data, this);
    }

    @Override
    public void OnItemClick(int position, String version) {
        int index = version.indexOf("-");
        StringBuilder builder = new StringBuilder();
        String[] packages = classPath.split("\\.");
        for (int i = 0; i < packages.length; i++) {
            builder.append(packages[i]);
            if (i < packages.length - 1) {
                builder.append("/");
            }
        }
        String title = "正在下载：" + version + " - " + classPath;
        builder.append(".java");
        if (index >= 0) {
            String realName = version.substring(index + 1, version.length()).trim();
            System.out.println("selected:" + realName);
            String url = Utils.getDownloadUrl(classPath, builder.toString(), realName);
            System.out.println("url = " + url);
            ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    System.out.println("thread name=" + Thread.currentThread().getName());
                    Utils.downloadFile(new String[]{url},
                            new File("/Users/baidu/Desktop/AndroidSourceViewer/" + version), new DownloadResult<File>() {
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
}
