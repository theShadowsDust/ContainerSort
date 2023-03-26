package de.theshadowsdust.containersort.service;

import de.theshadowsdust.containersort.api.SortType;
import de.theshadowsdust.containersort.api.ContainerSortApi;
import de.theshadowsdust.containersort.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.apache.commons.lang3.LocaleUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class LanguageService {

    private final ContainerSortApi containerSortApi;
    private final File file;
    private YamlConfiguration config;
    private final ResourceBundle defaultMessages;

    public LanguageService(@NotNull ContainerSortApi containerSortApi) {

        this.containerSortApi = containerSortApi;
        this.defaultMessages = ResourceBundle.getBundle("containersort", new UTF8ResourceBundleControl());
        this.file = new File(containerSortApi.getPlugin().getDataFolder(), "language.yml");
        load();
    }

    public void load() {
        boolean saveDefaultMessages = false;

        if (!file.exists()) {
            containerSortApi.getPlugin().saveResource(file.getName(), false);
            saveDefaultMessages = true;
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        if (saveDefaultMessages) {
            updateLocale(Locale.US);
        } else {
            updateLocales();
        }
    }

    public String pluginPrefix() {
        return this.config.getString("messages.prefix");
    }

    @NotNull
    public Component getMessage(@NotNull String key, @NotNull CommandSender commandSender, Object... placeholders) {
        String path = String.format("messages.%s.%s", commandSender instanceof Player player ? player.locale() : Locale.US, key);
        return MessageUtil.translateLegacyColorCodes(
                MessageFormat.format(this.config.getString(path, this.defaultMessages.getString(key)), placeholders));
    }

    @NotNull
    public List<Component> getSignLayout(@NotNull String playerName, @NotNull SortType sortType) {
        List<Component> layout = new ArrayList<>();

        String sortTypeDisplay = this.config.getString(
                String.format("messages.sortType-displayNames.%s", sortType.name().toLowerCase()),
                sortType.getDisplayName());

        for (String line : this.containerSortApi.getConfiguration().getSignLayout()) {
            layout.add(MessageUtil.translateLegacyColorCodes(line.
                    replace("%sign_owner_name%", playerName).
                    replace("%container_sort_type%",
                            sortTypeDisplay.length() > 16 ? sortTypeDisplay.substring(0, 16) : sortTypeDisplay)));
        }
        return layout;
    }


    private void updateLocales() {

        ConfigurationSection section = this.config.getConfigurationSection("messages");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            if (!key.contains("_")) continue;
            updateLocale(LocaleUtils.toLocale(key));
        }
    }

    public CompletableFuture<LocaleUpdateResult> updateLocale(@NotNull Locale locale) {
        return CompletableFuture.supplyAsync(() -> {

            String path = "messages.%s.%s";
            Iterator<String> iterator = this.defaultMessages.getKeys().asIterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                String formattedPath = path.formatted(locale, next);
                if(this.config.isSet(formattedPath)) continue;
                this.containerSortApi.getPlugin().getLogger().info(formattedPath);
                this.config.set(formattedPath, this.defaultMessages.getString(next));
            }

            try {
                saveConfig();
                return LocaleUpdateResult.SUCCESS;
            } catch (IOException e) {
                this.containerSortApi.getPlugin().getLogger().log(Level.SEVERE,
                        "Something went wrong while saving the file", e);
                return LocaleUpdateResult.CANNOT_SAVE;
            }
        });
    }

    public void saveConfig() throws IOException {
        this.config.save(this.file);
    }

    public enum LocaleUpdateResult {
        SUCCESS,
        CANNOT_SAVE,
        ALREADY_EXISTS;
    }
}
