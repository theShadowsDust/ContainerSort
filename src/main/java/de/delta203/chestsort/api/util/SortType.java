package de.delta203.chestsort.api.util;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum SortType {

    PLACEHOLDER,
    ALPHABETICALLY,
    HIGHEST_ITEM_AMOUNT,
    LOWEST_ITEM_AMOUNT;

    public static final Map<String, SortType> BY_NAME = Maps.newHashMap();

    @Nullable
    public static SortType getSortType(@NotNull String name) {
        return BY_NAME.get(name);
    }

    static {
        for (SortType sortType : values()) {
            BY_NAME.put(sortType.name(), sortType);
        }
    }
}
