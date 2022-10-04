package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.SortType;
import de.uniquegame.containersort.configuration.ContainerSortProperty;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.util.Permissions;
import de.uniquegame.containersort.util.SignUtil;
import org.bukkit.Sound;
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
    @SuppressWarnings("java:S3776")
    public void handlePlayerInteract(PlayerInteractEvent event) {

        if (!event.getAction().isRightClick()) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        var clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;
        var player = event.getPlayer();

        if (clickedBlock.getState() instanceof Sign sign) {

            if (!this.containerSortApi.isSortSign(sign)) return;
            var pluginPrefix = this.languageService.pluginPrefix();

            var container = SignUtil.findConnectedContainer(sign);
            if (container == null || !this.containerSortApi.isValidContainer(container.getBlock().getState())) {
                player.sendMessage(this.languageService.getMessage("no-connected-container-found", player, pluginPrefix));
                return;
            }

            if (!player.hasPermission(Permissions.PERMISSION_SORT_ALLOW)) return;
            if (this.containerSortApi.getConfiguration().isWorldDisabled(clickedBlock.getWorld())) return;

            if (!this.containerSortApi.isSignOwner(player.getUniqueId(), sign) &&
                    !player.hasPermission(Permissions.PERMISSION_SORT_ALLOW_OTHERS)) {

                player.sendMessage(this.languageService.getMessage("invalid-sign-owner", player, pluginPrefix));
                return;
            }

            var sortType = SortType.findSortType(sign.lines());
            if (sortType == null) {
                player.sendMessage(this.languageService.getMessage("cannot-find-sort-type", player, pluginPrefix));
                return;
            }

            this.containerSortApi.sortContainer(sortType, container.getInventory());
            player.playSound(
                    player.getLocation(),
                    Sound.valueOf(this.containerSortApi.getPropertyValue(ContainerSortProperty.SORT_SUCCESS_SOUND, String.class)),
                    1F, 1F);
        }
    }
}
