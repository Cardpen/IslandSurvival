package com.mygdx.game;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Creature extends Entity {

	double health;
	double maxHealth;
	double atk;
	double stamina;
	double maxStamina;
	double speed;
	double maxSpeed;
	double range;
	double atkRange;

	boolean moveLeft = false;
	boolean moveRight = false;
	boolean moveUp = false;
	boolean moveDown = false;

	Animation<TextureRegion> curAnimation;
	float stageTime = 0f;
	Animation<TextureRegion> walkLeft;
	Animation<TextureRegion> walkRight;

	Animation<TextureRegion> idleLeft;
	Animation<TextureRegion> idleRight;
	
	Animation<TextureRegion> atkLeft;
	Animation<TextureRegion> atkRight;
	
	Animation<TextureRegion> hurtLeft;
	Animation<TextureRegion> hurtRight;
	
	String animationSheet;
	
	Map curLocation;
	long lastAction;
	Random rand;
	double dx;
	double dy;

	public Creature(EntityType eType) {
		super(eType);
		rand = new Random();

		switch (eType) {
		case Boar:
			maxHealth = 30;
			health = maxHealth;
			atk = 5;
			maxStamina = 20;
			stamina = maxStamina;
			maxSpeed = 10.5;
			speed = maxSpeed;
			range = 150;
			atkRange = 50;
			animationSheet = "pigAnimation.png";
			Texture animationSheet = new Texture("pigAnimation.png");
			createAnimations(animationSheet);

			break;
		}
	}
	
	public Creature(Creature creature) {
		rand = new Random();
		this.name = creature.name;
		this.x = creature.x;
		this.y = creature.y;
		this.spawnRate = creature.spawnRate;
		this.imgFileName = creature.imgFileName;
		this.img = new Texture(imgFileName);
		this.collider = new Rectangle(creature.collider);
		this.entityID = creature.entityID;
		this.health = creature.health;
		this.maxHealth = creature.maxHealth;
		this.stamina = creature.stamina;
		this.maxStamina = creature.maxStamina;
		this.speed = creature.speed;
		this.maxSpeed  = creature.maxSpeed;
		this.atkRange = creature.atkRange;
		this.range = creature.range;
		this.animationSheet = creature.animationSheet;
		createAnimations(new Texture(animationSheet));
		
	}
	
	void createAnimations(Texture animationSheet) {
		TextureRegion[][] walkFrames = TextureRegion.split(animationSheet, animationSheet.getWidth() / 2,
				animationSheet.getHeight() / 5);

		walkLeft = new Animation<TextureRegion>(0.3f, walkFrames[1]);
		walkRight = new Animation<TextureRegion>(0.3f, walkFrames[0]);

		TextureRegion[] idleLeft = walkFrames[1];
		TextureRegion[] idleRight = walkFrames[0];

		this.idleLeft = new Animation<TextureRegion>(0.55f, idleLeft);
		this.idleRight = new Animation<TextureRegion>(0.55f, idleRight);
		
		this.atkLeft = new Animation<TextureRegion>(0.55f, walkFrames[3]);
		this.atkRight = new Animation<TextureRegion>(0.55f, walkFrames[2]);
		
		this.hurtLeft = new Animation<TextureRegion>(0.55f, walkFrames[4][0]);
		this.hurtRight = new Animation<TextureRegion>(0.55f, walkFrames[4][1]);
		
		curAnimation = this.idleRight;
	}

	public boolean update(List<Entity> entities, Player player) {
		double distance = distanceFrom(player);
		if (distance <= range) {
			aggroBehavior(distance, player);
		} else {
			neutralBehavior();
		}
		
		collider.x += dx;
		for (Entity entity : entities) {
			if (entity == this) {
				continue;
			}
			if (entity.collider.overlaps(collider)) {
				collider.x -= dx;
				dx = 0;
				break;
			}
		}

		collider.y += dy;
		for (Entity entity : entities) {
			if (entity == this) {
				continue;
			}
			if (entity.collider.overlaps(collider)) {
				dy = 0;
				collider.y -= dy;
			}
		}
		
		x += dx;
		y += dy;
		collider.setPosition(x, y);
		
		return trigger(player);

	}

	private void aggroBehavior(double distance, Player player) {
		dx = ((player.collider.x - collider.x) / (distance)) * 70 * Gdx.graphics.getDeltaTime();
		dy = ((player.collider.y - collider.y) / (distance)) * 70 * Gdx.graphics.getDeltaTime();
		if (curAnimation.getAnimationDuration() > 0.5f) {
			curAnimation = dx > 0 ? walkRight : walkLeft;
		}
		if (distance < atkRange && System.currentTimeMillis() > lastAction + 2000) {
			lastAction = System.currentTimeMillis();
			player.health -= 7;
			curAnimation = dx > 0 ? atkRight : atkLeft;
		}
	}

	private void neutralBehavior() {
		if (System.currentTimeMillis() > lastAction + rand.nextInt(4000) + 1000) {
			lastAction = System.currentTimeMillis();
			switch (rand.nextInt(2)) {
			case 0:
				dx = 0;
				dy = 0;
				curAnimation = idleRight;
				break;
			case 1:
				dx = rand.nextInt(2) * 130 * Gdx.graphics.getDeltaTime();
				dy = rand.nextInt(2) * 130 * Gdx.graphics.getDeltaTime();
				curAnimation = dx > 0 ? walkRight : walkLeft;
			}
		}
	}
	
	public void draw(SpriteBatch batch) {
		stageTime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = curAnimation.getKeyFrame(stageTime, true);
		batch.draw(currentFrame, x, y);
	}

	public Creature copyEntity() {
		return new Creature(this);
	}

	public boolean trigger(Player player) {
		if (player.triggering && distanceFrom(player) < player.range) {
			
			// Change boar animation to hurt animation
			curAnimation = dx > 0 ? hurtRight : hurtLeft;
					
			// gives player the item
			health -= 10 * Gdx.graphics.getDeltaTime();
		}
		
		return health <= 0;
	};

}
