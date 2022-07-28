package de.uniquegame.containersort.api.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public final class InventoryUtil {

    private final static Comparator<ItemStack> SORT_ALPHABETICALLY = Comparator.comparing(itemStack -> itemStack.getType().name());
    private final static Comparator<ItemStack> SORT_BY_LOWEST_AMOUNT = Comparator.comparing(ItemStack::getAmount);
    private final static Comparator<ItemStack> SORT_BY_HIGHEST_AMOUNT = SORT_BY_LOWEST_AMOUNT.reversed();

    public static void sortContainer(@NotNull SortType sortType, @NotNull Inventory inventory) {

        List<ItemStack> list = new ArrayList<>();
        for (ItemStack content : inventory.getContents()) {
            if (content != null) {
                list.add(content);
            }
        }

        switch (sortType) {
            case ALPHABETICALLY -> list.sort(SORT_ALPHABETICALLY);
            case HIGHEST_ITEM_AMOUNT -> list.sort(SORT_BY_HIGHEST_AMOUNT);
            case LOWEST_ITEM_AMOUNT -> list.sort(SORT_BY_LOWEST_AMOUNT);
            default -> throw new IllegalStateException("Unexpected value: " + sortType);
        }

        // Erstelle ein ItemStack Array mit der Länge von der Liste siehe oben.
        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            itemStacks[i] = list.get(i);
        }

        // Setze den content von dem Inventar (container) dann braucht man das Inventar nicht leeren.
        inventory.setContents(InventoryUtil.mergeSimilarStacks(itemStacks));
    }

    @NotNull
    public static ItemStack[] mergeSimilarStacks(ItemStack... items) {
        if (items.length <= 1) {
            return items;
        }

        List<ItemStack> itemList = new LinkedList<>();

        Iterating:
        for (ItemStack item : items) {
            for (ItemStack iStack : itemList) {
                if (iStack.getAmount() == iStack.getMaxStackSize()) continue;
                if (item.isSimilar(iStack)) {
                    iStack.setAmount(iStack.getAmount() + item.getAmount());
                    continue Iterating;
                }
            }

            itemList.add(item.clone());
        }

        return itemList.toArray(new ItemStack[0]);
    }

}
