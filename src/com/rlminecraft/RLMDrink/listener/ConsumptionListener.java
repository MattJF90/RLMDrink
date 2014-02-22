package com.rlminecraft.RLMDrink.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.material.MaterialData;

import com.rlminecraft.RLMDrink.RLMDrink;

public class ConsumptionListener implements Listener {
	
	private RLMDrink plugin;
	
	public ConsumptionListener (RLMDrink instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void OnPotionDrink (PlayerItemConsumeEvent event) {
		MaterialData item = event.getItem().getData();
		if (item.getItemType() != Material.POTION) {
			return;
		}
		if (!plugin.config.contains("drinks." + item.getData())) {
			return; // not a drink in the config
		}
		// Load drink data
		String name = plugin.config.getString("drinks." + item.getData() + ".name", "alcohol");
		String unit = plugin.config.getString("drinks." + item.getData() + ".unit", "drink");
		int drunkenness = plugin.config.getInt("drinks." + item.getData() + ".drunkenness", 1);
		plugin.drunkMap.increaseDrunkenness(event.getPlayer().getName(), drunkenness);
		event.getPlayer().sendMessage("You drank a " + unit + " of " + name + ".");
	}
	
}
