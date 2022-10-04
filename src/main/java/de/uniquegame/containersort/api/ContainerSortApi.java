package de.uniquegame.containersort.api;

import de.uniquegame.containersort.ContainerSortPlugin;
import de.uniquegame.containersort.configuration.ContainerSortConfiguration;
import de.uniquegame.containersort.configuration.ContainerSortProperty;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.service.PersistentDataStoreService;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public interface ContainerSortApi {

    boolean isSortSign(@NotNull Sign sign);

    /**
     * @param playerId the player who interacts with the attached {@link org.bukkit.block.Sign}
     * @return true if the {@link Player} can sort the {@link org.bukkit.block.Container}
     */
    boolean isSignOwner(@NotNull UUID playerId, @NotNull Sign sign);

    /**
     * @return the {@link ContainerSortPlugin}
     */
    ContainerSortPlugin getPlugin();

    PersistentDataStoreService getDataStoreService();

    LanguageService getLanguageService();

    ContainerSortConfiguration getConfiguration();

    void reloadConfig() throws IOException;

    <T> T getPropertyValue(@NotNull ContainerSortProperty<T> property, @NotNull Class<T> target);

    void saveSignData(@NotNull UUID playerId,
                      @NotNull String playerName,
                      @NotNull SortType sortType,
                      @NotNull SignChangeEvent event);

    boolean isValidContainer(@NotNull BlockState state);


    void sortContainer(@NotNull SortType sortType, @NotNull Inventory inventory);
}
