package com.rlminecraft.RLMDrink;

import java.util.concurrent.ConcurrentHashMap;

public class DrunkMap {
	
	private ConcurrentHashMap<String,Integer> map;
	
	public DrunkMap () {
		this.map = new ConcurrentHashMap<String,Integer>();
	}
	
	public int getDrunkenness (String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		else {
			return 0;
		}
	}
	
	public void increaseDrunkenness (String name, int amount) {
		if (!map.containsKey(name)) {
			map.put(name, amount);
		}
		else {
			int newAmount = map.get(name) + amount;
			map.put(name, newAmount);
		}
	}
	
	public void decreaseDrunkenness (String name, int amount) {
		if (map.containsKey(name)) {
			int newAmount = map.get(name) - amount;
			if (newAmount <= 0) {
				map.remove(name);
			}
			else {
				map.put(name, newAmount);
			}
		}
	}
	
	
	
}
