package com.mygdx.game;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Tree extends Entity{
	
	double demolitionValue;
	Texture imgNormal;
	Texture imgDamaged;
	String imgFileNameDamaged;
	Texture imgVeryDamaged;
	String imgFileNameVeryDamaged;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(demolitionValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((imgDamaged == null) ? 0 : imgDamaged.hashCode());
		result = prime * result + ((imgFileNameDamaged == null) ? 0 : imgFileNameDamaged.hashCode());
		result = prime * result + ((imgFileNameVeryDamaged == null) ? 0 : imgFileNameVeryDamaged.hashCode());
		result = prime * result + ((imgNormal == null) ? 0 : imgNormal.hashCode());
		result = prime * result + ((imgVeryDamaged == null) ? 0 : imgVeryDamaged.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tree other = (Tree) obj;
		if (Double.doubleToLongBits(demolitionValue) != Double.doubleToLongBits(other.demolitionValue))
			return false;
		if (imgDamaged == null) {
			if (other.imgDamaged != null)
				return false;
		} else if (!imgDamaged.equals(other.imgDamaged))
			return false;
		if (imgFileNameDamaged == null) {
			if (other.imgFileNameDamaged != null)
				return false;
		} else if (!imgFileNameDamaged.equals(other.imgFileNameDamaged))
			return false;
		if (imgFileNameVeryDamaged == null) {
			if (other.imgFileNameVeryDamaged != null)
				return false;
		} else if (!imgFileNameVeryDamaged.equals(other.imgFileNameVeryDamaged))
			return false;
		if (imgNormal == null) {
			if (other.imgNormal != null)
				return false;
		} else if (!imgNormal.equals(other.imgNormal))
			return false;
		if (imgVeryDamaged == null) {
			if (other.imgVeryDamaged != null)
				return false;
		} else if (!imgVeryDamaged.equals(other.imgVeryDamaged))
			return false;
		return true;
	}

	public Tree(EntityType eType) {
		x = 0;
		y = 0;
		demolitionValue = 100;
		switch (eType) {
		case Tree1:
			name = "Tree 1";
			imgFileName = "Tree1.png";
			imgNormal = new Texture(imgFileName);
			imgFileNameDamaged = "Tree1_Damaged.png";
			imgFileNameVeryDamaged = "Tree1_VeryDamaged.png";
			imgDamaged = new Texture(imgFileNameDamaged);
			imgVeryDamaged = new Texture(imgFileNameVeryDamaged);
			img = imgNormal;
			spawnRate = 10;
			collider = new Rectangle(104, 0, 24, 10);
			break;
		case Tree2:
			name = "Tree 2";
			imgFileName = "Tree2.png";
			imgNormal = new Texture(imgFileName);
			imgFileNameDamaged = "Tree1_Damaged.png";
			imgFileNameVeryDamaged = "Tree1_VeryDamaged.png";
			imgDamaged = new Texture(imgFileNameDamaged);
			imgVeryDamaged = new Texture(imgFileNameVeryDamaged);
			img = imgNormal;
			spawnRate = 10;
			collider = new Rectangle(86, 0, 16, 7);
			break;
		case Tree3:
			name = "Tree 3";
			imgFileName = "Tree3.png";
			imgNormal = new Texture(imgFileName);
			imgFileNameDamaged = "Tree1_Damaged.png";
			imgFileNameVeryDamaged = "Tree1_VeryDamaged.png";
			imgDamaged = new Texture(imgFileNameDamaged);
			imgVeryDamaged = new Texture(imgFileNameVeryDamaged);
			img = imgNormal;
			spawnRate = 10;
			collider = new Rectangle(88, 0, 18, 10);
			break;
		default:
			break;
		
		}
	}
	
	public Tree(Tree tree) {
		this.name = tree.name;
		this.x = tree.x;
		this.y = tree.y;
		this.spawnRate = tree.spawnRate;
		this.imgFileName = tree.imgFileName;
		this.imgNormal = new Texture(imgFileName);
		this.collider = new Rectangle(tree.collider);
		this.imgDamaged = new Texture(tree.imgFileNameDamaged);
		this.imgFileNameDamaged = tree.imgFileNameDamaged;
		this.imgVeryDamaged = new Texture(tree.imgFileNameVeryDamaged);
		this.imgFileNameVeryDamaged = tree.imgFileNameVeryDamaged;
		this.imgNormal = tree.imgNormal;
		this.demolitionValue = tree.demolitionValue;
		this.img = imgNormal;
		correctState();
		
	}
	
	@Override
	public boolean update(List<Entity> entities, Player player) {
		Rectangle transparancyCheck = new Rectangle(x, collider.y, img.getWidth(), img.getHeight() - collider.y + y);
		if (transparancyCheck.overlaps(player.collider)) {
			transparant = true;
		}
		if (transparant && !transparancyCheck.overlaps(player.collider)){
			transparant = false;
		}
		
		return trigger(player);
	}
	
	@Override
	public boolean trigger(Player player) {
		
		if (player.triggering && distanceFrom(player) < player.range) {
			demolitionValue -= 20 * Gdx.graphics.getDeltaTime();
			if (correctState()) {
				// give item to player
				Item piece = player.inventoryHas("Log");
				if (piece == null) {
					player.inventories.add(new Craftable(ItemType.LOG, 2));
				} else {
					piece.stackSize += 2;
				}
				System.out.println("Add 2 Logs!");
				return true;}
		}
		return false;
	}
	
	public boolean correctState() {
		if (demolitionValue < 55 && img == imgNormal) {
			img = imgDamaged;
		} else if (demolitionValue < 25 && img == imgDamaged) {
			img = imgVeryDamaged;
		} else if (demolitionValue <= 0) {
			
			return true;
		}
		return false;
	}
	

	public Tree copyEntity() {
		return new Tree(this);
	}
}
