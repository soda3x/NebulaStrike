package com.mygdx.game;

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
    // Main Game class
    private MyGdxGame game;

    // The constants for the texts
    public static final String GAME_NAME = "NEBULA STRIKE";

    // The constants for the buttons
    private static final String BUTTON_LONG_UP_TEXTURE_FILENAME = "buttonLong_blue.png";
    private static final String BUTTON_LONG_DOWN_TEXTURE_FILENAME = "buttonLong_beige_pressed.png";
    private static final float BUTTON_WIDTH = 160f;
    private static final float BUTTON_HEIGHT = 80f;
    private static final float BUTTON_SPACING = 10f;

    // The constants for kinds of sound
    private static final String PLAY_SOUND_FILENAME = "sound/button-1_newgame.mp3";
    private static final String EXIT_SOUND_FILENAME = "sound/button-4_alert.mp3";

    // The constants for music
    private static final String MENU_MUSIC_FILENAME = "sound/Mysterious-piano-theme.mp3";

    // The constants for font
    private static final String FONT_FONT_FILENAME = "good_neighbors.fnt";
    private static final String FONT_IMAGE_FILENAME = "good_neighbors.png";
    private static final String FONT_FONT_FILENAME2 = "gui/default.fnt";
    private static final String FONT_IMAGE_FILENAME2 = "gui/default.png";

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
     * Constructor to keep a reference to the main Game class
     * @param game main Game class
     */
    public MenuScreen(MyGdxGame game) {
        this.game = game;
    }

    /**
     * Create and instantiate necessary objects
     */
    public void create() {
        // Instantiate SpriteBatch
        batch = new SpriteBatch();

        // Create font for game's name label
        labelFont = new BitmapFont(
                Gdx.files.internal(FONT_FONT_FILENAME),
                Gdx.files.internal(FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        labelFont.getData().setScale(4, 4);
        labelFont.setColor(0f,1f,0f,1f);

        // Create font for buttons' labels
        buttonFont = new BitmapFont(
                Gdx.files.internal(FONT_FONT_FILENAME),
                Gdx.files.internal(FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Create label for the game's name
        gameNameLabel = new Label(labelFont, GAME_NAME,
                0f, Gdx.graphics.getHeight() - BUTTON_HEIGHT, Gdx.graphics.getWidth(), BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create the buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - BUTTON_WIDTH) / 2;
        float buttonY = (Gdx.graphics.getHeight() - 2 * BUTTON_HEIGHT) ;

        startButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        startButton.setText(buttonFont, "Start", Label.Alignment.CENTER, Label.Alignment.CENTER);
        startButton.setSound(PLAY_SOUND_FILENAME);

        creditButton = new Button(buttonX, buttonY - (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        creditButton.setText(buttonFont, "Credit", Label.Alignment.CENTER, Label.Alignment.CENTER);
        creditButton.setSound(EXIT_SOUND_FILENAME);

        scoreboardButton = new Button(buttonX, buttonY - 2 * (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        scoreboardButton.setText(buttonFont, "Scoreboard", Label.Alignment.CENTER, Label.Alignment.CENTER);
        scoreboardButton.setSound(EXIT_SOUND_FILENAME);

        quitButton = new Button(buttonX, buttonY - 3 * (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        quitButton.setText(buttonFont, "Quit", Label.Alignment.CENTER, Label.Alignment.CENTER);
        quitButton.setSound(EXIT_SOUND_FILENAME);

        // Create background music and init it
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(MENU_MUSIC_FILENAME));
        menuMusic.setLooping(true);
        menuMusic.play();

        // Init active flags by false
        startActive = false;
        creditActive = false;
        scoreboardActive = false;
        quitActive = false;

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Main loop, call/do all logic and rendering.
     * @param elapsedTime the elapsed time since the previous rendering time
     */
    public void render(float elapsedTime) {
        //Set background color and clear the screen
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            menuMusic.stop();
            creditActive = true;
        } else if (scoreboardButton.isDown) {
            menuMusic.stop();
            scoreboardActive = true;
        } else if (quitButton.isDown) {
            menuMusic.stop();
            quitActive = true;

        } else if (startActive) {
            game.setScreen(MyGdxGame.gameScreen);
        } else if (creditActive) {
            ;
        } else if (scoreboardActive) {
            ;
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
