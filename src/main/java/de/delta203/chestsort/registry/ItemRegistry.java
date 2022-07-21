package de.delta203.chestsort.registry;

import de.delta203.chestsort.api.ChestSortApi;
import de.delta203.chestsort.api.ChestSortItem;
import de.delta203.chestsort.api.util.SortType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {

    private final ChestSortApi chestSortApi;
    private final List<ChestSortItem> items;

    public ItemRegistry(@NotNull ChestSortApi chestSortApi) {
        this.chestSortApi = chestSortApi;
        this.items = new ArrayList<>();

        loadDefaults();
    }

    @NotNull
    public List<ChestSortItem> getItems() {
        return items;
    }

    @Nullable
    public ChestSortItem getSortItem(@NotNull SortType sortType) {

        ChestSortItem chestSortItem = null;
        for (int i = 0; i < this.items.size() && chestSortItem == null; i++) {
            if (this.items.get(i).getSortType() == sortType) {
                chestSortItem = this.items.get(i);
            }
        }

        return chestSortItem;
    }

    @Nullable
    public ChestSortItem getSortItem(@NotNull ItemStack itemStack) {

        ChestSortItem chestSortItem = null;
        for (int i = 0; i < this.items.size() && chestSortItem == null; i++) {
            if (this.items.get(i).toItemStack().isSimilar(itemStack)) {
                chestSortItem = this.items.get(i);
            }
        }

        return chestSortItem;
    }

    @Nullable
    public ChestSortItem getSortItem(@NotNull String name) {

        ChestSortItem chestSortItem = null;
        for (int i = 0; i < this.items.size() && chestSortItem == null; i++) {
            if (this.items.get(i).getName().equalsIgnoreCase(name)) {
                chestSortItem = this.items.get(i);
            }
        }

        return chestSortItem;
    }

    private void loadDefaults() {

        FileConfiguration config = this.chestSortApi.getConfiguration().getConfig();

        ConfigurationSection section = config.getConfigurationSection("gui.items");
        if (section == null) return;

        Material fallbackMaterial = Material.LIME_WOOL;

        for (String key : section.getKeys(false)) {
            key = key.toLowerCase();

            int slot = section.getInt(key + ".slot");
            String displayName = section.getString(key + ".name", key);

            Material material = Material.matchMaterial(
                    section.getString(key + ".material", fallbackMaterial.name()));

            List<Component> lore = new ArrayList<>();
            for (String text : section.getStringList(key + ".lore")) {
                lore.add(this.chestSortApi.translateLegacyColorCodes(text));
            }

            SortType sortType = SortType.getSortType(section.getString(key + ".type", "ALPHABETICALLY").toUpperCase());
            this.items.add(new ChestSortItem(
                    key,
                    this.chestSortApi.translateLegacyColorCodes(displayName),
                    material != null ? material : fallbackMaterial,
                    slot,
                    lore,
                    sortType != null ? sortType : SortType.ALPHABETICALLY));
        }

        if (config.isSet("gui.placeholder_item")) {

            Material placeHolderMaterial = Material.matchMaterial(
                    config.getString("gui.placeholder_item", "BLACK_STAINED_GLASS_PANE"), false);

            this.items.add(new ChestSortItem(
                    "placeholder_item",
                    Component.text(" "),
                    placeHolderMaterial != null ? placeHolderMaterial : Material.BLACK_STAINED_GLASS_PANE,
                    -1,
                    List.of(),
                    SortType.PLACEHOLDER));
        }
    }

    @NotNull
    public ItemStack translateItemDisplayName(@NotNull ItemStack origin) {
        ItemMeta itemMeta = origin.getItemMeta();
        itemMeta.displayName(Component.translatable(origin.translationKey()));
        origin.setItemMeta(itemMeta);
        return origin;
    }

    @NotNull
    public ItemStack buildItemStack(
            @NotNull Material material,
            @NotNull String displayName,
            @NotNull List<String> lore) {

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(this.chestSortApi.translateLegacyColorCodes(displayName));

        List<Component> list = new ArrayList<>();
        for (String text : lore) {
            list.add(this.chestSortApi.translateLegacyColorCodes(text));
        }

        itemMeta.lore(list);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public boolean canInteractWith(@NotNull ItemStack itemStack) {
        ChestSortItem chestSortItem = getSortItem(itemStack);
        if (chestSortItem == null) return false;
        return chestSortItem.getSortType() != SortType.PLACEHOLDER;
    }
}
