package com.rlminecraft.RLMDrink;

import java.util.Map;

import org.bukkit.configuration.MemorySection;

import com.rlminecraft.RLMDrink.Exceptions.BadConfigException;

public class Drink {
	public String name;
	public int id;
	public String unit;
	public int drunkenness;
	
	public Drink (String name, MemorySection drinkData) throws BadConfigException {
		this.name = name;
		Map<String,Object> data = drinkData.getValues(false);
		if (!data.containsKey("id")
				|| !data.containsKey("unit")
				|| !data.containsKey("drunkenness")) {
			throw new BadConfigException("Missing value in given config segment!");
		}
		this.id = Integer.parseInt(data.get("id").toString());
		this.unit = data.get("unit").toString();
		this.drunkenness = Integer.parseInt(data.get("drunkenness").toString());
	}
}
