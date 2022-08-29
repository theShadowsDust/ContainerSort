package de.uniquegame.containersort.service;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.SortType;
import de.uniquegame.containersort.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public class LanguageService {

    private final ContainerSortApi containerSortApi;
    private final File file;
    private final YamlConfiguration config;
    private final ResourceBundle defaultMessages;

    public LanguageService(@NotNull ContainerSortApi containerSortApi) {

        this.containerSortApi = containerSortApi;
        this.defaultMessages = ResourceBundle.getBundle("containersort", new UTF8ResourceBundleControl());
        this.file = new File(containerSortApi.getPlugin().getDataFolder(), "language.yml");
        if (!file.exists()) {
            containerSortApi.getPlugin().saveResource(file.getName(), false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        createLanguage(Locale.US);
    }

    public String pluginPrefix() {
        return this.config.getString("messages.prefix");
    }

    @NotNull
    public Component getMessage(@NotNull String key, @NotNull Player player, Object... placeholders) {

        String path = String.format("messages.%s.%s", player.locale(), key);
        if (!this.config.isSet(path)) {
            return MessageUtil.translateLegacyColorCodes(MessageFormat.format(this.defaultMessages.getString(key), placeholders));
        }

        return MessageUtil.translateLegacyColorCodes(MessageFormat.format(
                this.config.getString(path, String.format("N/A (%s)", path)), placeholders));
    }

    @NotNull
    public List<Component> getSignLayout(@NotNull String playerName, @NotNull SortType sortType) {
        List<Component> layout = new ArrayList<>();

        String sortTypeDisplay = this.config.getString(
                String.format("messages.sortType-displayNames.%s", sortType.name().toLowerCase()),
                sortType.getDisplayName());

        for (String line : this.containerSortApi.getSettings().getSignLayout()) {
            layout.add(MessageUtil.translateLegacyColorCodes(line.
                    replace("%sign_owner_name%", playerName).
                    replace("%container_sort_type%",
                            sortTypeDisplay.length() > 16 ? sortTypeDisplay.substring(0, 16) : sortTypeDisplay)));
        }
        return layout;
    }

    public boolean createLanguage(@NotNull Locale locale) {

        String path = "messages.%s.%s";
        if (this.config.isSet(path)) {
            return false;
        }

        Iterator<String> iterator = this.defaultMessages.getKeys().asIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            this.config.set(String.format(path, locale, next), this.defaultMessages.getString(next));
        }

        try {
            this.config.save(this.file);
            return true;
        } catch (IOException e) {
            this.containerSortApi.getPlugin().getLogger().log(Level.SEVERE,
                    "Something went wrong while saving the file", e);
            return false;
        }
    }
}
