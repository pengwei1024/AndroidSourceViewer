package com.apkfuns.androidsourceviewer.icons;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;


/**
 * Created by pengwei on 2017/11/7.
 */
public class PluginIcons {

    public static final Icon ICON_JAVA = intellijLoad("/fileTypes/java.png");
    public static final Icon ICON_DIFF = intellijLoad("/actions/diff.png");
    public static final Icon GradleSync = load("/icons/gradlesync.png");

    private static Icon load(String path) {
        try {
            return IconLoader.getIcon(path, PluginIcons.class);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private static Icon intellijLoad(String path) {
        return IconLoader.getIcon(path, AllIcons.class);
    }
}
