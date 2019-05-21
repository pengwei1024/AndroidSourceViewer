package com.apkfuns.androidsourceviewer.search;

import com.apkfuns.androidsourceviewer.entity.SearchEntity;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GoogleSearch implements CodeSearch {

    private static final String SEARCH_URL = "https://www.google.com/search?q=site%3Aandroid.googlesource.com+";

    @NotNull
    @Override
    public Set<SearchEntity> search(@NotNull String keyword) {
        Set<SearchEntity> result = new HashSet<>();
        try {
            Document doc = Jsoup.connect(SEARCH_URL + keyword).timeout(3000).get();
            Elements searchItems = doc.getElementsByClass("g");
            if (!searchItems.isEmpty()) {
                for (Element element : searchItems) {
                    Elements nodeA = element.getElementsByTag("a");
                    System.out.println(nodeA.first());
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
        return "google";
    }
}
