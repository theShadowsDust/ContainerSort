package de.uniquegame.containersort.api;

import de.uniquegame.containersort.ContainerSortPlugin;
import de.uniquegame.containersort.configuration.ContainerSortPluginConfiguration;
import de.uniquegame.containersort.configuration.ContainerSortSettings;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.service.PersistentDataStoreService;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ContainerSortApiImpl implements ContainerSortApi {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);
    private final ContainerSortPlugin plugin;
    private final ContainerSortPluginConfiguration configuration;
    private final PersistentDataStoreService persistentDataStoreService;
    private final LanguageService languageService;

    public ContainerSortApiImpl(@NotNull ContainerSortPlugin plugin) {
        this.plugin = plugin;
        this.configuration = new ContainerSortPluginConfiguration(this);
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
    public ContainerSortSettings getSettings() {
        return this.configuration.getSettings();
    }

    @Override
    public void saveSignData(@NotNull UUID playerId,
                             @NotNull String playerName,
                             @NotNull SortType sortType,
                             @NotNull SignChangeEvent event) {

        Sign sign = (Sign) event.getBlock().getState();

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

        List<Component> signLayout = this.getLanguageService().getSignLayout(playerName, sortType);
        for (int i = 0; i < signLayout.size(); i++) {
            event.line(i, signLayout.get(i));
        }
    }

    @Override
    public boolean isValidContainer(@NotNull BlockState state) {
        if (!(state instanceof Container container)) return false;

        InventoryHolder inventoryHolder = container.getInventory().getHolder();
        if (inventoryHolder == null) return false;
        return this.getSettings().getAllowedContainers().contains(inventoryHolder.
                getInventory().getType().name().toLowerCase());
    }
}
