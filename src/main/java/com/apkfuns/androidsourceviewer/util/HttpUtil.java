package com.apkfuns.androidsourceviewer.util;

import com.intellij.openapi.util.io.StreamUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    /**
     * Get 请求
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public static String syncGet(String uri) throws Exception {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        String result = new String(StreamUtil.loadFromStream(inStream));
        return result;
    }
}
