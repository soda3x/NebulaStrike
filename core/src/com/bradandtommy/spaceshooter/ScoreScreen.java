package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.Font;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;

class ScoreScreen implements Screen, InputProcessor {
    private SpriteBatch batch;

    // Labels
    private Label menuLabel;
    private Label headerLabel;
    private ArrayList<Label> scoreLabels;

    // Buttons
    private Button backToMenuButton;

    // Music
    private Music menuMusic;

    // Init active flags by false
    private boolean backToMenuActive;

    // Scores
    private ScoreIO scores;

    private void create() {
        // Font for name label and button
        BitmapFont labelFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        BitmapFont headerFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        BitmapFont scoreFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        labelFont.getData().setScale(4, 4);
        labelFont.setColor(0f,1f,0f,1f);

        headerFont.getData().setScale(2, 2);
        headerFont.setColor(1f,1f,1f,1f);

        scoreFont.getData().setScale(1, 1);
        scoreFont.setColor(1f,1f,1f,0.75f);

        // Create font for buttons' labels
        BitmapFont buttonFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Create label for the game's name
        menuLabel = new Label(labelFont, "High Scores",
                0f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        headerLabel = new Label(headerFont, "Name             Level             Score",
                0f, Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create the buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(Constants.BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(Constants.BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - Constants.BUTTON_WIDTH) / 2;
        float buttonY = (Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT);

        backToMenuButton = new Button(buttonX, buttonY - 3 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH + 20, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        backToMenuButton.setText(buttonFont, "Back to Menu", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.setSound(Constants.BUTTON_SND_1);

        // Create background music and init it
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.SCOREBOARD_MUSIC_FILENAME));
        menuMusic.setLooping(true);
        menuMusic.play();
        menuMusic.setVolume(Constants.MUSIC_VOLUME);

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);

        backToMenuActive = false;

        // Initialise scores
        scores = new ScoreIO();
        scoreLabels = new ArrayList<Label>();
        initScoreLabels(scoreFont);


        // Instantiate SpriteBatch
        batch = new SpriteBatch();
    }

    private void drawScores(SpriteBatch batch) {
        for (Label label : scoreLabels) {
            label.draw(batch);
        }
    }

    private void initScoreLabels(BitmapFont font) {
        // Prepare Labels for top 5 scores
        for (int i = 0; i < 5; ++i) {
            Score score = scores.getScores().poll();
            if (score == null) {
                // If no score for whatever reason just use dummy score
                score = new Score("ABC", 1, 1000);
            }
            Label label = new Label(font,score.getName() + "                                      " + score.getLevel() + "                                   " + score.getScore(),
                    0f, Gdx.graphics.getHeight() - (i + 7)  * 30, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                    Label.Alignment.CENTER, Label.Alignment.CENTER
            );
            scoreLabels.add(label);
        }

    }

    @Override
    public void show() { this.create(); }

    @Override
    public void render(float delta) {
        //Set background color and clear the screen
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //Draw game name label
        menuLabel.draw(batch);
        headerLabel.draw(batch);
        drawScores(batch);

        //Draw buttons
        backToMenuButton.draw(batch);

        batch.end();

        // Process user's selected action presented through buttons' state
        if (backToMenuButton.isDown) {
            menuMusic.stop();
            backToMenuActive = true;

        } else if (backToMenuActive) {
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
