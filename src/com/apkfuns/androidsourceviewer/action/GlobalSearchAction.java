package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.widget.GlobalSearchDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class GlobalSearchAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        new GlobalSearchDialog().setVisible(true);
    }
}
