package de.uniquegame.containersort.api.util;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SignUtil {

    @Nullable
    public static UUID findPlayerId(@NotNull Sign sign) {

        UUID playerId = null;
        for (int i = 0; i < sign.lines().size() && playerId == null; i++) {
            playerId = Bukkit.getPlayerUniqueId(PlainTextComponentSerializer.plainText().serialize(sign.line(i)));
        }

        return playerId;
    }
}
