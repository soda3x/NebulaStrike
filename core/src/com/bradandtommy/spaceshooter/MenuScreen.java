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
    public static final String GAME_NAME = "Furoggu crossing";
    public static final String GAME_FOOTNOTE = "Copyright @2020 Trong Tam Dang - DANTY017, UniSA. No rights reserved.";

    // The constants for the buttons
    private static final String BUTTON_LONG_UP_TEXTURE_FILENAME = "buttonLong_blue.png";
    private static final String BUTTON_LONG_DOWN_TEXTURE_FILENAME = "buttonLong_beige_pressed.png";
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 100f;

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

    // for name label, footnote and button
    private BitmapFont labelFont;
    private BitmapFont footnoteFont;
    private BitmapFont buttonFont;

    // Labels
    private Label nameLabel;
    private Label footnoteLabel;

    // Buttons
    private Button playButton;
    private Button exitButton;

    // Music
    private Music menuMusic;

    // Use this to only play/exit when the play/exit button is released instead of immediately as it's pressed
    private boolean playActive;
    private boolean exitActive;

    /**
     * Constructor to keep a reference to the main Game class
     * @param game main Game class
     */
    public MenuScreen(MyGdxGame game){
        this.game = game;
    }

    /**
     * Create necessary object and do some appropriate setup when the game is opened
     */
    public void create() {

        // Instantiate SpriteBatch UI skin and stage
        batch = new SpriteBatch();

        // Create font for game's name label
        labelFont = new BitmapFont(
                Gdx.files.internal(FONT_FONT_FILENAME),
                Gdx.files.internal(FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        labelFont.getData().setScale(4, 4);
        labelFont.setColor(0f,1f,0f,1f);

        // Create font for footnote
        footnoteFont = new BitmapFont(
                Gdx.files.internal(FONT_FONT_FILENAME2),
                Gdx.files.internal(FONT_IMAGE_FILENAME2),
                false);
        footnoteFont.setColor(0.2f, 0.2f,0.2f,1f);

        // Create font for buttons' labels
        buttonFont = new BitmapFont(
                Gdx.files.internal(FONT_FONT_FILENAME),
                Gdx.files.internal(FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Create label for the game'name
        nameLabel = new Label(labelFont, GAME_NAME,
                0f, (Gdx.graphics.getHeight() + BUTTON_HEIGHT)/ 2, Gdx.graphics.getWidth(), BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create footnote Label
        footnoteLabel = new Label(footnoteFont, GAME_FOOTNOTE,
                0f, 10, Gdx.graphics.getWidth(), BUTTON_HEIGHT / 2,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create the buttons and set the sound to it
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - BUTTON_WIDTH * 2) / 2;
        float buttonY = (Gdx.graphics.getHeight() - BUTTON_HEIGHT) / 2;

        playButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        playButton.setText(buttonFont, "PLAY", Label.Alignment.CENTER, Label.Alignment.CENTER);
        playButton.setSound(PLAY_SOUND_FILENAME);

        exitButton = new Button(buttonX + BUTTON_WIDTH, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        exitButton.setText(buttonFont,"EXIT", Label.Alignment.CENTER, Label.Alignment.CENTER);
        exitButton.setSound(EXIT_SOUND_FILENAME);

        // Create background music and init it
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(MENU_MUSIC_FILENAME));
        menuMusic.setLooping(true);
        menuMusic.play();

        // Init active flags by false
        playActive = false;
        exitActive = false;

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

        // Draw game name label
        nameLabel.draw(batch);
        // Draw footnote
        footnoteLabel.draw(batch);

        // Draw buttons
        playButton.draw(batch);
        exitButton.draw(batch);

        batch.end();

        // Process user's selected action presented through buttons' state
        if (playButton.isDown) {
            menuMusic.stop();
            playActive = true;
        } else if (exitButton.isDown) {
            menuMusic.stop();
            exitActive = true;
        } else if (playActive) {
            game.setScreen(MyGdxGame.gameScreen);
        } else if (exitActive) {
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
        footnoteFont.dispose();
        buttonFont.dispose();

        playButton.dispose();
        exitButton.dispose();

        menuMusic.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

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
        playButton.update(true, screenX, screenY);
        exitButton.update(true, screenX, screenY);

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
        playButton.update(false, screenX, screenY);
        exitButton.update(false, screenX, screenY);

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
