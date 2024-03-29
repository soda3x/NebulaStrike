package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.Menu;

/**
 *
 */
public class SpaceShooter extends Game implements ApplicationListener {

	// Sprite batch
	private SpriteBatch batch;

	// Image
	private Texture img;

	// SpaceShooter instance
	private static SpaceShooter instance;

	// Class with the game
	private GameScreen gameScreen;

	// Class with the menu
	private MenuScreen menuScreen;

	// Class with the credit
	private CreditsScreen creditsScreen;

	// Class with the score
	private ScoreScreen scoreScreen;

	/**
	 * Constructor is private so as to enforce singleton design pattern
	 * Calls create()
	 */
	private SpaceShooter() {
		this.create();

		// Create game screen and menu screen
		gameScreen = new GameScreen();
		menuScreen = new MenuScreen();
		creditsScreen = new CreditsScreen();
		scoreScreen = new ScoreScreen();
	}

	/**
	 * SpaceShooter should be singleton, call create in private constructor
	 * and use get instance to enforce shared object usage
	 * @return instance of SpaceShooter
	 */
	 public static SpaceShooter getSpaceShooterInstance() {
		if (instance == null) {
			instance = new SpaceShooter();
		}
		return instance;
	}

	/**
	 * Create necessary objects and set default screen
	 */
	@Override
	public void create () {
		// Change screens to the menu
		this.setScreen(menuScreen);
	}

	/**
	 * Retrieving the appropriated screen
	 */
	GameScreen getGameScreen() { return gameScreen; }
	MenuScreen getMenuScreen() { return menuScreen; }
	CreditsScreen getCreditsScreen() { return creditsScreen; }
	ScoreScreen getScoreScreen() { return scoreScreen; }
	GameOverScreen getGameOverScreen(Score score, boolean newHighScore) { return new GameOverScreen(score, newHighScore); }

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
