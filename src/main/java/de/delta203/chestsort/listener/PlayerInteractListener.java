package de.delta203.chestsort.listener;

import de.delta203.chestsort.api.ChestSortApi;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PlayerInteractListener implements Listener {

	private final ChestSortApi chestSortApi;

	public PlayerInteractListener(@NotNull ChestSortApi chestSortApi) {
		this.chestSortApi = chestSortApi;
	}

	@EventHandler
	public void handlePlayerInteract(PlayerInteractEvent event) {

		Block clickedBlock = event.getClickedBlock();

		if (clickedBlock == null) return;
		Player player = event.getPlayer();

		if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

		// Action zu rechtsklick geändert, weil man zu schnell die Kiste im Kreativmodus zerstört.
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (player.isSneaking()) {
				if (clickedBlock.getState() instanceof Container container) {

					if (!isValidContainer(container)) return;
					if (!this.chestSortApi.getConfiguration().hasPermission(player)) return;
					if (this.chestSortApi.getConfiguration().isWorldDisabled(clickedBlock.getWorld())) return;

					event.setCancelled(true);
					this.chestSortApi.getSortChestInventory().open(player, clickedBlock.getLocation(), clickedBlock);
				}
			}
		}
	}

	private boolean isValidContainer(@NotNull Container container) {
		return container instanceof Chest || container instanceof Barrel || container instanceof DoubleChest;
	}
}
