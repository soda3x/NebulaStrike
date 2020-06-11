package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen implements Screen, InputProcessor {
    private SpriteBatch batch;

    // Labels
    private Label menuLabel;
    private Label scoreLabel;
    private Label newScoreLabel;
    private Label tryAgainPromptLabel;

    // Container for name input
    private String[] name;
    private String hint;

    // Buttons
    private Button backToMenuButton;
    private Button playAgainButton;

    // Music
    private Music gameOverMusic;

    // Score
    private Score score;
    ScoreIO scoreIO;

    // Init active flags by false
    private boolean backToMenuActive;
    private boolean playAgainActive;

    private boolean newHighScore;

    // Keyboard listener
    private AlphaInputPoller alpha;

    public GameOverScreen(Score score, boolean newHighScore) {
        this.newHighScore = newHighScore;
        this.score = score;
        this.scoreIO = new ScoreIO();
    }

    private void create() {
        hint = "Press ENTER to add your score to the high scores table";
        this.alpha = new AlphaInputPoller();
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
                0f, Gdx.graphics.getHeight() - 3.5f * Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        BitmapFont scoreFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        scoreFont.getData().setScale(2, 2);
        scoreFont.setColor(1f,1f,1f,1f);

        scoreLabel = new Label(scoreFont, Long.toString(score.getScore()) + " points",
                0f, Gdx.graphics.getHeight() - 2.5f * Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );

        // Create the buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(Constants.BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(Constants.BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getWidth() - Constants.BUTTON_WIDTH) / 2;
        float buttonY = (Gdx.graphics.getHeight() - 2 * Constants.BUTTON_HEIGHT);

        playAgainButton = new Button(buttonX, buttonY - 3 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH + 20, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        playAgainButton.setText(buttonFont, "Play Again", Label.Alignment.CENTER, Label.Alignment.CENTER);
        playAgainButton.setSound(Constants.BUTTON_SND_1);

        backToMenuButton = new Button(buttonX, buttonY - 3.5f * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING), Constants.BUTTON_WIDTH + 20, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        backToMenuButton.setText(buttonFont, "Back to Menu", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.setSound(Constants.BUTTON_SND_1);

        // Create background music and init it
        if (newHighScore) {
            gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.GAMEOVER_WIN_MUSIC));
        } else {
            gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.GAMEOVER_FAIL_MUSIC));
        }
        gameOverMusic.setLooping(false);
        gameOverMusic.play();
        gameOverMusic.setVolume(Constants.MUSIC_VOLUME);

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);

        backToMenuActive = false;
        playAgainActive = false;

        // Instantiate SpriteBatch
        batch = new SpriteBatch();

        // Limit name to size of 3 for XYZ input style
        this.name = new String[3];

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

    }

    @Override
    public void show() {
        if (!this.newHighScore) {
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
        playAgainButton.draw(batch);

        batch.end();

        // If score is not null then render new high score elements
        if (this.newHighScore) {
            batch.begin();
            newScoreLabel.draw(batch);
            scoreLabel.draw(batch);
            batch.end();
        } else {
            batch.begin();
            tryAgainPromptLabel.draw(batch);
            scoreLabel.draw(batch);
            batch.end();
        }

        // Process user's selected action presented through buttons' state
        if (backToMenuButton.isDown) {
            gameOverMusic.stop();
            backToMenuActive = true;

        } else if (backToMenuActive) {
            gameOverMusic.stop();
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getMenuScreen());
        }

        if (playAgainButton.isDown) {
            gameOverMusic.stop();
            playAgainActive = true;
        } else if (playAgainActive) {
            gameOverMusic.stop();
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getGameScreen());
        }

        // Let's get keyboard input if we need to enter a score
        if (newHighScore) {
            alpha.poll();
            pollForInput();
            BitmapFont scoreEntryFont = new BitmapFont(
                    Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                    Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                    false);
            // Scale up the font slightly to make it more legible on larger screens for DEFAULT
            scoreEntryFont.getData().setScale(2, 2);
            scoreEntryFont.setColor(1f,1f,1f,1f);

            String x = "_";
            String y = "_";
            String z = "_";
            if (name[0] != null) {
                x = name[0];
            }
            if (name[1] != null) {
                y = name[1];
            }
            if (name[2] != null) {
                z = name[2];
            }

            Label enterYourNameLabel =  new Label(scoreEntryFont, "Enter Your Name",
                    0f, Gdx.graphics.getHeight() / 1.75f - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                    Label.Alignment.CENTER, Label.Alignment.CENTER
            );

            Label scoreEntryLabel = new Label(scoreEntryFont, x + " " + y + " " + z + " ",
                    0f, Gdx.graphics.getHeight() / 2f - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                    Label.Alignment.CENTER, Label.Alignment.CENTER
            );



            if (name[0] != null && name[1] != null && name[2] != null) {
                BitmapFont hintFont = new BitmapFont(
                        Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                        Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                        false);
                // Scale up the font slightly to make it more legible on larger screens for DEFAULT
                hintFont.getData().setScale(1, 1);
                hintFont.setColor(1f,1f,1f,0.5f);
                Label pressEnterHint =  new Label(hintFont, hint,
                        0f, Gdx.graphics.getHeight() / 2.5f - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                        Label.Alignment.CENTER, Label.Alignment.CENTER
                );
                batch.begin();
                pressEnterHint.draw(batch);
                batch.end();

                if (alpha.confirm.pressed()) {
                    if (scoreIO.writeToScoresFile(new Score(name[0]+name[1]+name[2], this.score.getLevel(), this.score.getScore()))) {
                        hint = "Successfully added score to the high scores table,\nfeel free to play again or return to the main menu";
                    }
                }

            }
            batch.begin();
            enterYourNameLabel.draw(batch);
            scoreEntryLabel.draw(batch);
            batch.end();
        }
    }

    private void pollForInput() {
        this.alpha.stringBuffer(name);
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
        gameOverMusic.dispose();
        playAgainButton.dispose();
        backToMenuButton.dispose();
        batch.dispose();
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
        playAgainButton.update(true, screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        backToMenuButton.update(false, screenX, screenY);
        playAgainButton.update(false, screenX, screenY);
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
