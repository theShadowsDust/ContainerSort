package de.chestsort.delta203.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.chestsort.delta203.plugin.files.ConfigYML;
import de.chestsort.delta203.plugin.listeners.BlockClick;
import de.chestsort.delta203.plugin.listeners.InventoryClick;

public class ChestSort extends JavaPlugin {

	public static ChestSort plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		ConfigYML.create();
		ConfigYML.load();
		
		Bukkit.getPluginManager().registerEvents(new BlockClick(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
	}
}
