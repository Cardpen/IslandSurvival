package com.mygdx.game;

import java.util.ArrayList;

public class Consumable extends Item{
	public String effect;
	public boolean[] multipliers;
	public int[] effectValues;
	
	public enum Effect
	{
		HEALTH, MAXHEALTH, HUNGER, MAXHUNGER, THIRST, MAXTHIRST, STAMINA, MAXSTAMINA, SPEED, MAXSPEED, LUCK, MAXLUCK
	}
	
	public Consumable(String name, String des, String effect, int stackSize, String imgName
			, boolean[] multipliers, int[] effectVal)
	{
		super(name, des, stackSize, imgName);
		this.effect = effect;
		this.effectValues = effectVal; // size is enum Effect length
		this.multipliers = multipliers;
	}
	
	public Consumable(ItemType itemType, int stackSize) 
	{
		super(itemType, stackSize);
		multipliers = new boolean[Effect.values().length];
		effectValues = new int[Effect.values().length];
		
		switch(itemType)
		{
			case BERRY:
				effect = "-hunger -thirst";
				
				// Set Multipliers for berry
				for (int i = 0; i < multipliers.length; i++)
					multipliers[i] = false;
				
				effectValues[Effect.HUNGER.ordinal()] = 10;
				effectValues[Effect.THIRST.ordinal()] = 10;
				break;
		case LOG:
			break;
		case MEAT:
			break;
		default:
			break;
		}
	}
	
	
}
