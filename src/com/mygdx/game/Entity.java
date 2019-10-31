package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Entity {
	boolean transparant = false;
	public String name;
	public float x;
	public float y;
	public Texture img;
	public double spawnRate;
	String imgFileName;
	Rectangle collider;
	EntityType entityID;

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public Entity() {
	}

	public Entity(EntityType eType) {
		x = 0;
		y = 0;
		switch (eType) {
		case Berry:
			entityID = eType;
			name = "Berry red";
			imgFileName = "BerryBush.png";
			img = new Texture("BerryBush.png");
			spawnRate = 3;
			collider = new Rectangle(0, 0, img.getWidth(), img.getHeight());
			break;
		case Boar:
			entityID = eType;
			name = "Boar";
			imgFileName = "pigRight.png";
			img = new Texture(imgFileName);
			spawnRate = 2;
			collider = new Rectangle(0, 0, img.getWidth(), img.getHeight());
			break;
		default:
			break;
		}

	}

	public Entity(String name, String imgFileName) {
		this.imgFileName = imgFileName;
		this.name = name;
		img = new Texture(Gdx.files.internal(imgFileName));
		spawnRate = 0;
		x = y = 0L;
		collider = new Rectangle(0, 0, img.getWidth(), img.getHeight());
	}

	public Entity(String name, String imgFileName, double spawnRate) {
		this(name, imgFileName);
		this.spawnRate = spawnRate;
	}

	public Entity(String name, String imgFileName, double spawnRate, float x, float y) {
		this(name, imgFileName, spawnRate);
		this.x = x;
		this.y = y;
	}

	public Entity(String name, String imgFileName, double spawnRate, float x, float y, Rectangle rect) {
		this(name, imgFileName, spawnRate, x, y);
		collider = rect;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collider == null) ? 0 : collider.hashCode());
		result = prime * result + ((img == null) ? 0 : img.hashCode());
		result = prime * result + ((imgFileName == null) ? 0 : imgFileName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(spawnRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (transparant ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Entity other = (Entity) obj;
		if (collider == null) {
			if (other.collider != null)
				return false;
		} else if (!collider.equals(other.collider))
			return false;
		if (img == null) {
			if (other.img != null)
				return false;
		} else if (!img.equals(other.img))
			return false;
		if (imgFileName == null) {
			if (other.imgFileName != null)
				return false;
		} else if (!imgFileName.equals(other.imgFileName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(spawnRate) != Double.doubleToLongBits(other.spawnRate))
			return false;
		if (transparant != other.transparant)
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	public Entity(Entity entity) {
		this.name = entity.name;
		this.x = entity.x;
		this.y = entity.y;
		this.spawnRate = entity.spawnRate;
		this.imgFileName = entity.imgFileName;
		this.img = new Texture(imgFileName);
		this.collider = new Rectangle(entity.collider);
		this.entityID = entity.entityID;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	public void draw(SpriteBatch sb) {
		if (transparant) {
			sb.setColor(1, 1, 1, 0.5f);
			sb.draw(img, x, y);
			sb.setColor(1, 1, 1, 1.0f);
		} else {
			sb.draw(img, x, y);
		}
	}

	public boolean update(List<Entity> entities, Player player) {
		Rectangle transparancyCheck = new Rectangle(x, collider.y + player.img.getHeight() , img.getWidth(), img.getHeight() - collider.y + y - player.img.getHeight());
		if (transparancyCheck.overlaps(player.collider)) {
			transparant = true;
		}
		if (transparant && !transparancyCheck.overlaps(player.collider)) {
			transparant = false;
		}

		return trigger(player);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public double distanceFrom(Entity entity) {

		return Math.hypot(entity.collider.y - collider.y,
				(entity.collider.x + entity.collider.width) / 2 - (collider.x + collider.width) / 2);
	}

	public boolean trigger(Player player) {
		if (player.triggering && distanceFrom(player) < player.range) {
			// gives player the item
			System.out.println(entityID + "");
			if (entityID == EntityType.Berry) {
				Item piece = player.inventoryHas("Berry");
				if (piece == null) {
					player.inventories.add(new Consumable(ItemType.BERRY, 3));
				} else {
					piece.stackSize += 3;
				}
				System.out.println("Add 3 berries!");
			}
			else if (entityID == EntityType.Boar)
			{
				System.out.println("Punching boar!");
			}
			return true;
		}
		return false;
	};

	public Entity copyEntity() {
		return new Entity(this);
	}
}
