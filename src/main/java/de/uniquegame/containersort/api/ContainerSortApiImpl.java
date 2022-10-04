package de.uniquegame.containersort.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.uniquegame.containersort.ContainerSortPlugin;
import de.uniquegame.containersort.configuration.ContainerSortConfiguration;
import de.uniquegame.containersort.configuration.ContainerSortProperty;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.service.PersistentDataStoreService;
import de.uniquegame.containersort.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ContainerSortApiImpl implements ContainerSortApi {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);

    private final File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final ContainerSortConfiguration defaultConfig;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private ContainerSortConfiguration configuration;

    private final ContainerSortPlugin plugin;
    private final PersistentDataStoreService persistentDataStoreService;
    private final LanguageService languageService;

    public ContainerSortApiImpl(@NotNull ContainerSortPlugin plugin) throws IOException {
        this.plugin = plugin;

        this.defaultConfig = new ContainerSortConfiguration(getPluginVersion(),
                List.of("world_nether", "world_the_end"),
                List.of("chest", "barrel", "shulker_box"),
                ContainerSortProperty.DEFAULTS,
                List.of("&f[&6ContainerSort&f]", "&e%sign_owner_name%", "%container_sort_type%", " "));

        this.persistentDataStoreService = new PersistentDataStoreService(this);
        this.languageService = new LanguageService(this);

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) Files.createDirectories(dataFolder.toPath());

        this.configFile = new File(dataFolder, "config.json");
        if (!this.configFile.exists()) {
            Files.createFile(this.configFile.toPath());
        }

        this.configuration = loadConfig();
    }

    @Override
    public boolean isSortSign(@NotNull Sign sign) {

        NamespacedKey sortSign = this.persistentDataStoreService.getNameSpacedKey(PersistentDataStoreService.SORT_SIGN);
        if (sortSign == null) return false;

        PersistentDataContainer dataContainer = sign.getPersistentDataContainer();
        if (!dataContainer.has(sortSign)) return false;

        Byte signValue = dataContainer.get(sortSign, PersistentDataType.BYTE);
        return signValue != null && signValue == 1;
    }

    @Override
    public boolean isSignOwner(@NotNull UUID playerId, @NotNull Sign sign) {

        PersistentDataContainer container = sign.getPersistentDataContainer();
        NamespacedKey signOwnerKey = this.persistentDataStoreService.
                getNameSpacedKey(PersistentDataStoreService.SORT_SIGN_OWNER);

        if (signOwnerKey == null) return false;
        if (!container.has(signOwnerKey)) return false;

        UUID value = container.get(signOwnerKey, PersistentDataStoreService.UUID_TAG_TYPE);
        if (value == null) return false;

        return value.equals(playerId);
    }

    @Override
    public ContainerSortPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public PersistentDataStoreService getDataStoreService() {
        return this.persistentDataStoreService;
    }

    @Override
    public LanguageService getLanguageService() {
        return this.languageService;
    }

    @Override
    public ContainerSortConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public <T> T getPropertyValue(@NotNull ContainerSortProperty<T> property, @NotNull Class<T> target) {
        return this.configuration.getPropertyValue(property, target);
    }

    @Override
    public void saveSignData(@NotNull UUID playerId,
                             @NotNull String playerName,
                             @NotNull SortType sortType,
                             @NotNull SignChangeEvent event) {

        Sign sign = (Sign) event.getBlock().getState();

        this.getDataStoreService().applyPersistentData(
                sign,
                PersistentDataStoreService.SORT_SIGN_OWNER,
                PersistentDataStoreService.UUID_TAG_TYPE, playerId);

        this.getDataStoreService().applyPersistentData(
                sign,
                PersistentDataStoreService.SORT_SIGN,
                PersistentDataType.BYTE,
                (byte) 1);

        sign.update();
        List<Component> lines = this.getLanguageService().getSignLayout(playerName, sortType);
        for (int i = 0; i < lines.size(); i++) {
            event.line(i, lines.get(i));
        }
    }

    @Override
    public boolean isValidContainer(@NotNull BlockState state) {
        if (!(state instanceof Container container)) return false;

        InventoryHolder inventoryHolder = container.getInventory().getHolder();
        if (inventoryHolder == null) return false;
        return this.configuration.getAllowedContainers().contains(inventoryHolder.
                getInventory().getType().name().toLowerCase());
    }

    @Override
    public void sortContainer(@NotNull SortType sortType, @NotNull Inventory inventory) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack content : inventory.getContents()) {
            if (content != null) {
                list.add(content);
            }
        }
        switch (sortType) {
            case ALPHABETICALLY -> list.sort(InventoryUtil.SORT_ALPHABETICALLY);
            case HIGHEST_ITEM_AMOUNT -> list.sort(InventoryUtil.SORT_BY_HIGHEST_AMOUNT);
            case LOWEST_ITEM_AMOUNT -> list.sort(InventoryUtil.SORT_BY_LOWEST_AMOUNT);
            default -> throw new IllegalStateException("Unexpected value: " + sortType);
        }

        // Erstelle ein ItemStack Array mit der Länge von der Liste siehe oben.
        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            itemStacks[i] = list.get(i);
        }
        inventory.setContents(InventoryUtil.mergeSimilarStacks(itemStacks));
    }

    @Override
    public void reloadConfig() throws IOException {
        this.configuration = loadConfig();
    }

    @NotNull
    private ContainerSortConfiguration loadConfig() throws IOException {

        var config = defaultConfig;
        boolean needUpdate;

        try (BufferedReader bufferedReader = Files.newBufferedReader(this.configFile.toPath())) {
            config = gson.fromJson(bufferedReader, ContainerSortConfiguration.class);
            needUpdate = !config.getPluginVersion().equals(defaultConfig.getPluginVersion());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        if (needUpdate) {

            File file = new File(this.plugin.getDataFolder(), String.format("bak_config_%s.json", dateFormat.format(new Date())));
            if (!file.exists()) {
                Files.copy(this.configFile.toPath(), file.toPath());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(this.configFile.toPath())) {
                config = defaultConfig;
                writer.write(gson.toJson(defaultConfig));
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        }
        return config;
    }

    @NotNull
    private String getPluginVersion() {
        String version = this.getPlugin().getDescription().getVersion();
        String versionSplitter = "-";
        if (version.contains(versionSplitter)) {
            version = version.split(versionSplitter)[0];
        }
        return version;
    }
}
