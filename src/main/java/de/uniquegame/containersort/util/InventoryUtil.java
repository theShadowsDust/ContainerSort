package de.uniquegame.containersort.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public final class InventoryUtil {

    public static final Comparator<ItemStack> SORT_ALPHABETICALLY = Comparator.comparing(itemStack -> itemStack.getType().name());
    public static final Comparator<ItemStack> SORT_BY_LOWEST_AMOUNT = Comparator.comparing(ItemStack::getAmount);
    public static final Comparator<ItemStack> SORT_BY_HIGHEST_AMOUNT = SORT_BY_LOWEST_AMOUNT.reversed();

    private InventoryUtil() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings({"java:S1119", "java:S135"})
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
