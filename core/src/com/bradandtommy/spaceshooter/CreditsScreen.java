package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class CreditsScreen implements Screen, InputProcessor {

    private Button backToMenuButton;
    private BitmapFont creditsTitleFont;
    private BitmapFont creditsFont;
    private Music creditsMusic;
    private Label creditsTitleLabel;
    private Label creditsLabel;
    private SpriteBatch batch;
    private boolean backToMenuActive;

    private void create() {

        creditsMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.CREDITS_MUSIC_LOOP));
        batch = new SpriteBatch();
        creditsTitleFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        creditsTitleFont.getData().setScale(4, 4);
        creditsTitleFont.setColor(0f,1f,0f,1f);

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

        Texture buttonLongTexture = new Texture(Constants.BUTTON_LONG_UP_TEXTURE_FILENAME);
        Texture buttonLongDownTexture = new Texture(Constants.BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - Constants.BUTTON_WIDTH) / 2;
        float buttonY = (Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT);
        backToMenuButton = new Button(buttonX, buttonY - 3 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH + 20, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        backToMenuButton.setText(buttonFont, "Back to Menu", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.setSound(Constants.BUTTON_SND_1);

        backToMenuActive = false;

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);
    }


    @Override
    public void show() { this.create(); }

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
