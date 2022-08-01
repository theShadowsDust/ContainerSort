package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.util.SignUtil;
import de.uniquegame.containersort.api.util.SortType;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

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
                event.setCancelled(!this.containerSortApi.isSignOwner(event.getPlayer().getUniqueId(), sign));
            }
        }
    }

    @EventHandler
    public void handleSignChange(SignChangeEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();

        Component componentLine = event.line(0);
        if (componentLine == null) return;

        String line = this.containerSortApi.getLanguageService().stripColors(componentLine);

        if (line.equalsIgnoreCase("[containersort]")) {

            if (this.containerSortApi.getSettings().isWorldDisabled(event.getBlock().getWorld())) return;
            if (!player.hasPermission("containersort.container.create")) return;


            if (block.getState() instanceof Sign sign) {

                Container container = findConnectedContainer(sign, sign.getBlockData());
                if (container == null) {
                    player.sendMessage(this.containerSortApi.getLanguageService().
                            getMessage("no-connected-container-found", player,
                                    this.containerSortApi.getLanguageService().prefix()));
                    block.breakNaturally(true);
                    return;
                }

                SortType sortType = SortType.findSortType(event.lines());
                if (sortType == null) {
                    player.sendMessage(this.containerSortApi.getLanguageService().getMessage(
                            "cannot-find-sort-type", player,
                            this.containerSortApi.getLanguageService().prefix()));
                    return;
                }

                UUID signOwnerId = SignUtil.findPlayerId(sign);
                if (signOwnerId != null) {
                    if (!signOwnerId.equals(player.getUniqueId()) /*&& !player.hasPermission("container.container.create.others")*/) {
                        player.sendMessage(this.containerSortApi.getLanguageService().getMessage("invalid-player-name",
                                player, this.containerSortApi.getLanguageService().prefix()));
                        return;
                    }
                }

                List<Component> signLayout = this.containerSortApi.getLanguageService().
                        getSignLayout(player.getName(), sortType);

                for (int i = 0; i < signLayout.size(); i++) {
                    event.line(i, signLayout.get(i));
                }

                this.containerSortApi.saveSignData(player, sign);
                player.sendMessage(this.containerSortApi.getLanguageService().
                        getMessage("sign-successfully-created", player,
                                this.containerSortApi.getLanguageService().prefix()));
            }
        }
    }

    @Nullable
    private Container findConnectedContainer(@NotNull Sign sign, @NotNull BlockData blockData) {

        BlockFace blockFace = BlockFace.SELF;
        if (blockData instanceof Rotatable rotatable) blockFace = rotatable.getRotation();
        if (blockData instanceof WallSign wallSign) blockFace = wallSign.getFacing();

        Block block = sign.getBlock().getRelative(blockFace.getOppositeFace());
        if (!(block.getState() instanceof Container container)) return null;

        return container;
    }
}
