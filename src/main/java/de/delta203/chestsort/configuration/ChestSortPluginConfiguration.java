package de.delta203.chestsort.configuration;

import de.delta203.chestsort.api.ChestSortApi;
import de.delta203.chestsort.api.util.SoundType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestSortPluginConfiguration {

    private final ChestSortApi chestSortApi;
    private final Map<SoundType, Sound> soundTypesMap;
    private List<String> disabledWorlds;
    private String permission;

    public ChestSortPluginConfiguration(@NotNull ChestSortApi chestSortApi) {
        this.chestSortApi = chestSortApi;
        this.soundTypesMap = new HashMap<>();
        loadConfig();
    }

    public void playSound(@NotNull Player player, @NotNull SoundType soundType) {
        Sound sound = getSound(soundType);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1F, 1F);
        }
    }

    @NotNull
    public FileConfiguration getConfig() {
        return this.chestSortApi.getPlugin().getConfig();
    }

    @Nullable
    public Sound getSound(@NotNull SoundType soundType) {
        return this.soundTypesMap.get(soundType);
    }

    @NotNull
    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public void setPermission(@NotNull String permission) {
        this.permission = permission;
    }

    @NotNull
    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(@NotNull Permissible permissible) {
        return permissible.hasPermission(this.getPermission());
    }

    public void loadConfig() {

        this.chestSortApi.getPlugin().saveDefaultConfig();
        double currentConfigVersion = getConfig().getDouble("config-version", 1.0);

        boolean saveConfig = false;

        try (InputStream inputStream = this.chestSortApi.getPlugin().getResource("config.yml")) {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(inputStreamReader);

                double configVersion = configuration.getDouble("config-version", 1.0);

                if (currentConfigVersion != configVersion) {
                    for (String key : configuration.getKeys(true)) {
                        if (!getConfig().isSet(key)) {
                            Object value = configuration.get(key);
                            getConfig().set(key, value);
                        }
                    }

                    saveConfig = true;
                    currentConfigVersion = configVersion;
                }

                inputStreamReader.close();
            }

            if (saveConfig) {
                getConfig().set("config-version", currentConfigVersion);
                this.chestSortApi.getPlugin().saveConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        putSound(SoundType.OPEN_GUI, "sound.openGui");
        putSound(SoundType.SORT_SUCCESS, "sound.sort_success");
        putSound(SoundType.SORT_FAILED, "sound.sort_failed");
        this.permission = getConfig().getString("permission");
        this.disabledWorlds = getConfig().getStringList("disabledWorlds");
    }

    public boolean lockingContainers() {
        return this.getConfig().getBoolean("lock-containers");
    }

    private void putSound(@NotNull SoundType soundType, @Nullable String configPath) {
        if (configPath == null) return;
        if (!getConfig().isSet(configPath)) return;
        this.soundTypesMap.put(soundType, Sound.valueOf(getConfig().getString(configPath)));
    }

    public boolean isWorldDisabled(@NotNull World world) {
        return this.disabledWorlds.contains(world.getName());
    }
}
