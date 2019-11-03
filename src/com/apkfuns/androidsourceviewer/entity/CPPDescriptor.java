package com.apkfuns.androidsourceviewer.entity;

/**
 * C/CPP 文件描述
 */
public class CPPDescriptor extends ClassDescriptor {

    // 扩展名
    private static final String[] EXT_ARRAY = {".c", ".cc", ".cpp"};

    public CPPDescriptor(Class source) {
        super(source);
    }

    public CPPDescriptor(String className) {
        super(className);
    }

    @Override
    protected void init() {
        this.fileName = this.className.replaceAll("\\.", "_") + getExt()[0];
        // 指定c++类放到 art.runtime.native
        this.packageName = "art.runtime.native";
    }

    @Override
    protected String[] getExt() {
        return EXT_ARRAY;
    }

    /**
     * @return java_lang_Thread
     */
    @Override
    public String getClassPath() {
        return this.className.replaceAll("\\.", "_");
    }
}
