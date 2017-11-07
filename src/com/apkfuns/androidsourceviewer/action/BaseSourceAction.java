package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public abstract class BaseSourceAction extends AnAction {

    protected String packageName;
    protected Project project;
    protected AnActionEvent actionEvent;
    protected String[] data;

    @Override
    public final void actionPerformed(AnActionEvent event) {
        this.project = event.getProject();
        this.actionEvent = event;
        packageName = Utils.getClassPath(event);
        if (Utils.isEmpty(packageName)) {
            NotificationUtils.infoNotification("Must be Java class or Method");
            return;
        }
        data = Utils.getVersionList(packageName);
        if (data == null) {
            NotificationUtils.infoNotification("Invalid PackageName:" + packageName);
            return;
        }
        onClassSelected(this.actionEvent, packageName);
    }

    protected abstract void onClassSelected(AnActionEvent event, String packageName);

}
