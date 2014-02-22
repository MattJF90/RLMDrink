package com.rlminecraft.RLMDrink;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import com.rlminecraft.RLMDrink.Exceptions.BadConfigException;
import com.rlminecraft.RLMDrink.listener.ConsumptionListener;



public class RLMDrink extends JavaPlugin {
	
	Logger console;
	boolean debugging = false;
	int tps = 20;
	public YamlConfiguration config;
	public DrunkMap drunkMap;
	
	public void onEnable() {
		console = this.getLogger();
		drunkMap = new DrunkMap();
		Debug("Player drunk map initialized");
		Debug("Loading from config");
		this.saveDefaultConfig();
		config = (YamlConfiguration) this.getConfig();
		verifyConfig();
		// Schedule sober timer
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				reduceDrunkenness();
			}
		}, (20*20), (30*20));
		// Schedule listeners
		this.getServer().getPluginManager().registerEvents(new ConsumptionListener(this), this);
	}
	
	public void onDisable() {
		console.info("RLMDrinks has been disabled!");
	}
	
	
	public void verifyConfig () {
		if (!config.contains("drinks")) {
			console.severe("Configuration error: \"drinks\" section missing!");
			this.getPluginLoader().disablePlugin(this);
			return;
		}
		if (!config.contains("states")) {
			console.severe("Configuration error: \"states\" section missing!");
			this.getPluginLoader().disablePlugin(this);
			return;
		}
	}
	
	
	/* LISTENERS */
	
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR
			|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			try {
				MaterialData item = event.getItem().getData();
				if (item.getItemType() == Material.POTION) {
					Debug("Potion used! Entering potion command...");
					PotionUse(player, item.getData());
				} else if (item.getItemType() == Material.INK_SACK) {
					//add weed effect
				}
			} catch (Exception e) {
				console.warning("Exception caught!");
			} finally {
				// nothing
			}
		}
		return;
	}
	
	public void PotionUse (Player player, int type) {
		if (player == null) {
			console.warning("Null player event!");
			return;
		}
		String drinkName = "";
		String drinkUnit = "";
		int currentDrunkLevel;
		if (drunkenness.containsKey(player.getName())) {
			currentDrunkLevel = drunkenness.get(player.getName());
		} else {
			currentDrunkLevel = 0;
		}
		int oldDrunkLevel = currentDrunkLevel;
		
		// Determine if drink consumed is alcoholic
		ListIterator<Drink> drinkIterator = drinks.listIterator();
		while (drinkIterator.hasNext()) {
			Drink drink = drinkIterator.next();
			if (drink.id == type) {
				drinkName = drink.name;
				drinkUnit = drink.unit;
				currentDrunkLevel += drink.drunkenness;
			}
		}
		if (drinkName == "") return;
		
		player.sendMessage("You drank a " + drinkUnit + " of " + drinkName + ".");
		
		// Determine change in state and/or effects to add
		State oldState = new State();
		State currentState = new State();
		Debug("Current drunkenness: " + currentDrunkLevel);
		ListIterator<State> stateIterator = states.listIterator();
		while (stateIterator.hasNext()) {
			State state = stateIterator.next();
			if (state.start <= oldDrunkLevel
					&& state.finish > oldDrunkLevel) {
				oldState = state;
			}
			if (state.start <= currentDrunkLevel
					&& state.finish > currentDrunkLevel) {
				currentState = state;
			}
		}
		if (currentState.name != oldState.name && !currentState.death) {
			Debug(player.getName() + ": " + oldState.name + " --> " + currentState.name);
			player.sendMessage("You are now " + currentState.name + "!");
		} else if (currentState.name == "NULL" && oldState.name == "NULL") {
			drunkenness.put(player.getName(), currentDrunkLevel);
			return;
		}
		
		// Get potion effects
		if (currentState.death) {
			Debug(player.getName() + ": " + oldState.name + " --> deceased");
			player.sendMessage(ChatColor.RED + "You died of alcohol poisoning!");
			currentDrunkLevel = 0;
			player.setHealth(0);
		} else {
			Debug("Effects: " + currentState.effects.toString());
			ListIterator<Effect> effectIterator = currentState.effects.listIterator();
			while (effectIterator.hasNext()) {
				player.addPotionEffect(effectIterator.next().toPotionEffect());
			}
		}
		drunkenness.put(player.getName(), currentDrunkLevel);
	}
	
	
	private void reduceDrunkenness () {
		Player[] players = this.getServer().getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			String player = players[i].getName();
			if (drunkenness.containsKey(player)) {
				int playerDrunk = drunkenness.get(player);
				playerDrunk -= 1;
				if (playerDrunk > 0) {
					drunkenness.put(player, playerDrunk);
				} else {
					drunkenness.remove(player);
				}
				applyStateEffects(players[i]);
			}
		}
	}
	
	
	private State getState (int drunkLevel) {
		ListIterator<State> stateIterator = states.listIterator();
		while (stateIterator.hasNext()) {
			State state = stateIterator.next();
			if (state.inRange(drunkLevel)) {
				return state;
			}
		}
		return new State();
	}
	
	
	private void applyStateEffects (Player player) {
		if (player == null) return;
		if (!drunkenness.containsKey(player.getName())) return;
		State state = getState(drunkenness.get(player.getName()));
		if (state.name == "NULL") return;
		ListIterator<Effect> effectIterator = state.effects.listIterator();
		while (effectIterator.hasNext()) {
			Effect effect = effectIterator.next();
			player.removePotionEffect(effect.name);
			player.addPotionEffect(effect.toPotionEffect());
		}
	}
	
	
	public void Debug(String message) {
		if (debugging) console.info("[DEBUG] " + message);
	}
	
}