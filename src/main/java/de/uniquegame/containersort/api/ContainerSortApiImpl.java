package de.uniquegame.containersort.api;

import de.uniquegame.containersort.ContainerSortPlugin;
import de.uniquegame.containersort.configuration.ContainerSortPluginConfiguration;
import de.uniquegame.containersort.configuration.ContainerSortSettings;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.service.PersistentDataStoreService;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ContainerSortApiImpl implements ContainerSortApi {

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
    public boolean isSortSign(Sign sign) {

        PersistentDataContainer container = sign.getPersistentDataContainer();
        NamespacedKey sortSign = this.persistentDataStoreService.getNameSpacedKey(PersistentDataStoreService.SORT_SIGN);

        if (sortSign == null) return false;
        if (!container.has(sortSign)) return false;

        Byte signValue = container.get(sortSign, PersistentDataType.BYTE);
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
    public ContainerSortPluginConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ContainerSortSettings getSettings() {
        return this.configuration.getSettings();
    }

    @Override
    public void saveSignData(@NotNull Player player, @NotNull Sign sign) {

        this.getDataStoreService().applyPersistentData(
                sign,
                PersistentDataStoreService.SORT_SIGN_OWNER,
                PersistentDataStoreService.UUID_TAG_TYPE, player.getUniqueId());

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
        return container instanceof Chest || container instanceof Barrel || container instanceof DoubleChest;
    }
}
