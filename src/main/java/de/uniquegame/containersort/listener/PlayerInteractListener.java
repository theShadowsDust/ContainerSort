package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.util.InventoryUtil;
import de.uniquegame.containersort.api.util.SignUtil;
import de.uniquegame.containersort.api.util.SortType;
import de.uniquegame.containersort.service.LanguageService;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerInteractListener implements Listener {

    private final ContainerSortApi containerSortApi;
    private final LanguageService languageService;

    public PlayerInteractListener(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        this.languageService = containerSortApi.getLanguageService();
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {

        if (!event.getAction().isRightClick()) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        var clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;
        var player = event.getPlayer();

        if (clickedBlock.getState() instanceof Sign sign) {

            if (!this.containerSortApi.isSortSign(sign)) return;
            var prefix = this.containerSortApi.getLanguageService().prefix();

            var container = SignUtil.findConnectedContainer(sign);
            if (container == null) {

                SignUtil.breakSign(player, this.languageService.getMessage(
                        "no-connected-container-found",
                        player, prefix),
                        sign);

                return;
            }

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

            var sortType = SortType.findSortType(sign.lines());
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
