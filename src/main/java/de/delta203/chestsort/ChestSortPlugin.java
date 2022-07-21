package de.delta203.chestsort;

import de.delta203.chestsort.api.ChestSortApi;
import de.delta203.chestsort.api.ChestSortApiImpl;
import de.delta203.chestsort.listener.InventoryClickListener;
import de.delta203.chestsort.listener.PlayerConnectionListener;
import de.delta203.chestsort.listener.PlayerInteractListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSortPlugin extends JavaPlugin {

	private ChestSortApiImpl chestSortApiImpl;

	@Override
	public void onEnable() {

		PluginManager pluginManager = getServer().getPluginManager();

		this.chestSortApiImpl = new ChestSortApiImpl(this);
		this.getServer().getServicesManager().register(ChestSortApi.class, this.chestSortApiImpl, this, ServicePriority.Highest);


		pluginManager.registerEvents(new PlayerConnectionListener(this.chestSortApiImpl), this);
		pluginManager.registerEvents(new PlayerInteractListener(this.chestSortApiImpl), this);
		pluginManager.registerEvents(new InventoryClickListener(this.chestSortApiImpl), this);
		getServer().getOnlinePlayers().forEach(player -> this.chestSortApiImpl.getSortChestInventory().setupInventory(player));
	}

	@Override
	public void onDisable() {
		this.getServer().getServicesManager().unregister(ChestSortApi.class, this.chestSortApiImpl);
	}
}
