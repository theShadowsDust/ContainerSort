package de.theshadowsdust.containersort.listener;

import de.theshadowsdust.containersort.api.ContainerSortApi;
import de.theshadowsdust.containersort.api.SortType;
import de.theshadowsdust.containersort.configuration.ContainerSortProperty;
import de.theshadowsdust.containersort.service.LanguageService;
import de.theshadowsdust.containersort.util.MessageUtil;
import de.theshadowsdust.containersort.util.Permissions;
import de.theshadowsdust.containersort.util.SignUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class SignListener implements Listener {

    private final ContainerSortApi containerSortApi;
    private final LanguageService languageService;
    private final String pluginPrefix;

    public SignListener(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        this.languageService = containerSortApi.getLanguageService();
        this.pluginPrefix = languageService.pluginPrefix();
    }

    @EventHandler
    @SuppressWarnings("java:S3776")
    public void handleSignChange(SignChangeEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (!player.hasPermission(Permissions.PERMISSION_SORT_CREATE)) return;

        String line = MessageUtil.stripColors(event.line(0));
        if (!line.equalsIgnoreCase(this.containerSortApi.getPropertyValue(ContainerSortProperty.SIGN_IDENTIFIER, String.class))) return;
        if (this.containerSortApi.getConfiguration().isWorldDisabled(event.getBlock().getWorld())) return;
        BlockState blockState = block.getState();

        if (blockState instanceof Sign sign) {

            Container container = SignUtil.findConnectedContainer(sign);
            boolean cancel = false;

            if (container == null && !this.containerSortApi.isValidContainer(blockState)) {
                player.sendMessage(this.languageService.getMessage("no-connected-container-found", player, this.pluginPrefix));
                cancel = true;
            }

            SortType sortType = SortType.findSortType(event.lines());
            if (sortType == null) {
                player.sendMessage(this.languageService.getMessage("cannot-find-sort-type", player, this.pluginPrefix));
                cancel = true;
            }

            String playerName = SignUtil.findPlayerName(event.lines());
            if (playerName == null) {
                playerName = player.getName();
            }

            UUID signOwnerId = Bukkit.getPlayerUniqueId(playerName);
            if (signOwnerId == null) {
                player.sendMessage(this.languageService.getMessage("cannot-find-username", player, this.pluginPrefix));
                cancel = true;
            }

            if (signOwnerId != null &&
                    !signOwnerId.equals(player.getUniqueId()) &&
                    !player.hasPermission(Permissions.PERMISSION_SORT_CREATE_OTHERS)) {
                player.sendMessage(this.languageService.getMessage("invalid-sign-owner", player, this.pluginPrefix));
                event.setCancelled(true);
                cancel = true;
            }

            if (!cancel) {
                this.containerSortApi.saveSignData(signOwnerId, playerName, sortType, sign);

                List<Component> lines = this.containerSortApi.getLanguageService().getSignLayout(playerName, sortType);
                for (int i = 0; i < lines.size(); i++) {
                    event.line(i, lines.get(i));
                }

                player.sendMessage(this.languageService.getMessage("sign-successfully-created", player, this.pluginPrefix));
            } else {
                event.setCancelled(true);
            }
        }
    }
}
