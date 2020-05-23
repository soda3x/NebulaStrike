package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpaceShooter extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private SpaceShooter instance;

	/**
	 * Constructor is private so as to enforce singleton design pattern
	 * Calls create()
	 */
	private SpaceShooter() {
		this.create();
	}

	/**
	 * SpaceShooter should be singleton, call create in private constructor
	 * and use get instance to enforce shared object usage
	 * @return instance of SpaceShooter
	 */
	public SpaceShooter getSpaceShooterInstance() {
		if (instance == null) {
			instance = new SpaceShooter();
		}
		return instance;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
