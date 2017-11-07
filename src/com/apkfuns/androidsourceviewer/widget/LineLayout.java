package com.apkfuns.androidsourceviewer.widget;

import java.awt.*;

/**
 * Created by pengwei on 17/2/20.
 */
public class LineLayout implements LayoutManager {

    private int padding;

    public LineLayout() {
        padding = 1;
    }

    public LineLayout(int padding) {
        this.padding = padding;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int count = target.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component m = target.getComponent(i);
                Dimension d = m.getPreferredSize();
                dim.width = Math.max(dim.width, d.width);
                dim.height += d.height + padding;
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + padding * 2;
            dim.height += insets.top + insets.bottom + padding * 2;
            return dim;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right);
        int count = parent.getComponentCount();
        int height = 0;
        int gap = padding;
        for (int i = 0; i < count; i++) {
            Component component = parent.getComponent(i);
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();
                component.setBounds(gap, height, maxWidth - gap * 2, size.height);
                height += size.height + gap * 2;
            }
        }
    }
}
