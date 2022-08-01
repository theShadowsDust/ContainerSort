package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.util.InventoryUtil;
import de.uniquegame.containersort.api.util.SortType;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerInteractListener implements Listener {

    private final ContainerSortApi containerSortApi;

    public PlayerInteractListener(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {

        if (event.useInteractedBlock() == Event.Result.DENY) return;
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;
        Player player = event.getPlayer();

        if (clickedBlock.getState() instanceof Sign sign) {

            if (!this.containerSortApi.isSortSign(sign)) return;
            BlockData blockData = sign.getBlockData();

            if (blockData instanceof Directional directional) {

                Block attachedBlock = sign.getBlock().getRelative(directional.getFacing().getOppositeFace());
                if (attachedBlock.getState() instanceof Container container) {

                    if (!this.containerSortApi.isValidContainer(container)) return;

                    if (!player.hasPermission("container.sort.allow")) return;
                    if (this.containerSortApi.getSettings().isWorldDisabled(clickedBlock.getWorld())) return;

                    if (!this.containerSortApi.isSignOwner(player.getUniqueId(), sign) &&
                            !player.hasPermission("containersort.sort.admin")) {

                        player.sendMessage(this.containerSortApi.getLanguageService().getMessage(
                                "invalid-sign-owner", player,
                                this.containerSortApi.getLanguageService().prefix()));
                        return;
                    }

                    SortType sortType = SortType.findSortType(sign.lines());
                    if (sortType == null) {
                        player.sendMessage(this.containerSortApi.getLanguageService().getMessage(
                                "cannot-find-sort-type",
                                player, this.containerSortApi.getLanguageService().prefix()));
                        return;
                    }

                    InventoryUtil.sortContainer(sortType, container.getInventory());
                    player.playSound(player.getLocation(),
                            this.containerSortApi.getSettings().getSortSuccessSound(), 1F, 1F);
                }
            }
        }
    }
}
