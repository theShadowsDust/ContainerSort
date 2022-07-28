package de.uniquegame.containersort.api;

import de.uniquegame.containersort.ContainerSortPlugin;
import de.uniquegame.containersort.api.util.SortType;
import de.uniquegame.containersort.configuration.ContainerSortPluginConfiguration;
import de.uniquegame.containersort.configuration.ContainerSortSettings;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.service.PersistentDataStoreService;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ContainerSortApi {

    boolean isSortSign(Sign sign);

    /**
     * @param player the player who interacts with the attached {@link org.bukkit.block.Sign}
     * @return true if the {@link Player} can sort the {@link org.bukkit.block.Container}
     */
    boolean isSignOwner(@NotNull Player player, @NotNull Sign sign);

    /**
     * @return the {@link ContainerSortPlugin}
     */
    ContainerSortPlugin getPlugin();

    ContainerSortPluginConfiguration getConfiguration();

    PersistentDataStoreService getDataStoreService();

    LanguageService getLanguageService();

    ContainerSortSettings getSettings();

    void saveSignData(@NotNull Player player, @NotNull Sign sign, @NotNull SortType sortType);
}
