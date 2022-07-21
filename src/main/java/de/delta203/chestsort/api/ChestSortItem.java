package de.delta203.chestsort.api;

import de.delta203.chestsort.api.util.SortType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public final class ChestSortItem {

    private String name;
    private Component displayName;
    private Material material;
    private int slot;
    private List<Component> description;
    private SortType sortType;
    private ItemStack toItemStack;

    public ChestSortItem(
            @NotNull String name,
            @NotNull Component displayName,
            @NotNull Material material,
            int slot,
            @NotNull List<Component> description,
            @NotNull SortType sortType) {

        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.slot = slot;
        this.description = description;
        this.sortType = sortType;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull Component displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @NotNull
    public List<Component> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<Component> description) {
        this.description = description;
    }

    @NotNull
    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(@NotNull SortType sortType) {
        this.sortType = sortType;
    }

    @NotNull
    public ItemStack toItemStack() {

        if (this.toItemStack == null) {
            this.toItemStack = new ItemStack(this.material);
            ItemMeta itemMeta = this.toItemStack.getItemMeta();
            itemMeta.displayName(getDisplayName());
            itemMeta.lore(this.description);
            this.toItemStack.setItemMeta(itemMeta);
        }

        return this.toItemStack;
    }
}
