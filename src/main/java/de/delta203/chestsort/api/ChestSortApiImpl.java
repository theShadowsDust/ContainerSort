package de.delta203.chestsort.api;

import de.delta203.chestsort.ChestSortPlugin;
import de.delta203.chestsort.configuration.ChestSortPluginConfiguration;
import de.delta203.chestsort.enums.SoundType;
import de.delta203.chestsort.inventory.SortChestInventory;
import de.delta203.chestsort.registry.ItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChestSortApiImpl implements ChestSortApi {

    private final ChestSortPlugin plugin;
    private final ChestSortPluginConfiguration configuration;
    private final ItemRegistry itemRegistry;
    private final SortChestInventory sortChestInventory;

    public ChestSortApiImpl(@NotNull ChestSortPlugin plugin) {
        this.plugin = plugin;
        this.configuration = new ChestSortPluginConfiguration(this);
        this.itemRegistry = new ItemRegistry(this);
        this.sortChestInventory = new SortChestInventory(this);
    }

    @Override
    public boolean isInSortingProgress(Player player) {
        return this.sortChestInventory.getPlayerChestLocations().containsKey(player.getUniqueId());
    }

    @Override
    public boolean isContainerLocked(Location location) {
        return this.sortChestInventory.getPlayerChestLocations().containsValue(location);
    }

    @Override
    public @Nullable Location getContainerLocation(Player player) {
        return this.sortChestInventory.getPlayerChestLocations().get(player.getUniqueId());
    }

    @Override
    public void removePlayerContainerLocation(Player player) {
        this.sortChestInventory.getPlayerChestLocations().remove(player.getUniqueId());
    }

    @Override
    public ChestSortPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public ChestSortPluginConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return this.itemRegistry;
    }

    @Override
    public SortChestInventory getSortChestInventory() {
        return this.sortChestInventory;
    }

    @Override
    public String stripColors(@NotNull Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
    }

    @Override
    public Component translateLegacyColorCodes(@NotNull String text) {
        return MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().
                serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text)));
    }

    @Override
    public void playSound(Player player, SoundType soundType) {
        this.getConfiguration().playSound(player, soundType);
    }
}
