package com.apkfuns.androidsourceviewer.util;

import com.intellij.util.io.HttpRequests;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FileUtil {

    /**
     * 下载内容到文本
     * @param url 请求链接
     * @return
     */
    public static String downloadContent(@NotNull String url) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            HttpRequests.request(url).productNameAsUserAgent().connect(new HttpRequests.RequestProcessor<Object>() {
                public Object process(@NotNull HttpRequests.Request request) throws IOException {
                    int contentLength = request.getConnection().getContentLength();
                    NetUtils.copyStreamContent(null, request.getInputStream(), byteArrayOutputStream, contentLength);
                    return null;
                }
            });
            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
