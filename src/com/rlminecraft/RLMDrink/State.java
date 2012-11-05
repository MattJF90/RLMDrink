package com.rlminecraft.RLMDrink;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.MemorySection;

import com.rlminecraft.RLMDrink.Exceptions.BadConfigException;
import com.rlminecraft.RLMDrink.Exceptions.InvalidPotionEffectTypeException;

public class State {
	public String name;
	public int start;
	public int finish;
	public boolean death;
	List<Effect> effects;
	
	public State () {
		this.name = "NULL";
		this.start = 0;
		this.finish = 0;
		this.death = false;
	}
	
	public State (String name, MemorySection stateData) throws BadConfigException{
		this.name = name;
		this.effects = new LinkedList<Effect>();
		Map<String,Object> data = stateData.getValues(false);
		if (!data.containsKey("startsAt")
				|| !data.containsKey("endsAt")
				|| !data.containsKey("death")) {
			throw new BadConfigException("Missing value in given config segment!");
		}
		this.start = Integer.parseInt(data.get("startsAt").toString());
		this.finish = Integer.parseInt(data.get("endsAt").toString());
		this.death = Boolean.parseBoolean(data.get("death").toString());
		if (!data.containsKey("effects")
				&& ! this.death) {
			throw new BadConfigException("Missing value in given config segment!");
		}
		// Import effects
		if (!this.death) {
			Map<String,Object> effectMap = ((MemorySection) data.get("effects")).getValues(false);
			for (int i = 0; i < effectMap.size(); i++) {
				Effect effect;
				try {
					effect = new Effect(effectMap.keySet().toArray()[i].toString());
				} catch (InvalidPotionEffectTypeException e) {
					throw new BadConfigException("Invalid potion effect type! (" + e.getCause().toString() + ")");
				}
				if (effect.name != null) {
					Map<String,Object> singleEffectMap = ((MemorySection) effectMap.get(effect.name.getName().toString())).getValues(false);
					if (singleEffectMap.containsKey("duration")
							&& singleEffectMap.containsKey("amplitude")) {
						effect.duration = Integer.parseInt(singleEffectMap.get("duration").toString());
						effect.amplitude = Integer.parseInt(singleEffectMap.get("amplitude").toString());
						this.effects.add(effect);
					}
				}
			}
		}
	}
	
	public boolean inRange (int level) {
		return (this.start <= level && level < this.finish);
	}
}
