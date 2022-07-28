package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.util.SortType;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class SignListener implements Listener {

    private final ContainerSortApi containerSortApi;

    public SignListener(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
    }

    @EventHandler
    public void handleSignBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Sign sign) {
            if (this.containerSortApi.isSortSign(sign)) {
                event.setCancelled(!this.containerSortApi.isSignOwner(event.getPlayer(), sign));
            }
        }
    }

    @EventHandler
    public void handleSignChange(SignChangeEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (this.containerSortApi.getSettings().isWorldDisabled(event.getBlock().getWorld())) return;
        if (!player.hasPermission("containersort.container.create")) return;

        Component componentLine = event.line(0);
        if (componentLine == null) return;

        Component sortTypeLineComp = event.line(1);
        if (sortTypeLineComp == null) return;

        String line = this.containerSortApi.getLanguageService().stripColors(componentLine);
        String sortTypeLine = this.containerSortApi.getLanguageService().stripColors(sortTypeLineComp);

        if (line.equalsIgnoreCase("[containersort]")) {

            SortType sortType = SortType.getSortType(sortTypeLine);
            if (sortType == null) {
                player.sendMessage(this.containerSortApi.getLanguageService().getMessage(
                        "cannot-find-sort-type",
                        player,
                        sortTypeLine));
                return;
            }

            if (block.getState() instanceof Sign sign) {
                this.containerSortApi.saveSignData(player, sign, sortType);
                player.sendMessage(this.containerSortApi.getLanguageService().getMessage("sign-successfully-created", player));
            }
        }
    }
}
