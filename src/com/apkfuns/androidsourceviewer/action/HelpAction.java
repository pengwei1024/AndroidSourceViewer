package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.action.base.BaseAction;
import com.apkfuns.androidsourceviewer.action.view.SettingDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 帮助
 */
public class HelpAction extends BaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SettingDialog(e.getProject()).setVisible(true);
    }
}
