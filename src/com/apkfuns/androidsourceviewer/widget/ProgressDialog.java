package com.apkfuns.androidsourceviewer.widget;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBProgressBar;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by pengwei on 2017/11/7.
 */
public class ProgressDialog extends DialogWrapper {

    private final String title;

    public ProgressDialog(@Nullable Project project, String title) {
        super(project);
        this.title = title;
        this.myHelpAction = null;
        this.init();
        setResizable(false);
        setTitle(Constant.TITLE);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return createPanel(title);
    }

    public void onShow() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
    }

    private static JComponent createPanel(String text) {
        JLabel label = new JLabel(text);
        JBProgressBar progress = new JBProgressBar();
        progress.setIndeterminate(true);
        progress.setValue(30);
        progress.setOpaque(true);
        progress.setPreferredSize(new Dimension(400, progress.getPreferredSize().height));
        JPanel panel = new JPanel(new LineLayout(5));
        panel.add(label);
        panel.add(progress);
        return panel;
    }
}
