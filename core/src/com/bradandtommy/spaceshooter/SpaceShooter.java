package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
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
		// Create game screen and menu screen
		gameScreen = new GameScreen(this);
		menuScreen = new MenuScreen(this);

		// Set window title
		Gdx.graphics.setTitle(menuScreen.GAME_NAME);

		// Change screens to the menu
		setScreen(menuScreen);
	}

	/**
	 * This method calls the super class render
	 * which in turn calls the render of the actual screen being used
	 */
	@Override
	public void render() {
		super.render();
	}

	/**
	 * This method calls the super class dispose
	 * and clean up the class's member object
	 */
	@Override
	public void dispose () {
		super.dispose();
		gameScreen.dispose();
		menuScreen.dispose();
	}

	/**
	 * This method calls the super class resize
	 * @param width new width
	 * @param height new height
	 */
	@Override
	public void resize(int width, int height) { super.resize(width, height);}

	/**
	 * This method calls the super class pause
	 */
	@Override
	public void pause() {
		super.pause();
	}

	/**
	 * This method calls the super class resume
	 */
	@Override
	public void resume() {
		super.resume();
	}

}
