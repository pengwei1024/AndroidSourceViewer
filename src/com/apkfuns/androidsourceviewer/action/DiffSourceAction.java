package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.apkfuns.androidsourceviewer.widget.PopListView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.FileDocumentContentImpl;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.List;

/**
 * Created by pengwei on 2017/11/6.
 * 对比两个类差异
 */
public class DiffSourceAction extends BaseSourceAction implements PopListView.OnItemClickListener {

    private PopListView popListView;
    private String firstValue;

    @Override
    protected void onClassSelected(AnActionEvent event, String packageName) {
        firstValue = null;
        popListView = new PopListView(event);
        popListView.createList("Choose a version", data, this);
    }

    @Override
    public void OnItemClick(int position, final String value) {
        if (firstValue == null) {
            Log.debug("first =>" + value);
            firstValue = value;
            String[] newArray = data.clone();
            for (int i = 0; i < newArray.length; i++) {
                if (value.equals(newArray[i])) {
                    newArray[i] = null;
                    break;
                }
            }
            popListView.createList("Choose another version", newArray, this);
        } else {
            System.out.println("second =>" + value);
            String title = "Download：" + packageName;
            ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    final ClassEntity entity1 = new ClassEntity(packageName, firstValue);
                    ClassEntity entity2 = new ClassEntity(packageName, value);
                    Utils.downloadFile(new ClassEntity[]{entity1, entity2},
                            new File(Constant.CACHE_PATH + entity1.getParentPath()),
                            new DownloadResult<File>() {
                                @Override
                                public void onSuccess(List<File> output) {
                                    Log.debug("success: length=" + output.size());
                                    if (output == null || output.size() < 2) {
                                        NotificationUtils.errorNotification("Error: Download " + entity1.getPackageName()
                                                + " Failure");
                                        return;
                                    }
                                    diff(project, output.get(0), output.get(1));
                                }

                                @Override
                                public void onFailure(String msg, Throwable throwable) {
                                    NotificationUtils.errorNotification("Error:" + msg);
                                }
                            }, entity1.isAndroidClass());
                }
            });
        }
    }

    /**
     * 调用 Android 文件对比
     * @param project
     * @param f1
     * @param f2
     */
    public static void diff(final Project project, final File f1, final File f2) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                VirtualFile v1 = LocalFileSystem.getInstance().findFileByIoFile(f1);
                Document document1 = FileDocumentManager.getInstance().getDocument(v1);
                FileDocumentContentImpl fileDocumentContent1 = new FileDocumentContentImpl(project, document1, v1);
                VirtualFile v2 = LocalFileSystem.getInstance().findFileByIoFile(f2);
                Document document2 = FileDocumentManager.getInstance().getDocument(v2);
                FileDocumentContentImpl fileDocumentContent2 = new FileDocumentContentImpl(project, document2, v2);
                SimpleDiffRequest simpleDiffRequest = new SimpleDiffRequest(Constant.TITLE, fileDocumentContent1, fileDocumentContent2,
                        f1.getName(), f2.getName());
                DiffManager.getInstance().showDiff(project, simpleDiffRequest);
            }
        });
    }
}
