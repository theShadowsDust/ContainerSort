package de.theshadowsdust.containersort.util;

import de.theshadowsdust.containersort.api.ContainerSortApiImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public final class SignUtil {

    private static final BlockFace[] BLOCK_FACES = BlockFace.values();

    private SignUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param container the container
     * @return the connected {@link Sign} with the {@link Container}
     */
    @Nullable
    public static Sign findConnectedSign(@NotNull Container container) {

        Sign sign = null;
        Block block = container.getBlock();
        for (int i = 0; i < BLOCK_FACES.length && sign == null; i++) {
            var currentBlock = block.getRelative(BLOCK_FACES[i]);
            if (currentBlock.getState() instanceof Sign wallSign) {
                sign = wallSign;
            }
        }

        return sign;
    }

    /**
     *
     * @param sign the sign
     * @return the connected {@link Container} with the {@link Sign}
     */
    @Nullable
    public static Container findConnectedContainer(@NotNull Sign sign) {
        var blockData = sign.getBlockData();
        Block block = null;
        if (blockData instanceof WallSign wallSign) {
            block = sign.getBlock().getRelative(wallSign.getFacing().getOppositeFace());
        }

        return block != null && block.getState() instanceof Container container ? container : null;
    }

    /**
     *
     * @param lines the lines of the sign
     * @return the name of the owner from the sign
     */
    @Nullable
    public static String findPlayerName(@NotNull List<Component> lines) {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String playerName = null;
            for (int i = 0; i < lines.size() && playerName == null; i++) {
                var line = MessageUtil.stripColors(lines.get(i));
                if (!line.isEmpty()) {
                    var offlinePlayer = Bukkit.getOfflinePlayer(line);
                    if (offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(line))
                        continue;
                    playerName = offlinePlayer.getName();
                }
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
