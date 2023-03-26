package de.theshadowsdust.containersort.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class ContainerSortProperty<T> {

    public static final ContainerSortProperty<String> SORT_SUCCESS_SOUND = new ContainerSortProperty<>("sortSuccess", "BLOCK_NOTE_BLOCK_PLING");
    public static final ContainerSortProperty<String> SIGN_IDENTIFIER = new ContainerSortProperty<>("signIdentifier", "[containersort]");

    public static final ContainerSortProperty<Boolean> PROTECT_SIGNS = new ContainerSortProperty<>("protectSigns", true);
    public static final ContainerSortProperty<Boolean> ITEM_TRANSFER_BLOCKED = new ContainerSortProperty<>("itemTransferBlocked", true);
    public static final ContainerSortProperty<Boolean> PROTECT_CONTAINERS = new ContainerSortProperty<>("protectContainers", true);
    public static final ContainerSortProperty<Double> MAX_SIGN_DISTANCE = new ContainerSortProperty<>("maxSignDistance", 4.0D);
    public static final List<ContainerSortProperty<?>> DEFAULTS = List.of(
            SORT_SUCCESS_SOUND,
            SIGN_IDENTIFIER,
            PROTECT_SIGNS,
            ITEM_TRANSFER_BLOCKED,
            PROTECT_CONTAINERS,
            MAX_SIGN_DISTANCE);

    private String key;
    private T value;

    public ContainerSortProperty(@NotNull String key, @NotNull T value) {
        this.key = key;
        this.value = value;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public void setKey(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public T getValue() {
        return value;
    }

    public void setValue(@NotNull T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerSortProperty<?> that)) return false;

        if (!Objects.equals(key, that.key)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
