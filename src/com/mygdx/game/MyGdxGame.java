package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	
	SpriteBatch batch;
	SpriteBatch uiBatch;
	
	OrthographicCamera camera;
	Player player;
	UI ui;
	Map map;
	List<Texture> textures = new ArrayList<Texture>();
	Sound walking;
	Sound running;
	Sound punching;
	Music background;
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.update();
		neededGraphics();
		walking = Gdx.audio.newSound(Gdx.files.internal("walking.wav"));
		running = Gdx.audio.newSound(Gdx.files.internal("running.mp3"));
		punching = Gdx.audio.newSound(Gdx.files.internal("punch.wav"));
		
		background = Gdx.audio.newMusic(Gdx.files.internal("birds.mp3"));

		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		player = new Player();
		ui = new UI(player);
		map = new Map(MapType.Base, player);
		map.addEntity(player);
		player.curLocation = map;
		Gdx.input.setInputProcessor(new InputController(this));
	}
	
	public void neededGraphics() {
		textures.add(new Texture("grassland.png"));
		textures.add(new Texture("desert.png"));
		textures.add(new Texture("Tree1.png"));
		textures.add(new Texture("Tree2.png"));
		textures.add(new Texture("Tree3.png"));
		textures.add(new Texture("Tree1_Damaged.png"));
		textures.add(new Texture("Tree1_VeryDamaged.png"));
		textures.add(new Texture("Player_Animations.png"));
		textures.add(new Texture("BerryBush.png"));
		textures.add(new Texture("Player.png"));
		textures.add(new Texture("UI.png"));
		textures.add(new Texture("Bars.png"));
		textures.add(new Texture("berry.png"));
		textures.add(new Texture("log.png"));
		textures.add(new Texture("pigAnimation.png"));
		textures.add(new Texture("pigRight.png"));
		
		
	}

	@Override
	public void render() {
		
		map.update();
		camera.position.set(player.x + player.img.getWidth() / 2, player.y + player.img.getHeight() / 2, 0);
		camera.update();
		ui.update(player);
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		map.draw(batch);
		batch.end();
		uiBatch.begin();
		ui.draw(uiBatch);
		uiBatch.end();
		
		if (player.health <= 0) {
			dispose();
			System.exit(0);
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		walking.dispose();
		running.dispose();
		punching.dispose();
		background.dispose();
		for (Texture tex: textures) {
			tex.dispose();
		}
	}

}
