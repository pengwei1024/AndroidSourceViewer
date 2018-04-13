package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * 清理缓存的Java文件
 * Tools > Android Source Viewer > Clean Cache
 */
public class CleanAction extends AnAction {

    private File cacheFile;

    @Override
    public void actionPerformed(AnActionEvent e) {
        cacheFile = new File(Constant.CACHE_PATH);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        long size = FileUtils.sizeOfDirectory(cacheFile);
        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(Constant.TITLE);
        builder.resizable(false);
        builder.setCenterPanel(new JLabel(String.format("Currently occupy storage %.2fM, "
                + "Clean Cache immediately?", size/1024.0/1024.0),
                Messages.getInformationIcon(), SwingConstants.CENTER));
        builder.addOkAction().setText("Clean Now");
        builder.addCancelAction().setText("Cancel");
        builder.setButtonsAlignment(SwingConstants.RIGHT);
        if (builder.show() == 0) {
            clean();
        }
    }

    /**
     * clean cache directory
     */
    public void clean() {
        if (cacheFile != null) {
            try {
                FileUtils.cleanDirectory(cacheFile);
                NotificationUtils.infoNotification("Clean up successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
