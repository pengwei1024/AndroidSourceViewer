package com.apkfuns.androidsourceviewer;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        for (String name: Constant.ANDROID_VERSION_LIST) {
            defaultActionGroup.add(new BaseVersionAction(name));
        }
    }

    private static class BaseVersionAction extends AnAction {
        private String versionName;

        public BaseVersionAction(@Nullable String text) {
            super(text);
            this.versionName = text;
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            int index = versionName.lastIndexOf("-");
            StringBuilder builder = new StringBuilder();
            if (index >= 0) {
                String realName = versionName.substring(index + 1, versionName.length()).trim();
                System.out.println("selected:" + realName);
            }
        }
    }
}
