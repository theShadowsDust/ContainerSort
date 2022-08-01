package de.uniquegame.containersort.configuration;

import de.uniquegame.containersort.api.ContainerSortApi;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public final class ContainerSortPluginConfiguration {

    private final ContainerSortApi containerSortApi;
    private ContainerSortSettings settings;

    public ContainerSortPluginConfiguration(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        loadConfig();
    }

    @NotNull
    public FileConfiguration getConfig() {
        return this.containerSortApi.getPlugin().getConfig();
    }

    @NotNull
    public ContainerSortSettings getSettings() {
        return settings;
    }

    public void loadConfig() {

        File dataFolder = this.containerSortApi.getPlugin().getDataFolder();
        if (!dataFolder.exists()) {
            try {
                Files.createDirectory(dataFolder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(dataFolder, "config.yml");
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.settings = this.getConfig().getObject("settings", ContainerSortSettings.class);
        if (this.settings == null) {

            this.settings = new ContainerSortSettings(
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    List.of("world_nether", "world_the_end"),
                    true,
                    List.of("&f[&6ContainerSort&f]", "%sign_owner_name%", "%container_sort_type%", " "), 4);

            this.getConfig().set("settings", this.settings);
            this.containerSortApi.getPlugin().saveConfig();
        }
    }
}
