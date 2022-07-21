package de.delta203.chestsort.api;

import de.delta203.chestsort.ChestSortPlugin;
import de.delta203.chestsort.api.util.SoundType;
import de.delta203.chestsort.configuration.ChestSortPluginConfiguration;
import de.delta203.chestsort.inventory.SortChestInventory;
import de.delta203.chestsort.registry.ItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChestSortApi {

    /**
     * @param player the player who sort the {@link org.bukkit.block.Container}
     * @return true if the player has the sort inventory open
     */
    boolean isInSortingProgress(Player player);

    /**
     * @param location the location of the {@link org.bukkit.block.Container}
     * @return true if the {@link org.bukkit.block.Container} is locked to put items in the inventory.
     */
    boolean isContainerLocked(Location location);

    /**
     *
     * @param player the player who sorting the chest
     * @return the current {@link Location} of the {@link org.bukkit.block.Container}
     */
    @Nullable
    Location getContainerLocation(Player player);

    /**
     * Remove the current {@link org.bukkit.block.Container} location
     * @param player the player who sorted the {@link org.bukkit.block.Container}
     */
    void removePlayerContainerLocation(Player player);

    /**
     * @return the {@link ChestSortPlugin}
     */
    ChestSortPlugin getPlugin();

    ChestSortPluginConfiguration getConfiguration();

    ItemRegistry getItemRegistry();

    SortChestInventory getSortChestInventory();

    /**
     *
     * @param text the text
     * @return the plain text without colors
     */
    String stripColors(@NotNull Component text);

    /**
     * Translates a {@link String} that contains the legacy color codes
     * @param text the text to translate
     * @return the translated {@link Component}
     */
    Component translateLegacyColorCodes(@NotNull String text);

    void playSound(Player player, SoundType soundType);
}
