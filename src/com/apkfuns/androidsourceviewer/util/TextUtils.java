package com.apkfuns.androidsourceviewer.util;

import org.jetbrains.annotations.Nullable;

public class TextUtils {

    public static boolean isEmpty(@Nullable String str) {
        if (str == null || str.trim().length() == 0)
            return true;
        else
            return false;
    }
}
