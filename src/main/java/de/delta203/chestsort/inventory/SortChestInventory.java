package de.delta203.chestsort.inventory;

import de.delta203.chestsort.api.ChestSortApi;
import de.delta203.chestsort.api.ChestSortItem;
import de.delta203.chestsort.api.util.SortType;
import de.delta203.chestsort.api.util.SoundType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SortChestInventory {

    private final ChestSortApi chestSortApi;
    private final Map<UUID, Inventory> playerInventories;
    private final Map<UUID, Location> playerChestLocations;

    public SortChestInventory(@NotNull ChestSortApi chestSortApi) {
        this.chestSortApi = chestSortApi;
        this.playerInventories = new HashMap<>();
        this.playerChestLocations = new HashMap<>();
    }

    @NotNull
    public Map<UUID, Location> getPlayerChestLocations() {
        return playerChestLocations;
    }

    public void setPlayerChestLocation(@NotNull Player player, @NotNull Location location) {
        if (!this.playerChestLocations.containsKey(player.getUniqueId())) {
            this.playerChestLocations.put(player.getUniqueId(), location);
        }
    }

    @Nullable
    public Inventory getInventory(@NotNull Player player) {
        return this.playerInventories.get(player.getUniqueId());
    }

    public void removePlayerInventory(@NotNull Player player) {
        this.playerInventories.remove(player.getUniqueId());
    }

    public void setupInventory(@NotNull Player player) {

        String inventoryName = this.chestSortApi.getConfiguration().getConfig().getString("gui.name", "");
        if (!this.playerInventories.containsKey(player.getUniqueId())) {
            Inventory playerInventory = this.chestSortApi.getPlugin().getServer().createInventory(
                    null, 9, this.chestSortApi.translateLegacyColorCodes(inventoryName));

            ChestSortItem chestSortItem = this.chestSortApi.getItemRegistry().getSortItem(SortType.PLACEHOLDER);
            if (chestSortItem != null) {
                for (int i = 0; i < playerInventory.getSize(); i++) {
                    if (playerInventory.getItem(i) == null) {
                        playerInventory.setItem(i, chestSortItem.toItemStack());
                    }
                }
            }

            for (ChestSortItem sortItem : this.chestSortApi.getItemRegistry().getItems()) {
                if (sortItem.getSortType() == SortType.PLACEHOLDER) continue;
                playerInventory.setItem(sortItem.getSlot(), sortItem.toItemStack());
            }

            this.playerInventories.put(player.getUniqueId(), playerInventory);
        }
    }

    public void open(@NotNull Player player, @NotNull Location location, Block container) {

        if (this.playerInventories.containsKey(player.getUniqueId())) {

            Inventory playerInventory = this.playerInventories.get(player.getUniqueId());
            FileConfiguration config = this.chestSortApi.getConfiguration().getConfig();

            List<String> chestLore = new ArrayList<>();
            for (String text : config.getStringList("gui.chest.lore")) {
                chestLore.add(text.replace("%x%", String.valueOf(location.getBlockX())).
                        replace("%y%", String.valueOf(location.getBlockY())).
                        replace("%z%", String.valueOf(location.getBlockZ())));
            }

            playerInventory.setItem(config.getInt("gui.chest.slot"),
                    this.chestSortApi.getItemRegistry().
                            translateItemDisplayName(this.chestSortApi.getItemRegistry().
                                    buildItemStack(container.getType(), "", chestLore)));

            player.openInventory(playerInventory);
            setPlayerChestLocation(player, location);
            this.chestSortApi.playSound(player, SoundType.OPEN_GUI);
        }
    }
}
