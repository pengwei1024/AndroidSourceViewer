package com.apkfuns.androidsourceviewer.action.base;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

public abstract class BaseAction extends AnAction implements DumbAware {

    protected Project project;

    @Override
    public void actionPerformed(AnActionEvent event) {
        this.project = event.getProject();
    }

    @Override
    public final boolean isDumbAware() {
        return true;
    }
}
