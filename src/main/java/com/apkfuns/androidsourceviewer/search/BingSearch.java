package com.apkfuns.androidsourceviewer.search;

import com.apkfuns.androidsourceviewer.entity.SearchEntity;
import com.apkfuns.androidsourceviewer.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BingSearch implements CodeSearch {

    private static final String SEARCH_URL = "https://cn.bing.com/search?q=site%3aandroid.googlesource.com+";

    public static void main(String[] args) {
        new BingSearch().search("LoadedApk");
    }

    @Override
    @NotNull
    public Set<SearchEntity> search(@NotNull String keyword) {
        Set<SearchEntity> result = new HashSet<>();
        try {
            Document doc = Jsoup.connect(SEARCH_URL + keyword).get();
            Elements searchItems = doc.getElementsByClass("b_title");
            if (!searchItems.isEmpty()) {
                for (Element element : searchItems) {
                    if (element.childNodeSize() > 0 && element.child(0).childNodeSize() > 0) {
                        Element nodeA = element.child(0).child(0);
                        String url = nodeA.attr("href");
                        if (!Utils.isEmpty(url)) {
                            System.out.println(url);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NotNull
    @Override
    public String source() {
        return "bing";
    }
}
