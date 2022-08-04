package de.uniquegame.containersort.util;

import de.uniquegame.containersort.api.ContainerSortApiImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class SignUtil {

    @Nullable
    public static UUID findPlayerId(@NotNull Sign sign) {

        CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> {
            UUID playerId = null;
            for (int i = 0; i < sign.lines().size() && playerId == null; i++) {
                var uuid = Bukkit.getPlayerUniqueId(MessageUtil.stripColors(sign.line(i)));
                if (uuid == null) continue;
                playerId = uuid;
            }

            return playerId;
        }, ContainerSortApiImpl.EXECUTOR_SERVICE);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void breakSign(@NotNull Player player, @NotNull Component message, @NotNull Sign sign) {
        player.sendMessage(message);
        sign.getBlock().breakNaturally(true);
    }

    @Nullable
    public static Container findConnectedContainer(@NotNull Sign sign) {
        var blockData = sign.getBlockData();
        Block block = null;

        if (blockData instanceof Rotatable) {
            block = sign.getBlock().getRelative(BlockFace.DOWN);
        }

        if (blockData instanceof WallSign wallSign) {
            block = sign.getBlock().getRelative(wallSign.getFacing().getOppositeFace());
        }

        return block.getState() instanceof Container container ? container : null;
    }

    @Nullable
    public static String findPlayerName(@NotNull List<Component> lines) {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String playerName = null;
            for (int i = 0; i < lines.size() && playerName == null; i++) {

                var line = MessageUtil.stripColors(lines.get(i));

                if (line.isEmpty()) continue;

                var offlinePlayer = Bukkit.getOfflinePlayer(line);
                if (!offlinePlayer.hasPlayedBefore()) continue;

                var name = offlinePlayer.getName();
                if (name == null) continue;
                if (!name.equalsIgnoreCase(line)) continue;
                playerName = name;
            }

            return playerName;
        }, ContainerSortApiImpl.EXECUTOR_SERVICE);


        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignored) {
            return null;
        }
    }

}
