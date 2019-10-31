package com.mygdx.game;

public class Craftable extends Item {
	
	
	public Craftable(String name, String des, int stackSize, String imgName)
	{
		super(name, des, stackSize, imgName);
	}
	
	public Craftable(ItemType itemType, int stackSize) 
	{
		super(itemType, stackSize);
		
		switch(itemType)
		{
			case LOG:
				
				break;
		}
	}
}
