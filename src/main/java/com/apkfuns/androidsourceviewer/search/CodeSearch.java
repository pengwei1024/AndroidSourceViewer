package com.apkfuns.androidsourceviewer.search;

import com.apkfuns.androidsourceviewer.entity.SearchEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface CodeSearch {
    @NotNull
    Set<SearchEntity> search(@NotNull String keyword);

    @NotNull
    String source();
}
