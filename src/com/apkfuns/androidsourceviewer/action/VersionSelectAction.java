package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.Constant;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.apkfuns.androidsourceviewer.entity.DownloadResult;
import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Created by pengwei on 2017/11/5.
 */
public class VersionSelectAction extends QuickSwitchSchemeAction implements DumbAware {

    private String packageName;

    public VersionSelectAction(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected void fillActions(Project project, @NotNull DefaultActionGroup defaultActionGroup, @NotNull DataContext dataContext) {
        for (String name : Constant.ANDROID_VERSION_LIST) {
            defaultActionGroup.add(new BaseVersionAction(name, packageName));
        }
    }

    private static class BaseVersionAction extends AnAction {
        private String versionName;
        private String packageName;

        public BaseVersionAction(@Nullable String text, @Nullable String packageName) {
            super(text);
            this.versionName = text;
            this.packageName = packageName;
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            int index = versionName.lastIndexOf("-");
            StringBuilder builder = new StringBuilder();
            String[] packages = packageName.split("\\.");
            for (int i = 0; i < packages.length; i++) {
                builder.append(packages[i]);
                if (i < packages.length - 1) {
                    builder.append("/");
                }
            }
            builder.append(".java");
            if (index >= 0) {
                String realName = versionName.substring(index + 1, versionName.length()).trim();
                System.out.println("selected:" + realName);

                String url = String.format(Constant.DOWNLOAD_BASE_PATH, realName, builder.toString());
                System.out.println("url = " + url);
                Utils.downloadFile(new String[]{url},
                        new File("/Users/baidu/Desktop/AndroidSourceViewer/" + versionName), new DownloadResult<File>() {
                            @Override
                            public void onSuccess(List<File> output) {
                                System.out.println("success: length=" + output.size());
                                Utils.openFileInPanel(output.get(0).getPath(), anActionEvent.getProject());
                            }

                            @Override
                            public void onFailure(String msg, Throwable throwable) {
                                System.out.println("onFailure=>" + msg);
                            }
                        });
            }
        }
    }
}
