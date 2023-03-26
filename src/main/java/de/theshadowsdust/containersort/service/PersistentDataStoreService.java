package de.theshadowsdust.containersort.service;

import de.theshadowsdust.containersort.api.ContainerSortApi;
import de.theshadowsdust.containersort.api.UUIDTagType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PersistentDataStoreService {

    public static final String SORT_SIGN_OWNER = "sign_owner";
    public static final String SORT_SIGN = "sort_sign";
    public static final UUIDTagType UUID_TAG_TYPE = new UUIDTagType();

    private final ContainerSortApi api;
    private final List<NamespacedKey> nameSpacedKeys;

    public PersistentDataStoreService(@NotNull ContainerSortApi api) {
        this.api = api;
        this.nameSpacedKeys = new ArrayList<>();
        registerNameSpacedKey(SORT_SIGN, SORT_SIGN_OWNER);
    }

    @NotNull
    public List<NamespacedKey> getNameSpacedKeys() {
        return nameSpacedKeys;
    }

    @Nullable
    public NamespacedKey getNameSpacedKey(@NotNull String key) {

        NamespacedKey result = null;

        for (int i = 0; i < this.nameSpacedKeys.size() && result == null; i++) {
            NamespacedKey namespacedKey = this.nameSpacedKeys.get(i);
            if (namespacedKey.getKey().equalsIgnoreCase(key)) {
                result = namespacedKey;
            }
        }

        return result;
    }

    public void registerNameSpacedKey(String... keys) {
        for (String key : keys) {
            NamespacedKey namespacedKey = getNameSpacedKey(key);
            if (namespacedKey == null) {
                namespacedKey = new NamespacedKey(this.api.getPlugin(), key);
                this.nameSpacedKeys.add(namespacedKey);
            }
        }
    }

    public boolean has(@NotNull PersistentDataHolder dataHolder, @NotNull String key) {
        NamespacedKey namespacedKey = getNameSpacedKey(key);
        if (namespacedKey == null) return false;
        return dataHolder.getPersistentDataContainer().has(namespacedKey);
    }

    public void removePersistentData(@NotNull PersistentDataHolder dataHolder, @NotNull String key) {
        if (!has(dataHolder, key)) return;
        dataHolder.getPersistentDataContainer().remove(Objects.requireNonNull(getNameSpacedKey(key)));
    }

    public <T, Z> void applyPersistentData(PersistentDataHolder dataHolder,
                                           @NotNull String key,
                                           @NotNull PersistentDataType<T, Z> type,
                                           @NotNull Z value) {

        NamespacedKey namespacedKey = getNameSpacedKey(key);
        if (namespacedKey == null) return;
        dataHolder.getPersistentDataContainer().set(namespacedKey, type, value);
    }

}
