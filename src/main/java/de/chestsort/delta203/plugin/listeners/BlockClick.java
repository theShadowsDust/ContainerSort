package de.chestsort.delta203.plugin.listeners;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.chestsort.delta203.plugin.files.ConfigYML;

public class BlockClick implements Listener {

	public static HashMap<Player, Location> player_chestLoc = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.LEFT_CLICK_BLOCK && p.isSneaking()) {
			try {
				if(e.getClickedBlock().getType() == Material.CHEST) {
					if(!p.hasPermission(ConfigYML.get().getString("permission"))) return;
					if(ConfigYML.get().getStringList("disabledWorlds").contains(p.getWorld().getName())) return;
					
					e.setCancelled(true);
					player_chestLoc.put(p, e.getClickedBlock().getLocation());
					if(!ConfigYML.get().getString("sound.openGui").equalsIgnoreCase("None")) p.playSound(p.getLocation(), Sound.valueOf(ConfigYML.get().getString("sound.openGui")), 1, 1);
					
					int guisize = ConfigYML.get().getInt("gui.size");
					String guiname = ConfigYML.get().getString("gui.name").replace('&', '§');
					
					Inventory inv = Bukkit.createInventory(p, guisize, guiname);
					
					if(ConfigYML.get().getInt("gui.glasscolor") != -1) {
						for(int i = 0; i < guisize; i++) {
							inv.setItem(i, easy(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) ConfigYML.get().getInt("gui.glasscolor")), " "));
						}
					}
					
					inv.setItem(ConfigYML.get().getInt("gui.items.chest.slot")-1, 
							normal(new ItemStack(Material.valueOf(ConfigYML.get().getString("gui.items.chest.material")), 1, (short) ConfigYML.get().getInt("gui.items.chest.subID")),
									ConfigYML.get().getString("gui.items.chest.name").replace('&', '§'),
									ConfigYML.get().getString("gui.items.chest.lore")
										.replace('&', '§')
										.replace("%x%", "" + e.getClickedBlock().getLocation().getBlockX())
										.replace("%y%", "" + e.getClickedBlock().getLocation().getBlockY())
										.replace("%z%", "" + e.getClickedBlock().getLocation().getBlockZ())
									)
							);
					inv.setItem(ConfigYML.get().getInt("gui.items.sort_a_z.slot")-1, 
							normal(new ItemStack(Material.valueOf(ConfigYML.get().getString("gui.items.sort_a_z.material")), 1, (short) ConfigYML.get().getInt("gui.items.sort_a_z.subID")),
									ConfigYML.get().getString("gui.items.sort_a_z.name").replace('&', '§'),
									ConfigYML.get().getString("gui.items.sort_a_z.lore")
										.replace('&', '§'))
							);
					inv.setItem(ConfigYML.get().getInt("gui.items.sort_amount.slot")-1, 
							normal(new ItemStack(Material.valueOf(ConfigYML.get().getString("gui.items.sort_amount.material")), 1, (short) ConfigYML.get().getInt("gui.items.sort_amount.subID")),
									ConfigYML.get().getString("gui.items.sort_amount.name").replace('&', '§'),
									ConfigYML.get().getString("gui.items.sort_amount.lore")
										.replace('&', '§'))
							);
					p.openInventory(inv);
				}
			}catch(Exception ex) {}
		}
	}
	
	private static ItemStack easy(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack normal(ItemStack item, String name, String lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore.split(",")));
		item.setItemMeta(meta);
		
		return item;
	}
}
