package com.apkfuns.androidsourceviewer.entity;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 处理 JList双击事件
 * @param <T> JList数据集合T
 */
public class ListDoubleClickEvent<T> extends MouseAdapter {

    private final DoubleClickListener<T> listener;

    public ListDoubleClickEvent(DoubleClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            JList theList = (JList) e.getSource();
            int index = theList.getSelectedIndex();
            if (this.listener != null) {
                this.listener.onDoubleClick(theList, index, (T) theList.getSelectedValue());
            }
        }
    }

    public interface DoubleClickListener<T> {
        void onDoubleClick(JList<T> jList, int position, T selectedValue);
    }
}
