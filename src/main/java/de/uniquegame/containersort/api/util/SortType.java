package de.uniquegame.containersort.api.util;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum SortType {

    ALPHABETICALLY("Alphabetically"),
    HIGHEST_ITEM_AMOUNT("Highest Amount"),
    LOWEST_ITEM_AMOUNT("Lowest Amount");

    public static final Map<String, SortType> BY_NAME = Maps.newHashMap();

    private final String displayName;

    SortType(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public static SortType getSortType(@NotNull String name) {
        return BY_NAME.get(name);
    }

    @NotNull
    public String getDisplayName() {
        return this.displayName;
    }

    static {
        for (SortType sortType : values()) {
            BY_NAME.put(sortType.name(), sortType);
        }
    }
}
