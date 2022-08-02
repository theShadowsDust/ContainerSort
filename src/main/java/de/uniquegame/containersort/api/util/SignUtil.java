package de.uniquegame.containersort.api.util;

import de.uniquegame.containersort.api.ContainerSortApiImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SignUtil {

    @Nullable
    public static UUID findPlayerId(@NotNull Sign sign) {

        CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> {
            UUID playerId = null;
            for (int i = 0; i < sign.lines().size() && playerId == null; i++) {
                UUID uuid = Bukkit.getPlayerUniqueId(PlainTextComponentSerializer.plainText().serialize(sign.line(i)));
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

    @Nullable
    public static String findPlayerName(@NotNull List<Component> lines) {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String playerName = null;
            for (int i = 0; i < lines.size() && playerName == null; i++) {

                String line = PlainTextComponentSerializer.plainText().serialize(lines.get(i));

                if (line.isEmpty()) continue;

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(line);
                if (!offlinePlayer.hasPlayedBefore()) continue;

                String name = offlinePlayer.getName();
                if (name == null) continue;
                if (!name.equalsIgnoreCase(line)) continue;
                playerName = name;
            }

            return playerName;
        }, ContainerSortApiImpl.EXECUTOR_SERVICE);


        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
