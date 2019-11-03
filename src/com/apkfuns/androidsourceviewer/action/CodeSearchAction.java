package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.action.base.BaseAction;
import com.apkfuns.androidsourceviewer.action.view.SettingDialog;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;

/**
 * 使用搜索引擎进行源代码搜索
 * 进行站内搜索： site:android.googlesource.com  <源码关键词>
 * 如:  https://www.google.com/search?q=site%3Aandroid.googlesource.com+ActivityThread
 */
public class CodeSearchAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        super.actionPerformed(event);
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        String selectText = editor.getSelectionModel().getSelectedText();
        if (StringUtil.isEmpty(selectText)) {
            PsiElement element = event.getData(LangDataKeys.PSI_ELEMENT);
            if (element == null) {
                NotificationUtils.infoNotification("selected code empty");
                return;
            }
            String[] result = element.toString().split(":");
            if (result.length == 2) {
                selectText = result[1];
            } else {
                selectText = element.toString();
            }
        }
        selectText = selectText.trim().replaceAll("\\s+", "+");
        String url = "https://cn.bing.com/search?q=site%3Aandroid.googlesource.com+" + selectText;
        if (SettingDialog.configAccessExternalNetwork()) {
            url = "https://www.google.com/search?q=site%3Aandroid.googlesource.com+" + selectText;
        }
        BrowserUtil.browse(url);
    }
}
