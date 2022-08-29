package de.uniquegame.containersort.listener;

import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.util.Permissions;
import de.uniquegame.containersort.util.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public class ContainerProtectionListener implements Listener {

    private final ContainerSortApi api;

    public ContainerProtectionListener(ContainerSortApi api) {
        this.api = api;
    }

    @EventHandler
    public void handleContainerBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.api.getSettings().isProtectContainers() && block.getState() instanceof Container container) {
            Map<Container, Sign> map = findSignAndContainer(container.getInventory().getHolder());
            if (!map.isEmpty()) {
                Sign connectedSign = map.get(container);
                if (this.api.isSortSign(connectedSign) && !this.api.isSignOwner(player.getUniqueId(), connectedSign)) {
                    event.setCancelled(!player.hasPermission(Permissions.PERMISSION_SORT_BREAK_OTHERS));
                }
            }
        }
    }

    @EventHandler
    public void handleSignBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();
        event.setCancelled(this.api.getSettings().isProtectSigns() &&
                !player.hasPermission(Permissions.PERMISSION_SORT_BREAK_OTHERS)
                && block.getState() instanceof Sign sign &&
                this.api.isSortSign(sign)
                && !this.api.isSignOwner(player.getUniqueId(), sign));
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {

        if (!this.api.getSettings().isItemTransferBlocked()) return;
        Inventory destination = event.getDestination();
        boolean cancel = false;

        Map<Container, Sign> map = findSignAndContainer(destination.getHolder());
        if (destination.getHolder() instanceof BlockInventoryHolder holder &&
                holder.getBlock().getState() instanceof Container container &&
                !map.isEmpty()) {
            cancel = this.api.isSortSign(map.get(container)) && this.api.isValidContainer(container);
        }

        event.setCancelled(cancel);
    }

    private Map<Container, Sign> findSignAndContainer(InventoryHolder holder) {

        Sign connectedSign = null;
        Container connectedContainer = null;

        if (holder instanceof DoubleChest doubleChest) {

            InventoryHolder rightSide = doubleChest.getRightSide();
            InventoryHolder leftSide = doubleChest.getLeftSide();

            if (leftSide instanceof BlockInventoryHolder inventoryHolder &&
                    inventoryHolder.getBlock().getState() instanceof Container container) {
                connectedSign = SignUtil.findConnectedSign(container);
                connectedContainer = container;
            }

            if (rightSide instanceof BlockInventoryHolder inventoryHolder &&
                    inventoryHolder.getBlock().getState() instanceof Container container) {
                connectedSign = SignUtil.findConnectedSign(container);
                connectedContainer = container;
            }
        } else {
            if (holder instanceof BlockInventoryHolder inventoryHolder &&
                    inventoryHolder.getBlock().getState() instanceof Container container) {
                connectedSign = SignUtil.findConnectedSign(container);
                connectedContainer = container;
            }
        }

        return connectedSign != null ? Map.of(connectedContainer, connectedSign) : Map.of();
    }
}
