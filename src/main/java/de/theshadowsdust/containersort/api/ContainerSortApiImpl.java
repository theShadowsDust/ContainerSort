package de.theshadowsdust.containersort.api;

import de.theshadowsdust.containersort.configuration.ContainerSortConfiguration;
import de.theshadowsdust.containersort.configuration.ContainerSortProperty;
import de.theshadowsdust.containersort.service.ConfigurationService;
import de.theshadowsdust.containersort.service.LanguageService;
import de.theshadowsdust.containersort.service.PersistentDataStoreService;
import de.theshadowsdust.containersort.ContainerSortPlugin;
import de.theshadowsdust.containersort.util.InventoryUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ContainerSortApiImpl implements ContainerSortApi {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);
    private final ConfigurationService configurationService;
    private final ContainerSortPlugin plugin;
    private final PersistentDataStoreService persistentDataStoreService;
    private final LanguageService languageService;

    public ContainerSortApiImpl(@NotNull ContainerSortPlugin plugin) throws IOException {
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) Files.createDirectories(dataFolder.toPath());

        this.configurationService = new ConfigurationService(this);
        this.persistentDataStoreService = new PersistentDataStoreService(this);
        this.languageService = new LanguageService(this);
    }

    @Override
    public boolean isSortSign(@NotNull Sign sign) {

        NamespacedKey sortSign = this.persistentDataStoreService.getNameSpacedKey(PersistentDataStoreService.SORT_SIGN);
        if (sortSign == null) return false;

        PersistentDataContainer dataContainer = sign.getPersistentDataContainer();
        if (!dataContainer.has(sortSign)) return false;

        Byte signValue = dataContainer.get(sortSign, PersistentDataType.BYTE);
        return signValue != null && signValue == 1;
    }

    @Override
    public boolean isSignOwner(@NotNull UUID playerId, @NotNull Sign sign) {

        PersistentDataContainer container = sign.getPersistentDataContainer();
        NamespacedKey signOwnerKey = this.persistentDataStoreService.
                getNameSpacedKey(PersistentDataStoreService.SORT_SIGN_OWNER);

        if (signOwnerKey == null) return false;
        if (!container.has(signOwnerKey)) return false;

        UUID value = container.get(signOwnerKey, PersistentDataStoreService.UUID_TAG_TYPE);
        if (value == null) return false;

        return value.equals(playerId);
    }

    @Override
    public ContainerSortPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public PersistentDataStoreService getDataStoreService() {
        return this.persistentDataStoreService;
    }

    @Override
    public LanguageService getLanguageService() {
        return this.languageService;
    }

    @Override
    public ContainerSortConfiguration getConfiguration() {
        return this.configurationService.getConfiguration();
    }

    @Override
    public void reloadConfig() throws IOException {
        this.configurationService.reloadConfig();
        this.languageService.load();
    }

    @Override
    public <T> T getPropertyValue(@NotNull ContainerSortProperty<T> property, @NotNull Class<T> target) {
        return this.getConfiguration().getPropertyValue(property, target);
    }

    @Override
    public void saveSignData(@NotNull UUID playerId,
                             @NotNull String playerName,
                             @NotNull SortType sortType,
                             @NotNull Sign sign) {

        this.getDataStoreService().applyPersistentData(
                sign,
                PersistentDataStoreService.SORT_SIGN_OWNER,
                PersistentDataStoreService.UUID_TAG_TYPE, playerId);

        this.getDataStoreService().applyPersistentData(
                sign,
                PersistentDataStoreService.SORT_SIGN,
                PersistentDataType.BYTE,
                (byte) 1);

        sign.update();
    }

    @Override
    public boolean isValidContainer(@NotNull BlockState state) {
        if (!(state instanceof Container container)) return false;

        InventoryHolder inventoryHolder = container.getInventory().getHolder();
        if (inventoryHolder == null) return false;
        return this.getConfiguration().getAllowedContainers().contains(inventoryHolder.
                getInventory().getType().name().toLowerCase());
    }

    @Override
    public void sortContainer(@NotNull SortType sortType, @NotNull Inventory inventory) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack content : inventory.getContents()) {
            if (content != null) {
                list.add(content);
            }
        }
        switch (sortType) {
            case ALPHABETICALLY -> list.sort(InventoryUtil.SORT_ALPHABETICALLY);
            case HIGHEST_ITEM_AMOUNT -> list.sort(InventoryUtil.SORT_BY_HIGHEST_AMOUNT);
            case LOWEST_ITEM_AMOUNT -> list.sort(InventoryUtil.SORT_BY_LOWEST_AMOUNT);
            default -> throw new IllegalStateException("Unexpected value: " + sortType);
        }

        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            itemStacks[i] = list.get(i);
        }
        inventory.setContents(InventoryUtil.mergeSimilarStacks(itemStacks));
    }
}
