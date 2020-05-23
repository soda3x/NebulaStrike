package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpaceShooter extends Game implements ApplicationListener {

	// The class with the menu
	private static MenuScreen menuScreen;

	// The class with the game
	private static GameScreen gameScreen;

	/**
	 * Create necessary objects and set default screen
	 */
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
