package de.uniquegame.containersort.service;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.util.SortType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageService {

    private final ContainerSortApi containerSortApi;
    private final YamlConfiguration config;

    public LanguageService(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        File file = new File(containerSortApi.getPlugin().getDataFolder(), "language.yml");
        if (!file.exists()) {
            containerSortApi.getPlugin().saveResource(file.getName(), false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String prefix() {
        return this.config.getString("messages.prefix");
    }

    @NotNull
    public Component getMessage(@NotNull String key, @NotNull Player player, Object... placeholders) {

        String path = String.format("messages.%s.%s", player.locale(), key);
        if (!this.config.isSet(path)) {
            path = String.format("messages.%s.%s", Locale.ENGLISH, key);
        }

        return translateLegacyColorCodes(MessageFormat.format(
                this.config.getString(path, String.format("N/A (%s)", path)), placeholders));
    }

    /**
     * @param text the text
     * @return the plain text without colors
     */
    public String stripColors(@NotNull Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
    }

    /**
     * Translates a {@link String} that contains the legacy color codes
     *
     * @param text the text to translate
     * @return the translated {@link Component}
     */
    public Component translateLegacyColorCodes(@NotNull String text) {
        return MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().
                serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text)));
    }

    @NotNull
    public List<Component> getSignLayout(@NotNull String playerName, @NotNull SortType sortType) {
        List<Component> layout = new ArrayList<>();
        for (String line : this.containerSortApi.getSettings().getSignLayout()) {
            layout.add(translateLegacyColorCodes(line.
                    replace("%sign_owner_name%", playerName).
                    replace("%container_sort_type%", sortType.getDisplayName())));
        }

        return layout;
    }
}
