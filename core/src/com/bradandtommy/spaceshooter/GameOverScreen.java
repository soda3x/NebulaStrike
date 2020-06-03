package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class GameOverScreen implements Screen, InputProcessor {
    private SpriteBatch batch;

    // Labels
    private Label menuLabel;
    private Label newScoreLabel;
    private Label tryAgainPromptLabel;

    // Buttons
    private Button backToMenuButton;

    // Music
    private Music menuMusic;

    // Score
    private Score score;

    // Init active flags by false
    private boolean backToMenuActive;

    public GameOverScreen() {
        this.score = null;
    }

    // Constructor for if player gets a new high score
    public GameOverScreen(Score score) {
        this.score = score;
    }

    private void create() {
        Background.getBackgroundInstance().create();
        // Font for name label and button
        BitmapFont labelFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        labelFont.getData().setScale(4, 4);
        labelFont.setColor(0f,1f,0f,1f);

        // Create font for buttons' labels
        BitmapFont buttonFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Create label for the game's name
        menuLabel = new Label(labelFont, "Game Over",
                0f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        BitmapFont tryAgainFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        tryAgainFont.getData().setScale(1, 1);
        tryAgainFont.setColor(1f,0f,0f,1f);

        tryAgainPromptLabel = new Label(tryAgainFont, "The aliens are still out there, and they're heading straight for Earth!\r\nGet out there and fight again!",
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
        if (score != null) {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.GAMEOVER_WIN_MUSIC));
        } else {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.GAMEOVER_FAIL_MUSIC));
        }
        menuMusic.setLooping(false);
        menuMusic.play();
        menuMusic.setVolume(Constants.MUSIC_VOLUME);

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);

        backToMenuActive = false;

        // Instantiate SpriteBatch
        batch = new SpriteBatch();

    }

    private void createWithScore() {
        this.create();
        BitmapFont labelFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        labelFont.getData().setScale(2, 2);
        labelFont.setColor(1f,1f,1f,1f);

        newScoreLabel = new Label(labelFont, "New High Score!",
                0f, Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        newScoreLabel = new Label(labelFont, "_ _ _",
                0f, Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );
    }

    private void assembleKeyboard() {

    }

    @Override
    public void show() {
        if (this.score == null) {
            this.create();
        } else {
            this.createWithScore();
        }
    }

    @Override
    public void render(float delta) {
        //Set background color and clear the screen
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Background.getBackgroundInstance().update(batch);

        batch.begin();

        //Draw game name label
        menuLabel.draw(batch);

        //Draw buttons
        backToMenuButton.draw(batch);

        batch.end();

        // If score is not null then render new high score elements
        if (score != null) {
            batch.begin();
            newScoreLabel.draw(batch);
            batch.end();
        } else {
            batch.begin();
            tryAgainPromptLabel.draw(batch);
            batch.end();
        }

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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        backToMenuButton.update(true, screenX, screenY);
        return true;
    }

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
