package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represent the credit screen
 */
class CreditsScreen implements Screen, InputProcessor {

    // Button
    private Button backToMenuButton;

    // Font
    private BitmapFont creditsTitleFont;
    private BitmapFont creditsFont;

    // Music
    private Music creditsMusic;

    // Label
    private Label creditsTitleLabel;
    private Label creditsLabel;

    // Sprite batch
    private SpriteBatch batch;

    // Use this to only when the back to menu button is released instead of immediately as it's pressed
    private boolean backToMenuActive;

    /**
     * Create necessary object and do some appropriate setup
     */
    private void create() {

        // Create background music and initialise it
        creditsMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.CREDITS_MUSIC_LOOP));
        batch = new SpriteBatch();
        creditsTitleFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        creditsTitleFont.getData().setScale(4, 4);
        creditsTitleFont.setColor(0f,1f,0f,1f);

        // Create the font for the credit label
        creditsFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        creditsFont.getData().setScale(1, 1);
        creditsFont.setColor(1f,1f,1f,1f);

        creditsTitleLabel = new Label(creditsTitleFont, "Credits",
                0f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        creditsLabel = new Label(creditsFont, "Programming by Bradley Newman\nProgramming by Tommy Dang\nMusic and Sound Design by Bradley Newman\nGraphics by Tommy Dang\nAdditional Graphics by Bradley Newman\n\n Thank you for playing our game!",
                0f, Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create font for buttons' labels
        BitmapFont buttonFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Create the buttons and set the sound to it
        Texture buttonLongTexture = new Texture(Constants.BUTTON_LONG_UP_TEXTURE_FILENAME);
        Texture buttonLongDownTexture = new Texture(Constants.BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - Constants.BUTTON_WIDTH) / 2;
        float buttonY = (Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT);
        backToMenuButton = new Button(buttonX, buttonY - 3 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH + 20, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        backToMenuButton.setText(buttonFont, "Back to Menu", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.setSound(Constants.BUTTON_SND_1);

        // Init active flags by false
        backToMenuActive = false;

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Show the credit screen
     */
    @Override
    public void show() { this.create(); }

    /**
     * Main loop, call/do all logic and rendering.
     * @param delta delta time since the previous rendering time
     */
    @Override
    public void render(float delta) {
        Background.getBackgroundInstance().update(batch);
        creditsMusic.play();
        batch.begin();
        creditsTitleLabel.draw(batch);
        creditsLabel.draw(batch);
        backToMenuButton.draw(batch);
        batch.end();

        // Process user's selected action presented through buttons' state
        if (backToMenuButton.isDown) {
            creditsMusic.stop();
            backToMenuActive = true;

        } else if (backToMenuActive) {
            creditsMusic.stop();
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getMenuScreen());
        }
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

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
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
        backToMenuButton.update(true, screenX, screenY);
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
        backToMenuButton.update(false, screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
