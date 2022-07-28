package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.util.InventoryUtil;
import de.uniquegame.containersort.api.util.SortType;
import org.bukkit.block.*;
import org.bukkit.block.data.Rotatable;
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

            if(!this.containerSortApi.isSortSign(sign)) return;
            if(!this.containerSortApi.isSignOwner(player, sign)) {
                player.sendMessage(this.containerSortApi.getLanguageService().getMessage("invalid-sign-owner", player));
                return;
            }

            if (sign.getBlockData() instanceof Rotatable rotatable) {
                Block attachedBlock = clickedBlock.getRelative(rotatable.getRotation());
                if (attachedBlock.getState() instanceof Container container) {

                    if (!isValidContainer(container)) return;
                    if (!player.hasPermission("container.sort.allow")) return;
                    if (this.containerSortApi.getSettings().isWorldDisabled(clickedBlock.getWorld())) return;


                    String sortTypeName = this.containerSortApi.getLanguageService().stripColors(sign.line(1));
                    SortType sortType = SortType.getSortType(sortTypeName);

                    if (sortType == null) {
                        player.sendMessage(this.containerSortApi.getLanguageService().getMessage(
                                "cannot-find-sort-type",
                                player,
                                sortTypeName));
                        return;
                    }

                    InventoryUtil.sortContainer(sortType, container.getInventory());
                    player.playSound(player.getLocation(),
                            this.containerSortApi.getSettings().getSortSuccessSound(), 1F, 1F);
                }
            }
        }
    }

    private boolean isValidContainer(@NotNull Container container) {
        return container instanceof Chest || container instanceof Barrel || container instanceof DoubleChest;
    }
}
