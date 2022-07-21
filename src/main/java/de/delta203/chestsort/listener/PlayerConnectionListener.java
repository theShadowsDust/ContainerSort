package de.delta203.chestsort.listener;

import de.delta203.chestsort.api.ChestSortApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerConnectionListener implements Listener {

    private final ChestSortApi chestSortApi;

    public PlayerConnectionListener(@NotNull ChestSortApi chestSortApi) {
        this.chestSortApi = chestSortApi;
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        this.chestSortApi.getSortChestInventory().setupInventory(event.getPlayer());
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        this.chestSortApi.removePlayerContainerLocation(event.getPlayer());
        this.chestSortApi.getSortChestInventory().removePlayerInventory(event.getPlayer());
    }
}
