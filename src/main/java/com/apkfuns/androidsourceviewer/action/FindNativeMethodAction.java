package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.action.base.BaseSourceAction;
import com.apkfuns.androidsourceviewer.download.SearchDownload;
import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.app.Constant;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.apkfuns.androidsourceviewer.widget.PopListView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.platform.templates.github.DownloadUtil;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;


public class FindNativeMethodAction extends BaseSourceAction implements PopListView.OnItemClickListener {
    private static final String[] NATIVE_EXT = {".cc", ".cpp", ".c"};

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
                download(value);
            }
        });
    }

    /**
     * download File
     * @param version source version
     */
    private void download(String version) {
        String searchKey = packageName.replaceAll("\\.", "_");
        ClassEntity classEntity = new ClassEntity(searchKey, version);
        try {
            List<String> urls = SearchDownload.onlineSearch(classEntity, false);
            if (urls.isEmpty()) {
                NotificationUtils.infoNotification("Did not find the relevant File");
                return;
            }
            for (String url : urls) {
                for (String ext : NATIVE_EXT) {
                    if (url.endsWith(ext)) {
                        Log.debug(url);
                        File downloadFile = new File(Constant.CACHE_PATH + "search/" + url);
                        url = "http://androidxref.com" + url.replace("/xref/", "/raw/");
                        DownloadUtil.downloadAtomically(null, url, downloadFile);
                        if (downloadFile.exists()) {
                            Utils.openFileInPanel(downloadFile.getPath(), project);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected String[] versionList() {
        return Constant.ANDROID_VERSION_LIST;
    }
}
