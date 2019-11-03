package com.apkfuns.androidsourceviewer.action.view;

import com.apkfuns.androidsourceviewer.app.Constant;
import com.apkfuns.androidsourceviewer.util.GlobalStorageUtil;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.apkfuns.androidsourceviewer.widget.LineLayout;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class SettingDialog extends BaseDialog {
    // storage key
    private static final String ACCESS_EXTERNAL_NETWORK = "AccessExternalNetwork";
    // contentPane
    private JPanel contentPane;
    // 缓存目录
    private File cacheDir = new File(Constant.CACHE_PATH);
    // cleanDiskComponent
    private LabeledComponent cleanDiskComponent;

    public SettingDialog(final Project project) {
        super();
        setContentPane(contentPane);
        setModal(true);
        setTitle("设置");
        setSize(600, 350);
        setResizable(false);
        int border = 20;
        contentPane.setBorder(BorderFactory.createEmptyBorder(border, border, 0, 0));
        contentPane.setLayout(new LineLayout(10));
        // 判断是否可以访问外网
        JCheckBox checkBox = new JCheckBox("sure", configAccessExternalNetwork());
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                ButtonModel buttonModel = abstractButton.getModel();
                buttonModel.isSelected();
                GlobalStorageUtil.put(ACCESS_EXTERNAL_NETWORK, buttonModel.isSelected() ? "1" : "0");
            }
        });
        LabeledComponent netComponent = LabeledComponent.create(checkBox, "Can you access Google?");
        contentPane.add(netComponent);
        // 清理缓存
        JButton cleanDiskButton = new JButton("Clear immediately");
        cleanDiskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    FileUtils.cleanDirectory(cacheDir);
                    NotificationUtils.infoNotification("Clean up successfully");
                    collectCacheFile(project);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cleanDiskComponent = LabeledComponent.create(cleanDiskButton, "Collecting...");
        contentPane.add(cleanDiskComponent);
        collectCacheFile(project);
        setLocationCenter();
    }

    /**
     * 收集缓存信息
     *
     * @param project project
     */
    private void collectCacheFile(final Project project) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Collecting...", false) {
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Collecting...");
                final long size = FileUtils.sizeOfDirectory(cacheDir);
                UIUtil.invokeLaterIfNeeded(new Runnable() {
                    @Override
                    public void run() {
                        cleanDiskComponent.setText(String.format("Currently occupy storage %.2fM", size / 1024.0 / 1024.0));
                    }
                });
            }
        });
    }

    /**
     * 是否能访问外网
     *
     * @return bool
     */
    public static boolean configAccessExternalNetwork() {
        return "1".equals(GlobalStorageUtil.get(ACCESS_EXTERNAL_NETWORK, "1"));
    }
}
