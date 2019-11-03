package com.apkfuns.androidsourceviewer.action.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by pengwei on 17/2/22.
 */
public abstract class BaseDialog extends JDialog {

    public BaseDialog() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    /**
     * 界面居中
     */
    void setLocationCenter() {
        Dimension dimension = getSize();
        int windowWidth = dimension.width;
        int windowHeight = dimension.height;
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        this.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2); // 设置窗口居中显示
    }
}
