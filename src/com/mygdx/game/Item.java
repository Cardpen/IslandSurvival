package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;

public abstract class Item {
	public String name;
	public String description;
	public int stackSize;
	public Texture img;
	ItemType iType;
	
	// Multi args constructor
	public Item(String name, String des, int stackSize, String imgName)
	{
		this.name = name;
		this.description = des;	
		this.stackSize = stackSize;
		img = new Texture(imgName);
	}
	
	public Item(ItemType itemType, int stackSize)
	{
		switch(itemType)
		{
			case BERRY:
				iType = itemType;
				name = "Berry";
				description = "A small red juicy fruit.";
				this.stackSize = stackSize;
				img = new Texture("berry.png");
				break;
			case MEAT:
				name = "Meat";
				description = "A fresh raw meat.";
				this.stackSize = stackSize;
//				img = new Texture("meat.png");
				break;
			case LOG:
				name = "Log";
				description = "A piece of wood.";
				this.stackSize = stackSize;
				img = new Texture("log.png");
				break;
			default:
				// something is wrong
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((iType == null) ? 0 : iType.hashCode());
		result = prime * result + ((img == null) ? 0 : img.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (iType != other.iType)
			return false;
		if (img == null) {
			if (other.img != null)
				return false;
		} else if (!img.equals(other.img))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
