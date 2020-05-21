package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;

/**
 * Main Game class
 */
public class MyGdxGame extends Game implements ApplicationListener {
	// The class with the menu
	public static MenuScreen menuScreen;
	// The class with the game
	public static GameScreen gameScreen;

	/**
	 * Create necessary objects and set default screen
	 */
	@Override
	public void create() {
		// Create game sceen and menu screen
		gameScreen = new GameScreen();
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
	public void dispose() {
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
	public void resize(int width, int height) {
	    super.resize(width, height);
	}

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
