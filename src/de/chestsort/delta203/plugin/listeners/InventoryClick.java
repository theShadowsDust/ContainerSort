package de.chestsort.delta203.plugin.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import de.chestsort.delta203.plugin.SortType;
import de.chestsort.delta203.plugin.files.ConfigYML;

public class InventoryClick implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory().getTitle().equalsIgnoreCase(ConfigYML.get().getString("gui.name").replace('&', '§'))) {
			e.setCancelled(true);
			if(!p.hasPermission(ConfigYML.get().getString("permission"))) {
				e.getView().close();
				if(!ConfigYML.get().getString("sound.sort_failed").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.sort_failed")), 1, 1);
				return;
			}
			
			Location blockLoc = BlockClick.player_chestLoc.get(p);
			
			if(blockLoc == null) { 
				e.getView().close();
				if(!ConfigYML.get().getString("sound.sort_failed").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.sort_failed")), 1, 1);
				return;
			}
			if(blockLoc.getBlock().getType() != Material.CHEST) {
				e.getView().close();
				if(!ConfigYML.get().getString("sound.sort_failed").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.sort_failed")), 1, 1);
				return;
			}
			
			try {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ConfigYML.get().getString("gui.items.sort_a_z.name").replace('&', '§'))) {
					Chest chest = (Chest) blockLoc.getBlock().getState();
					InventoryHolder holder = chest.getInventory().getHolder();
					if(holder instanceof DoubleChest) { //-> doublechest
						DoubleChest doubleChest = (DoubleChest) holder;
						sortDoubleChest(SortType.A_Z, doubleChest);
					}else { //-> normal chest
						sortNormalChest(SortType.A_Z, chest);
					}
					e.getView().close();
					if(!ConfigYML.get().getString("sound.sort_success").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.sort_success")), 1, 1);
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ConfigYML.get().getString("gui.items.sort_amount.name").replace('&', '§'))) {
					Chest chest = (Chest) blockLoc.getBlock().getState();
					InventoryHolder holder = chest.getInventory().getHolder();
					if(holder instanceof DoubleChest) { //-> doublechest
						DoubleChest doubleChest = (DoubleChest) holder;
						sortDoubleChest(SortType.AMOUNT, doubleChest);
					}else { //-> normal chest
						sortNormalChest(SortType.AMOUNT, chest);
					}
					e.getView().close();
					if(!ConfigYML.get().getString("sound.sort_success").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.sort_success")), 1, 1);
				}
			}catch(Exception ex) {
				e.getView().close();
				if(!ConfigYML.get().getString("sound.sort_failed").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.sort_failed")), 1, 1);
				return;
			}
		}
	}
	
	private void sortNormalChest(SortType type, Chest chest) {
		if(type == SortType.A_Z) {
			/*
			 * create hash
			 */
			HashMap<String, ArrayList<ItemStack>> id_items = new HashMap<>();
			for(ItemStack content : chest.getInventory().getContents()) {
				try {
					if(content != null) {
						if(!id_items.containsKey(content.toString())) {
							ArrayList<ItemStack> items = new ArrayList<>();
							items.add(content);
							id_items.put(content.toString(), items);
						}else {
							ArrayList<ItemStack> items = id_items.get(content.toString());
							items.add(content);
							id_items.put(content.toString(), items);
						}
					}
				}catch(Exception ex) {}
			}
			
			/*
			 * sort items
			 */
			ArrayList<String> sortedItems = new ArrayList<>(id_items.keySet());
			Collections.sort(sortedItems);
			
			chest.getInventory().clear();
			for(String itemString : sortedItems) {
				for(ItemStack content : id_items.get(itemString)) {
					chest.getInventory().addItem(content);
				}
			}
		}else { //-> type == AMOUNT
			/*
			 * prepare chest 
			 */
			ArrayList<ItemStack> items = new ArrayList<>();
			for(ItemStack content : chest.getInventory().getContents()) {
				try {
					if(content != null) {
						items.add(content);
					}
				}catch(Exception ex) {}
			}
			chest.getInventory().clear();
			for(ItemStack content : items) {
				chest.getInventory().addItem(content);
			}
			
			/*
			 * create hash
			 */
			HashMap<String, ItemStack> amount_id_items = new HashMap<>();
			for(ItemStack content : chest.getInventory().getContents()) {
				try {
					if(content != null) {
						amount_id_items.put(content.getAmount()+content.toString(), content);
					}
				}catch(Exception ex) {}
			}
			
			/*
			 * sort items
			 */
			ArrayList<String> sortedItems = new ArrayList<>(amount_id_items.keySet());
			Collections.sort(sortedItems);
			Collections.reverse(sortedItems);
			
			chest.getInventory().clear();
			for(String itemString : sortedItems) {
				chest.getInventory().addItem(amount_id_items.get(itemString));
			}
		}
	}
	
	private void sortDoubleChest(SortType type, DoubleChest chest) {
		if(type == SortType.A_Z) {
			/*
			 * create hash
			 */
			HashMap<String, ArrayList<ItemStack>> id_items = new HashMap<>();
			for(ItemStack content : chest.getInventory().getContents()) {
				try {
					if(content != null) {
						if(!id_items.containsKey(content.toString())) {
							ArrayList<ItemStack> items = new ArrayList<>();
							items.add(content);
							id_items.put(content.toString(), items);
						}else {
							ArrayList<ItemStack> items = id_items.get(content.toString());
							items.add(content);
							id_items.put(content.toString(), items);
						}
					}
				}catch(Exception ex) {}
			}
			
			/*
			 * sort items
			 */
			ArrayList<String> sortedItems = new ArrayList<>(id_items.keySet());
			Collections.sort(sortedItems);
			
			chest.getInventory().clear();
			for(String itemString : sortedItems) {
				for(ItemStack content : id_items.get(itemString)) {
					chest.getInventory().addItem(content);
				}
			}
		}else { //-> type == AMOUNT
			/*
			 * prepare chest 
			 */
			ArrayList<ItemStack> items = new ArrayList<>();
			for(ItemStack content : chest.getInventory().getContents()) {
				try {
					if(content != null) {
						items.add(content);
					}
				}catch(Exception ex) {}
			}
			chest.getInventory().clear();
			for(ItemStack content : items) {
				chest.getInventory().addItem(content);
			}
			
			/*
			 * create hash
			 */
			HashMap<String, ItemStack> amount_id_items = new HashMap<>();
			for(ItemStack content : chest.getInventory().getContents()) {
				try {
					if(content != null) {
						amount_id_items.put(content.getAmount()+content.toString(), content);
					}
				}catch(Exception ex) {}
			}
			
			/*
			 * sort items
			 */
			ArrayList<String> sortedItems = new ArrayList<>(amount_id_items.keySet());
			Collections.sort(sortedItems);
			Collections.reverse(sortedItems);
			
			chest.getInventory().clear();
			for(String itemString : sortedItems) {
				chest.getInventory().addItem(amount_id_items.get(itemString));
			}
		}
	}
}
