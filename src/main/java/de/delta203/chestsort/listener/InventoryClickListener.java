package de.delta203.chestsort.listener;

import de.delta203.chestsort.SortType;
import de.delta203.chestsort.api.ChestSortApi;
import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class InventoryClickListener implements Listener {

	private final ChestSortApi chestSortApi;

	public InventoryClickListener(@NotNull ChestSortApi chestSortApi) {
		this.chestSortApi = chestSortApi;
	}

	@EventHandler
	public void handleInventoryClose(InventoryCloseEvent event) {

		Player player = (Player) event.getPlayer();
		Inventory inventory = this.chestSortApi.getSortChestInventory().getInventory(player);
		if (inventory != null && inventory.equals(event.getInventory())) {
			this.chestSortApi.removePlayerContainerLocation(player);
		}
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {

		Inventory destination = event.getDestination();
		boolean cancel = false;

		if (destination.getHolder() instanceof Container container) {
			cancel = this.chestSortApi.isContainerLocked(container.getLocation()) &&
					this.chestSortApi.getConfiguration().lockingContainers();
		}

		if (destination.getHolder() instanceof DoubleChest doubleChest) {

			InventoryHolder leftSide = doubleChest.getLeftSide();
			InventoryHolder rightSide = doubleChest.getRightSide();

			if (leftSide != null && rightSide != null) {
				if (leftSide instanceof BlockInventoryHolder leftHolder &&
						rightSide instanceof BlockInventoryHolder rightHolder) {

					cancel = ( this.chestSortApi.isContainerLocked(leftHolder.getBlock().getLocation()) ||
							this.chestSortApi.isContainerLocked(rightHolder.getBlock().getLocation()) )  &&
							this.chestSortApi.getConfiguration().lockingContainers();
				}
			}
		}

		event.setCancelled(cancel);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {

		if (event.getWhoClicked() instanceof Player whoClicked) {
			Inventory inventory = this.chestSortApi.getSortChestInventory().getInventory(whoClicked);
			if (inventory == null) return;

			Inventory clickedInventory = event.getClickedInventory();
			if (clickedInventory == null) return;
			if (!clickedInventory.equals(inventory)) return;

			event.setCancelled(true);

			if (!this.chestSortApi.getConfiguration().hasPermission(whoClicked)) {
				closeView(whoClicked);
				return;
			}

			Location storedChestLocation = this.chestSortApi.getContainerLocation(whoClicked);

			if (storedChestLocation == null) {
				closeView(whoClicked);
				return;
			}

			BlockState blockState = storedChestLocation.getBlock().getState();

			if (blockState instanceof Container container) {
				if (!isValidContainer(container)) {
					closeView(whoClicked);
					return;
				}
			}

			ItemStack currentItem = event.getCurrentItem();
			if (currentItem == null) return;

			if (!this.chestSortApi.getItemRegistry().canInteractWith(currentItem)) return;

			Container container = (Container) storedChestLocation.getBlock().getState();
			InventoryHolder holder = container.getInventory().getHolder();

			if (holder == null) return;

			ChestSortItem chestSortItem = this.chestSortApi.getItemRegistry().getSortItem(currentItem);
			if (chestSortItem == null || chestSortItem.getSortType() == SortType.PLACEHOLDER) {
				closeView(whoClicked);
				return;
			}

			InventoryUtil.sortContainer(chestSortItem.getSortType(), holder.getInventory());

			this.chestSortApi.playSound(whoClicked, SoundType.SORT_SUCCESS);
			whoClicked.openInventory(holder.getInventory());
		}
	}

	private boolean isValidContainer(Container container) {

		Inventory inventory = container.getInventory();
		InventoryHolder inventoryHolder = inventory.getHolder();

		if (inventoryHolder != null) {
			if (inventoryHolder instanceof DoubleChest) {
				return true;
			}
		}

		return container instanceof Barrel || container instanceof Chest;

	}

	private void closeView(@NotNull Player player) {
		this.chestSortApi.playSound(player, SoundType.SORT_FAILED);
		player.closeInventory();
	}
}
