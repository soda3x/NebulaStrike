package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represent menu screen
 */
public class MenuScreen implements Screen, InputProcessor {
    // Sprite batch for drawing
    private SpriteBatch batch;

    // Font for name label and button
    private BitmapFont labelFont;
    private BitmapFont buttonFont;

    // Labels
    private Label gameNameLabel;

    // Buttons
    private Button startButton;
    private Button creditButton;
    private Button scoreboardButton;
    private Button quitButton;

    // Music
    private Music menuMusic;

    // Just use this to only start/credit/scoreboard/quit when the corresponding button is released instead of immediately as it's pressed
    private boolean startActive;
    private boolean creditActive;
    private boolean scoreboardActive;
    private boolean quitActive;

    /**
     * Create and instantiate necessary objects
     */
    private void create() {
        Background.getBackgroundInstance().create();
        // Create font for game's name label
        labelFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        labelFont.getData().setScale(4, 4);
        labelFont.setColor(0f,1f,0f,1f);

        // Create font for buttons' labels
        buttonFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Create label for the game's name
        gameNameLabel = new Label(labelFont, "Nebula Strike",
                0f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create the buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(Constants.BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(Constants.BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - Constants.BUTTON_WIDTH) / 2;
        float buttonY = (Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT);

        startButton = new Button(buttonX, buttonY, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        startButton.setText(buttonFont, "Play", Label.Alignment.CENTER, Label.Alignment.CENTER);
        startButton.setSound(Constants.BUTTON_SND_1);

        scoreboardButton = new Button(buttonX, buttonY - (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        scoreboardButton.setText(buttonFont, "High Scores", Label.Alignment.CENTER, Label.Alignment.CENTER);
        scoreboardButton.setSound(Constants.BUTTON_SND_1);

        creditButton = new Button(buttonX, buttonY - 2 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        creditButton.setText(buttonFont, "Credits", Label.Alignment.CENTER, Label.Alignment.CENTER);
        creditButton.setSound(Constants.BUTTON_SND_1);

        quitButton = new Button(buttonX, buttonY - 3 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        quitButton.setText(buttonFont, "Quit", Label.Alignment.CENTER, Label.Alignment.CENTER);
        quitButton.setSound(Constants.BUTTON_SND_1);

        // Create background music and init it
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.MENU_MUSIC_FILENAME));
        menuMusic.setLooping(false);
        menuMusic.play();
        menuMusic.setVolume(Constants.MUSIC_VOLUME);

        // Init active flags by false
        startActive = false;
        creditActive = false;
        scoreboardActive = false;
        quitActive = false;

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);
        // Instantiate SpriteBatch
        batch = new SpriteBatch();
    }

    /**
     * Main loop, call/do all logic and rendering.
     * @param elapsedTime the elapsed time since the previous rendering time
     */
    public void render(float elapsedTime) {
        //Set background color and clear the screen
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Background.getBackgroundInstance().update(batch);

        batch.begin();

        //Draw game name label
        gameNameLabel.draw(batch);

        //Draw buttons
        startButton.draw(batch);
        creditButton.draw(batch);
        scoreboardButton.draw(batch);
        quitButton.draw(batch);

        batch.end();

        // Process user's selected action presented through buttons' state
        if (startButton.isDown) {
            menuMusic.stop();
            startActive = true;
        } else if (creditButton.isDown) {
            creditActive = true;
        } else if (scoreboardButton.isDown) {
            menuMusic.stop();
            scoreboardActive = true;
        } else if (quitButton.isDown) {
            quitActive = true;

        } else if (startActive) {
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getGameScreen());
        } else if (creditActive) {
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getCreditsScreen());
        } else if (scoreboardActive) {
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getScoreScreen());
        } else if (quitActive) {
            Gdx.app.exit();
        }
    }

    /**
     * Cleanup done after the game closes.
     */
    @Override
    public void dispose() {
        batch.dispose();

        labelFont.dispose();
        buttonFont.dispose();

        startButton.dispose();
        creditButton.dispose();
        scoreboardButton.dispose();
        quitButton.dispose();

        menuMusic.dispose();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    /**
     * Show the menu screen
     */
    @Override
    public void show() { create(); }

    /**
     * Hide the menu screen
     */
    @Override
    public void hide() {
    }

    /**
     * Called when a key was pressed
     * @param keycode one of the constants in Input.Keys
     * @return whether the input was processed
     */
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Called when a key was released
     * @param keycode one of the constants in Input.Keys
     * @return whether the input was processed
     */
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    /**
     * Called when a key was typed
     * @param character The character
     * @return whether the input was processed
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Called when the screen was touched or a mouse button was pressed
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @param button the button
     * @return whether the input was processed
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        startButton.update(true, screenX, screenY);
        creditButton.update(true, screenX, screenY);
        scoreboardButton.update(true, screenX, screenY);
        quitButton.update(true, screenX, screenY);

        return true;
    }

    /**
     * Called when a finger was lifted or a mouse button was released
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @param button the button
     * @return whether the input was processed
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        startButton.update(false, screenX, screenY);
        creditButton.update(false, screenX, screenY);
        scoreboardButton.update(false, screenX, screenY);
        quitButton.update(false, screenX, screenY);

        return true;
    }

    /**
     * Called when a finger or the mouse was dragged.
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @return whether the input was processed
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @return whether the input was processed
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    /**
     * Called when the mouse wheel was scrolled
     * @param amount the scroll amount, -1 or 1 depending on the direction the wheel was scrolled.
     * @return whether the input was processed.
     */
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
