package de.uniquegame.containersort.api;

import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public enum SortType {

    ALPHABETICALLY("Alphabetically", "a_z"),
    HIGHEST_ITEM_AMOUNT("Highest Amount", "highest"),
    LOWEST_ITEM_AMOUNT("Lowest Amount", "lowest");

    private static final SortType[] VALUES = values();
    private static final Map<String, SortType> BY_NAME = Maps.newHashMap();

    private final String displayName;
    private final String shortName;

    SortType(@NotNull String displayName, @NotNull String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    @Nullable
    public static SortType getSortType(@NotNull String name) {
        return BY_NAME.get(name);
    }


    @Nullable
    public static SortType findByDisplayName(@NotNull String displayName) {

        SortType sortType = null;
        for (int i = 0; i < VALUES.length && sortType == null; i++) {
            SortType type = VALUES[i];
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                sortType = type;
            }
        }

        return sortType;
    }

    public String getShortName() {
        return shortName;
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

    @Nullable
    public static SortType findSortType(@NotNull List<Component> lines) {

        SortType sortType = null;
        for (int i = 0; i < lines.size() && sortType == null; i++) {
            String text = PlainTextComponentSerializer.plainText().serialize(lines.get(i));
            sortType = findByDisplayName(text);
            if (sortType == null) {
                sortType = findByShortName(text);
            }
        }

        return sortType;
    }

    @Nullable
    public static SortType findByShortName(@NotNull String shortName) {

        SortType sortType = null;
        for (int i = 0; i < VALUES.length && sortType == null; i++) {
            SortType type = VALUES[i];
            if (type.getShortName().equalsIgnoreCase(shortName)) {
                sortType = type;
            }
        }

        return sortType;
    }
}
