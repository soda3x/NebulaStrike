package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/**
 * UniSA Tiled-LibGDX implementation Assignment 1.
 *
 * Sound and music implementation sourced from
 * https://www.soundjay.com/button-sounds-1.html
 * https://soundimage.org/fantasywonder/
 * http://www.orangefreesounds.com/funny-game-sound/
 * http://www.orangefreesounds.com/winning-sound-effect/
 * http://www.orangefreesounds.com/mysterious-piano-theme/
 *
 * There will be a license text file for the orangefreesounds in the asset/sound folder
 *
 * follow link for more details.
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
