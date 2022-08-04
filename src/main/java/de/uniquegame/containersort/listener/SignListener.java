package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.SortType;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.util.MessageUtil;
import de.uniquegame.containersort.util.Permissions;
import de.uniquegame.containersort.util.SignUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SignListener implements Listener {

    private final ContainerSortApi containerSortApi;
    private final LanguageService languageService;

    public SignListener(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        this.languageService = containerSortApi.getLanguageService();
    }

    @EventHandler
    public void handleSignBreak(BlockBreakEvent event) {

        if (!this.containerSortApi.getSettings().signsProtected()) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getState() instanceof Sign sign) {
            if (this.containerSortApi.isSortSign(sign)) {
                if (!this.containerSortApi.isSignOwner(player.getUniqueId(), sign)) {
                    event.setCancelled(!player.hasPermission(Permissions.PERMISSION_SORT_BREAK_OTHERS));
                }
            }
        }
    }

    @EventHandler
    public void handleSignChange(SignChangeEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();

        Component componentLine = event.line(0);
        if (componentLine == null) return;

        String line = MessageUtil.stripColors(componentLine);
        String prefix = this.containerSortApi.getLanguageService().prefix();

        if (line.equalsIgnoreCase("[containersort]")) {

            if (this.containerSortApi.getSettings().isWorldDisabled(event.getBlock().getWorld())) return;
            if (!player.hasPermission(Permissions.PERMISSION_SORT_CREATE)) return;

            BlockState blockState = block.getState();

            if (blockState instanceof Sign sign) {
                Container container = SignUtil.findConnectedContainer(sign);
                if (container == null && !this.containerSortApi.isValidContainer(blockState)) {
                    SignUtil.breakSign(player, this.languageService.getMessage(
                                    "no-connected-container-found",
                                    player,
                                    prefix),
                            sign);
                    return;
                }

                SortType sortType = SortType.findSortType(event.lines());
                if (sortType == null) {
                    SignUtil.breakSign(player, this.languageService.getMessage(
                                    "cannot-find-sort-type",
                                    player,
                                    prefix),
                            sign);
                    return;
                }

                String playerName = SignUtil.findPlayerName(event.lines());
                if (playerName == null) {
                    playerName = player.getName();
                }

                UUID signOwnerId = Bukkit.getPlayerUniqueId(playerName);
                if (signOwnerId == null) {
                    SignUtil.breakSign(player, this.languageService.getMessage(
                                    "cannot-find-username",
                                    player, prefix),
                            sign);
                    return;
                }
                if (!signOwnerId.equals(player.getUniqueId()) &&
                        !player.hasPermission(Permissions.PERMISSION_SORT_CREATE_OTHERS)) {
                    signOwnerId = player.getUniqueId();
                }

                List<Component> signLayout = this.containerSortApi.
                        getLanguageService().getSignLayout(playerName, sortType);

                for (int i = 0; i < signLayout.size(); i++) {
                    event.line(i, signLayout.get(i));
                }

                this.containerSortApi.saveSignData(signOwnerId, sign);
                player.sendMessage(this.containerSortApi.getLanguageService().
                        getMessage("sign-successfully-created", player,
                                this.containerSortApi.getLanguageService().prefix()));
            }
        }
    }
}
