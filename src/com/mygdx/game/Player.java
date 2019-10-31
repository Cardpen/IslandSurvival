package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Consumable.Effect;

public class Player extends Entity {

	double health;
	double maxHealth;
	double hunger;
	double maxHunger;
	double thirst;
	double maxThirst;
	double stamina;
	double maxStamina;
	double speed;
	double maxSpeed;
	double luck;
	double maxLuck;
	double range;
	List<Item> inventories;
	public final int INVENTORY_SPACES = 7;

	boolean moveLeft = false;
	boolean moveRight = false;
	boolean moveUp = false;
	boolean moveDown = false;
	boolean triggering = false;

	Animation<TextureRegion> curAnimation;
	float stageTime = 0f;
	Animation<TextureRegion> walkUp;
	Animation<TextureRegion> walkDown;
	Animation<TextureRegion> walkLeft;
	Animation<TextureRegion> walkRight;

	Animation<TextureRegion> punchUp;
	Animation<TextureRegion> punchDown;
	Animation<TextureRegion> punchLeft;
	Animation<TextureRegion> punchRight;

	Animation<TextureRegion> idleUp;
	Animation<TextureRegion> idleDown;
	Animation<TextureRegion> idleLeft;
	Animation<TextureRegion> idleRight;

	Stack<Integer> pressBuffer = new Stack<Integer>();
	Stack<Integer> releaseBuffer = new Stack<Integer>();

	Map curLocation;

	Sound walking;
	Sound running;
	Sound punching;
	
	public Player() {
		x = 1500;
		y = 500;
		walking = Gdx.audio.newSound(Gdx.files.internal("walking.wav"));
		running = Gdx.audio.newSound(Gdx.files.internal("running.mp3"));
		punching = Gdx.audio.newSound(Gdx.files.internal("punch.wav"));
		
		Texture animationSheet = new Texture("Player_Animations.png");
		createAnimations(animationSheet);
		img = new Texture("player.png");

		collider = new Rectangle(x, y, img.getWidth(), 10);
		range = 30;
		setStats();
		inventories = new ArrayList<Item>(INVENTORY_SPACES);
	}

	public void setStats() {
		health = 100;
		maxHealth = 100;
		stamina = 100;
		maxStamina = 100;
		hunger = 100;
		maxHunger = 100;
		thirst = 100;
		maxThirst = 100;
		speed = 10;
		maxSpeed = 10;
	}

	void createAnimations(Texture animationSheet) {
		TextureRegion[][] walkFrames = TextureRegion.split(animationSheet, animationSheet.getWidth() / 4,
				animationSheet.getHeight() / 4);

		TextureRegion[] downPunchFrames = { walkFrames[0][1], walkFrames[0][3] };
		TextureRegion[] upPunchFrames = { walkFrames[3][1], walkFrames[3][3] };
		TextureRegion[] leftPunchFrames = { walkFrames[2][1], walkFrames[2][3] };
		TextureRegion[] rightPunchFrames = { walkFrames[1][1], walkFrames[1][3] };

		walkFrames[3][0].setRegionY((walkFrames[3][1].getRegionY() + 5));
		walkFrames[3][2].setRegionY((walkFrames[3][3].getRegionY() + 5));
		walkFrames[3][1].setRegionY((walkFrames[3][1].getRegionY() + 9));
		walkFrames[3][3].setRegionY((walkFrames[3][3].getRegionY() + 9));

		walkFrames[2][0].setRegionY((walkFrames[2][1].getRegionY() + 5));
		walkFrames[2][2].setRegionY((walkFrames[2][3].getRegionY() + 5));
		walkFrames[2][1].setRegionY((walkFrames[2][1].getRegionY() + 9));
		walkFrames[2][3].setRegionY((walkFrames[2][3].getRegionY() + 9));

		walkUp = new Animation<TextureRegion>(0.25f, walkFrames[3]);
		walkDown = new Animation<TextureRegion>(0.25f, walkFrames[0]);
		walkLeft = new Animation<TextureRegion>(0.25f, walkFrames[2]);
		walkRight = new Animation<TextureRegion>(0.25f, walkFrames[1]);

		punchDown = new Animation<TextureRegion>(0.25f, downPunchFrames);
		punchUp = new Animation<TextureRegion>(0.25f, upPunchFrames);
		punchLeft = new Animation<TextureRegion>(0.25f, leftPunchFrames);
		punchRight = new Animation<TextureRegion>(0.25f, rightPunchFrames);

		TextureRegion[] idleUp = { walkFrames[3][0] };
		TextureRegion[] idleDown = { walkFrames[0][0] };
		TextureRegion[] idleLeft = { walkFrames[2][0] };
		TextureRegion[] idleRight = { walkFrames[1][0] };

		this.idleUp = new Animation<TextureRegion>(0.25f, idleUp);
		this.idleDown = new Animation<TextureRegion>(0.25f, idleDown);
		this.idleLeft = new Animation<TextureRegion>(0.25f, idleLeft);
		this.idleRight = new Animation<TextureRegion>(0.25f, idleRight);

		curAnimation = this.idleDown;
	}

	public boolean update(List<Entity> entities, Player player) {
		keyInput();
		processInput(entities);

		if (curAnimation == idleUp || curAnimation == idleDown || curAnimation == idleLeft
				|| curAnimation == idleRight) {
			stamina += 6 * Gdx.graphics.getDeltaTime();
			hunger -= 0.1 * Gdx.graphics.getDeltaTime();
			thirst -= 0.2 * Gdx.graphics.getDeltaTime();
			if (stamina > maxStamina) {
				stamina = maxStamina;
			}
		} else if (triggering) {
			stamina -= 8 * Gdx.graphics.getDeltaTime();
			if (stamina < 0) {
				triggering = false;
			}
			hunger -= 1.5 * Gdx.graphics.getDeltaTime();
			thirst -= 2.2 * Gdx.graphics.getDeltaTime();
		}

		hunger = hunger < 0 ? 0 : hunger;
		thirst = thirst < 0 ? 0 : thirst;
		stamina = stamina < 0 ? 0 : stamina;

		return false;
	}

	public void keyInput() {
		int keycode;
		while (!pressBuffer.isEmpty()) {
			keycode = pressBuffer.pop();
			if (keycode == Input.Keys.A) {
				moveLeft = true;
				if (speed <= maxSpeed) {
					walking.stop();
					walking.loop();
				} else {
					running.stop();
					running.loop();
				}
			} else if (keycode == Input.Keys.D) {
				moveRight = true;
				if (speed <= maxSpeed) {
					walking.stop();
					walking.loop();
				} else {
					running.stop();
					running.loop();
				}
			}
			
			if (keycode == Input.Keys.W) {
				moveUp = true;
				if (speed <= maxSpeed) {
					walking.stop();
					walking.loop();
				} else {
					running.stop();
					running.loop();
				}
			} else if (keycode == Input.Keys.S) {
				moveDown = true;
				if (speed <= maxSpeed) {
					walking.stop();
					walking.loop();
				} else {
					running.stop();
					running.loop();
				}
			}

			if (keycode == Input.Keys.E) {
				triggering = true;
				punching.loop(3.0f);
				if (curAnimation == idleDown || curAnimation == walkDown) {
					curAnimation = punchDown;
				}
				if (curAnimation == idleLeft || curAnimation == walkLeft) {
					curAnimation = punchLeft;
				}
				if (curAnimation == idleUp || curAnimation == walkUp) {
					curAnimation = punchUp;
				}
				if (curAnimation == idleRight || curAnimation == walkRight) {
					curAnimation = punchRight;
				}
			}

			if (keycode == Input.Keys.SHIFT_LEFT) {
				speed *= 1.7;
			}
		}

		while (!releaseBuffer.isEmpty()) {
			keycode = releaseBuffer.pop();

			if (keycode == Input.Keys.A) {
				moveLeft = false;
				if (!moveUp && !moveDown) {
					curAnimation = idleLeft;
					walking.stop();
					running.stop();
				}
			} else if (keycode == Input.Keys.D) {
				moveRight = false;
				if (!moveUp && !moveDown) {
					curAnimation = idleRight;
					walking.stop();
					running.stop();
				}
			}

			if (keycode == Input.Keys.W) {
				moveUp = false;
				if (!moveLeft && !moveRight) {
					curAnimation = idleUp;
					walking.stop();
					running.stop();
				}
			} else if (keycode == Input.Keys.S) {
				moveDown = false;
				if (!moveLeft && !moveRight) {
					curAnimation = idleDown;
					walking.stop();
					running.stop();
				}
			}

			if (keycode == Input.Keys.E) {
				triggering = false;
				punching.stop();
				if (curAnimation == punchDown || curAnimation == walkDown) {
					curAnimation = idleDown;
				}
				if (curAnimation == punchLeft || curAnimation == walkLeft) {
					curAnimation = idleLeft;
				}
				if (curAnimation == punchUp || curAnimation == walkUp) {
					curAnimation = idleUp;
				}
				if (curAnimation == punchRight || curAnimation == walkRight) {
					curAnimation = idleRight;
				}
			}

			if (keycode == Input.Keys.T) {
				hunger -= 10;
				thirst -= 10;
			}

			if (keycode == Input.Keys.Y) {
				hunger += 10;
				thirst += 10;

			}

			if (keycode == Input.Keys.SPACE) {
				y = moveUp ? y + 100 : moveDown ? y - 100 : y;
				x = moveRight ? x + 100 : moveLeft ? x - 100 : x;
			} else if (keycode == Input.Keys.SHIFT_LEFT) {
				speed = maxSpeed;
			}

			switch (keycode) {
			case Input.Keys.NUM_1:
				useItem(1);
				break;
			case Input.Keys.NUM_2:
				useItem(2);
				break;
			case Input.Keys.NUM_3:
				useItem(3);
				break;
			case Input.Keys.NUM_4:
				useItem(4);
				break;
			case Input.Keys.NUM_5:
				useItem(5);
				break;
			case Input.Keys.NUM_6:
				useItem(6);
				break;
			case Input.Keys.NUM_7:
				useItem(7);
				break;
			}
		}
	}

	public void processInput(List<Entity> entities) {
		float dx = 0;
		float dy = 0;
		double ratio = (speed / 15.0) * Gdx.graphics.getDeltaTime();
		int move = (int) (200 * ratio);
		if (moveLeft) {
			dx = -move;
			hunger -= ratio * 0.5;
			thirst -= ratio * 1;
			stamina -= speed <= maxSpeed ? ratio * -2 : ratio * 8;
			curAnimation = walkLeft;
			
		} else if (moveRight) {
			dx = move;
			hunger -= ratio * 0.5;
			thirst -= ratio * 1;
			stamina -= speed <= maxSpeed ? ratio * -2 : ratio * 8;
			curAnimation = walkRight;
		}

		if (moveUp) {
			dy = (float) (move * .75);
			hunger -= ratio * 0.5;
			thirst -= ratio * 1;
			stamina -= speed <= maxSpeed ? ratio * -2 : ratio * 8;
			curAnimation = walkUp;
		} else if (moveDown) {
			dy = (float) (-move * .75);
			hunger -= ratio * 0.5;
			thirst -= ratio * 1;
			stamina -= speed <= maxSpeed ? ratio * -2 : ratio * 8;
			curAnimation = walkDown;
		}

		hunger = hunger < 0 ? 0 : hunger;
		thirst = thirst < 0 ? 0 : thirst;
		stamina = stamina < 0 ? 0 : stamina;
		stamina = stamina > maxStamina ? maxStamina : stamina;

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

		dx = stamina <= 0 ? 0 : dx;
		dy = stamina <= 0 ? 0 : dy;
		Rectangle bounds = new Rectangle(360, 240, curLocation.img.getWidth() - 720, curLocation.img.getHeight() - 500);
		;
		if (dx != 0 && !bounds.contains(collider)) {
			dx = 0;
		}

		if (dy != 0 && !bounds.contains(collider)) {
			dy = 0;
		}

		x += dx;
		y += dy;
		collider.setPosition(x, y);

	}

	public void draw(SpriteBatch batch) {
		stageTime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = curAnimation.getKeyFrame(stageTime, true);
		batch.draw(currentFrame, x, y);
	}

	public void useItem(int itemslot) {
		if (inventories.size() >= itemslot) {
			Item usingItem = inventories.get(itemslot - 1);
			if (usingItem != null) {
				System.out.println(usingItem.stackSize + " " + usingItem.name);

				if (usingItem instanceof Consumable) {
					if (usingItem.stackSize > 0) {
						System.out.println("Consumed 1 " + usingItem.name);
						usingItem.stackSize--;
						int healthEffect = ((Consumable) usingItem).effectValues[Effect.HEALTH.ordinal()];
						int hungerEffect = ((Consumable) usingItem).effectValues[Effect.HUNGER.ordinal()];
						int thirstEffect = ((Consumable) usingItem).effectValues[Effect.THIRST.ordinal()];

						health = health + healthEffect > maxHealth ? maxHealth : health + healthEffect;
						hunger = hunger + hungerEffect > maxHunger ? maxHunger : hunger + hungerEffect;
						thirst = thirst + thirstEffect > maxThirst ? maxThirst : thirst + thirstEffect;

						if (usingItem.stackSize <= 0)
							inventories.remove(itemslot - 1);
					} else {
						System.out.println("Out of that item");
					}
				}
			}
		} else {
			// Empty Inventory Slot
			System.out.println("Nothing there!");
		}
	}

	public Item inventoryHas(String itemName) {
		for (Item item : inventories) {
			if (item.name.equals(itemName)) {
				return item;
			}
		}
		return null;
	}
}
