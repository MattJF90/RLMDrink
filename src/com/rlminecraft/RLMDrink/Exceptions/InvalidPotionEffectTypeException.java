package com.rlminecraft.RLMDrink.Exceptions;

public class InvalidPotionEffectTypeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1261548121434084241L;
	public InvalidPotionEffectTypeException() {}
	public InvalidPotionEffectTypeException(String msg) {
		super(msg);
	}
}
