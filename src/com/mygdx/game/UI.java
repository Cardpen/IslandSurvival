package com.mygdx.game;

import java.awt.Font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UI {
	Player player;
	Sprite ui;
	TextureRegion hpBar;
	TextureRegion staminaBar;
	TextureRegion hungerBar;
	TextureRegion thirstBar;
	int wideWidth;
	int narrowWidth;
	int height;

	int pushHunger = 0;
	int pushThirst = 0;
	BitmapFont font;

	public UI(Player player) {
		this.player = player;
		font = new BitmapFont();
		font.getData().setScale(0.9f);
		wideWidth = 181;
		narrowWidth = 151;
		height = 15;
		Texture tex = new Texture("UI.png");
		Texture bars = new Texture("Bars.png");
		hpBar = new TextureRegion(bars, 0, 0, wideWidth, height);
		staminaBar = new TextureRegion(bars, 0, 16, narrowWidth, height);
		hungerBar = new TextureRegion(bars, 191, 0, wideWidth, height);
		thirstBar = new TextureRegion(bars, 191, 16, narrowWidth, height);
		hungerBar.flip(true, false);
		thirstBar.flip(true, false);
		this.ui = new Sprite(tex, 0, 0, tex.getWidth(), tex.getHeight());
	}

	public void update(Player player) {
		hpBar.setRegionWidth((int) (wideWidth * ((double) player.health / (double) player.maxHealth)));
		staminaBar.setRegionWidth((int) (narrowWidth * ((double) player.stamina / (double) player.maxStamina)));
		pushHunger = wideWidth - (int) (wideWidth * ((double) player.hunger / (double) player.maxHunger));
		pushThirst = narrowWidth - (int) (narrowWidth * ((double) player.thirst / (double) player.maxThirst));
	}

	public void draw(SpriteBatch batch) {
		font.getData().setScale(0.9f);
		font.setColor(Color.WHITE);

		batch.draw(hpBar, 10, 454);
		font.draw(batch, "Health: " + Math.round(player.health) + "/" + player.maxHealth, 30, 469);
		batch.draw(hungerBar, 534 + pushHunger, 454);
		font.draw(batch, "Hunger: " + Math.round(player.hunger) + "/" + player.maxHunger, 590, 469);

		font.getData().setScale(0.7f);
		batch.draw(staminaBar, 10, 437);
		font.draw(batch, "Stamina: " + Math.round(player.stamina) + "/" + player.maxStamina, 30, 448);
		batch.draw(thirstBar, 560 + pushThirst, 435);
		font.draw(batch, "Thirst: " + Math.round(player.thirst) + "/" + player.maxThirst, 620, 448);

		ui.draw(batch);
		
		// Draw the items in the inventory
		for (int i = 0; i < player.inventories.size(); i++) {
			batch.draw(player.inventories.get(i).img, 321 + (49* i), 14);
			font.getData().setScale(0.8f);
			font.setColor(Color.BLACK);
			font.draw(batch, player.inventories.get(i).stackSize + "", 343 + (49 * i), 20);
		}
	}
}
