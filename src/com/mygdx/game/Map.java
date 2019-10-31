package com.mygdx.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Map {
	
	String name;
	Texture img;
	int density;
	List<Entity> entities = new ArrayList<Entity>(); // What the map is currently has
	List<Entity> spawnable = new ArrayList<Entity>(); // What the map can spawn
	sortY sorter;
	Random rand;
	Player player;
	
	Music background;
	public Map(MapType mapType, Player player) {
		sorter = new sortY();
		rand = new Random();
		this.player = player;
		switch (mapType) {
		case Base:
			name = "Base map";
			density = 600;
			img = new Texture("grassland.png");
			spawnable.add(new Tree(EntityType.Tree1));
			spawnable.add(new Tree(EntityType.Tree2));
			spawnable.add(new Tree(EntityType.Tree3));
			spawnable.add(new Entity(EntityType.Berry));
			spawnable.add(new Creature(EntityType.Boar));
			background = Gdx.audio.newMusic(Gdx.files.internal("birds.mp3"));
			background.setLooping(true);
			background.setVolume(5.0f);
			background.play();

			
			break;
		default:
			break;
		}
		
		generateEntities();
		
	}
	
	public void generateEntities() {
		for (int i = 0; i < density; i++) {
			for (Entity entity: spawnable) {
				if (rand.nextInt(100) < entity.spawnRate) {
					Entity randomSpawn = entity.copyEntity();
					setPosition(randomSpawn);
					entities.add(randomSpawn);
				}
			}
		}
	}
	
	public void setPosition(Entity entity) {
		entity.setPosition(rand.nextInt(img.getWidth()), rand.nextInt(img.getHeight()));
		entity.collider.setPosition(entity.x + entity.collider.x, 
				entity.y + entity.collider.y);
		for (Entity check: entities) {
			if (check.collider.overlaps(entity.collider)) {
				setPosition(entity);
			}
		}
	}
	
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}
	
	public void update() {
		List<Entity> deathList = new ArrayList<Entity>();
		for (Entity entity: entities) {
			if (entity.update(entities, player)) {
				deathList.add(entity);
			}
		}
		
		for (Entity dead: deathList) {
			entities.remove(dead);
		}
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(img, 0, 0);
		entities.sort(sorter);
		for (Entity entity: entities) {
			entity.draw(batch);
		}
	}
	
}

class sortY implements Comparator<Entity> {

	@Override
	public int compare(Entity a, Entity b) {
		return a.y >= b.y ? -1 : 1;
	}
	
}
