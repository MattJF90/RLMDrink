package com.rlminecraft.RLMDrink;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rlminecraft.RLMDrink.Exceptions.InvalidPotionEffectTypeException;

public class Effect {
	public PotionEffectType name;
	public int duration;
	public int amplitude;
	
	private int tps = 20;
	
	public Effect(String name) throws InvalidPotionEffectTypeException {
		this.name = StringToPotionEffectType(name);
		this.duration = 0;
		this.amplitude = 1;
	}
	
	public PotionEffect toPotionEffect() {
		PotionEffect effect = new PotionEffect(this.name, this.duration * tps, this.amplitude);
		return effect;
	}
	
	private PotionEffectType StringToPotionEffectType (String type) throws InvalidPotionEffectTypeException {
		if (type.startsWith("BLINDNESS")) return PotionEffectType.BLINDNESS;
		if (type.startsWith("CONFUSION")) return PotionEffectType.CONFUSION;
		if (type.startsWith("DAMAGE_RESISTANCE")) return PotionEffectType.DAMAGE_RESISTANCE;
		if (type.startsWith("FAST_DIGGING")) return PotionEffectType.FAST_DIGGING;
		if (type.startsWith("FIRE_RESISTANCE")) return PotionEffectType.FIRE_RESISTANCE;
		if (type.startsWith("HARM")) return PotionEffectType.HARM;
		if (type.startsWith("HEAL")) return PotionEffectType.HEAL;
		if (type.startsWith("HUNGER")) return PotionEffectType.HUNGER;
		if (type.startsWith("INCREASE_DAMAGE")) return PotionEffectType.INCREASE_DAMAGE;
		//if (type.startsWith("INVISIBILITY")) return PotionEffectType.INVISIBILITY;
		if (type.startsWith("JUMP")) return PotionEffectType.JUMP;
		//if (type.startsWith("NIGHT_VISION")) return PotionEffectType.NIGHT_VISION;
		if (type.startsWith("POISON")) return PotionEffectType.POISON;
		if (type.startsWith("REGENERATION")) return PotionEffectType.REGENERATION;
		if (type.startsWith("SLOW_DIGGING")) return PotionEffectType.SLOW_DIGGING;
		if (type.startsWith("SLOW")) return PotionEffectType.SLOW;
		if (type.startsWith("SPEED")) return PotionEffectType.SPEED;
		if (type.startsWith("WATER_BREATHING")) return PotionEffectType.WATER_BREATHING;
		if (type.startsWith("WEAKNESS")) return PotionEffectType.WEAKNESS;
		throw new InvalidPotionEffectTypeException("Invalid potion type specified!");
	}
}
