package de.uniquegame.containersort.api;

import de.uniquegame.containersort.ContainerSortPlugin;
import de.uniquegame.containersort.configuration.ContainerSortConfiguration;
import de.uniquegame.containersort.configuration.ContainerSortProperty;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.service.PersistentDataStoreService;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public interface ContainerSortApi {

    /**
     *
     * @param sign the sign
     * @return true if the given {@link Sign} is a sort sign.
     */
    boolean isSortSign(@NotNull Sign sign);

    /**
     * @param playerId the player who interacts with the attached {@link org.bukkit.block.Sign}
     * @return true if the {@link Player} can sort the {@link org.bukkit.block.Container}
     */
    boolean isSignOwner(@NotNull UUID playerId, @NotNull Sign sign);

    ContainerSortPlugin getPlugin();

    PersistentDataStoreService getDataStoreService();

    LanguageService getLanguageService();

    ContainerSortConfiguration getConfiguration();

    void reloadConfig() throws IOException;

    /**
     *
     * @param property the property
     * @param target the target value class
     * @return the value of the given property
     * @param <T> the target
     */
    <T> T getPropertyValue(@NotNull ContainerSortProperty<T> property, @NotNull Class<T> target);

    /**
     *
     * @param playerId the player that owns the {@link Sign}
     * @param playerName the player name
     * @param sortType the sort type defines how the container will be sorted
     * @param sign the sign who the data will be stored
     */
    void saveSignData(@NotNull UUID playerId,
                      @NotNull String playerName,
                      @NotNull SortType sortType,
                      @NotNull Sign sign);

    /**
     *
     * @param state the blockState
     * @return true if the blockState is a valid container
     */
    boolean isValidContainer(@NotNull BlockState state);


    /**
     *
     * @param sortType the sort type defines how the container will be sorted
     * @param inventory the inventory to sort
     */
    void sortContainer(@NotNull SortType sortType, @NotNull Inventory inventory);
}
