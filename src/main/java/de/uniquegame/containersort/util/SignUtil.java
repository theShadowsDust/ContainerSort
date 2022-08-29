package de.uniquegame.containersort.util;

import de.uniquegame.containersort.api.ContainerSortApiImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public final class SignUtil {

    private static final BlockFace[] DOUBLE_CHEST_FACES = {
            BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST,
            BlockFace.EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST,
            BlockFace.WEST};

    private static final BlockFace[] BLOCK_FACES = {
            BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST,
            BlockFace.EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST,
            BlockFace.WEST};

    private SignUtil() {
        throw new IllegalStateException("Utility class");
    }

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
            Bukkit.getLogger().log(Level.SEVERE, "[ContainerSort] Cannot find player id", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Nullable
    public static Sign findConnectedSign(@NotNull Container container) {

        Sign sign = null;
        Block block = container.getBlock();

        boolean doubleChest = container.getInventory().getHolder() instanceof DoubleChest;
        BlockFace[] faces = doubleChest ? DOUBLE_CHEST_FACES : BLOCK_FACES;
        for (int i = 0; i < faces.length && sign == null; i++) {
            var currentBlock = block.getRelative(faces[i]);
            if (currentBlock.getState() instanceof Sign wallSign) {
                sign = wallSign;
            }
        }

        return sign;
    }

    @Nullable
    public static Container findConnectedContainer(@NotNull Sign sign) {
        var blockData = sign.getBlockData();
        Block block = null;
        if (blockData instanceof WallSign wallSign) {
            block = sign.getBlock().getRelative(wallSign.getFacing().getOppositeFace());
        }

        return block != null && block.getState() instanceof Container container ? container : null;
    }

    @Nullable
    public static String findPlayerName(@NotNull List<Component> lines) {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String playerName = null;
            for (int i = 0; i < lines.size() && playerName == null; i++) {
                var line = MessageUtil.stripColors(lines.get(i));
                var offlinePlayer = Bukkit.getOfflinePlayerIfCached(line);
                if (offlinePlayer == null || offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(line))
                    continue;
                playerName = offlinePlayer.getName();
            }

            return playerName;
        }, ContainerSortApiImpl.EXECUTOR_SERVICE);


        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Bukkit.getLogger().log(Level.SEVERE, "[ContainerSort] Cannot find player name", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
