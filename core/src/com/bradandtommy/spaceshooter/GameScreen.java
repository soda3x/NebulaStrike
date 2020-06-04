package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

class GameScreen implements Screen, InputProcessor {
    // The enum for states of the game
    public enum GameState { PLAYING, FAIL, PAUSE };

    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;
    private OrthographicCamera camera;
    private Player player;
    private Music initial;
    private Music bgm;
    private Sound tutorialSpeech;
    private boolean showHitboxes = false;
    private InputPoller input;
    private long scoreCounter;
    private long levelCounter;
    private int lives;
    private long timeElapsed;

    //Buttons
    private Button backToMenuButton;
    private Button resumeButton;
    private Button pauseButton;

    //Just use this to only do action when the action button is released instead of immediately as it's pressed
    private boolean backToMenuActive;
    private boolean resumeActive;
    private boolean pauseActive;
    private boolean musicConfigured;

    private boolean spawnedGameOverScreen;

    // The game's current state
    public GameState gameState;

    private ArrayList<Enemy> enemies;

    // ADDED BY TOMMY
    private int enemiesMax;

    private void create() {
        this.musicConfigured = false;
        this.spawnedGameOverScreen = false;
        gameState = GameState.PLAYING;
        lives = Constants.PLAYER_INIT_LIVES;
        scoreCounter = 0;
        levelCounter = 1;

        // Calculate number of enemies to draw on screen per level
        enemiesMax = MathUtils.round(levelCounter * Constants.ENEMY_NUMBER_BASE / 2);

        input = new InputPoller();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        tutorialSpeech = Gdx.audio.newSound(Gdx.files.internal(Constants.TUTORIAL_SPEECH));
        tutorialSpeech.play();

        camera = new OrthographicCamera(w, h);
        camera.setToOrtho(false, w, h);

        this.player = new Player();
        player.setPos(camera, player.getWidth() / 2, 200);

        this.enemies = new ArrayList<Enemy>();

        // Buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(Constants.BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(Constants.BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getHeight() - Constants.BUTTON_WIDTH) / 2;
        float buttonY = 0f;

        //restartButton.setSound(NEWGAME_SOUND_FILENAME);
        // Button to back to main menu
        backToMenuButton = new Button(buttonX, buttonY, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        //backToMenuButton.setSound(RETURN_SOUND_FILENAME);
        // Button to resume
        resumeButton = new Button(buttonX, buttonY, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        //resumeButton.setSound(RESUME_SOUND_FILENAME);

        // quitButton.setSound(ALERT_SOUND_FILENAME);
        // Button to pause
        pauseButton = new Button(Gdx.graphics.getWidth() - 50, 10, 32, 32, new Texture(Constants.BUTTON_PAUSE), new Texture(Constants.BUTTON_PAUSE_DOWN));

        //------------------------------------

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        Background.getBackgroundInstance().create();

        // Set the buttons' state to up
        backToMenuButton.isDown = false;
        resumeButton.isDown = false;
        pauseButton.isDown = false;

        // Reset active flags
        backToMenuActive = false;
        resumeActive = false;

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);
    }

    public void configureMusic() {
        initial = Gdx.audio.newMusic(Gdx.files.internal(Constants.GAMESCREEN_INITIAL_MUSIC));
        initial.setLooping(false);
        initial.play();
        initial.setVolume(Constants.MUSIC_VOLUME);

        if (initial.isPlaying()) {
            bgm = Gdx.audio.newMusic(Gdx.files.internal(Constants.GAMESCREEN_MUSIC_LOOP));
            bgm.setLooping(true);
            bgm.setVolume(Constants.MUSIC_VOLUME);
        }

        initial.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                bgm.play();
            }
        });
        this.musicConfigured = true;
    }

    public void render(float deltaTime) {

        this.updatePause();
        if (gameState == GameState.PAUSE) {
            // Pause background music and present the Pause screen
            initial.setVolume(0.0f);
            bgm.setVolume(0.0f);
            this.drawPaused();
            return;
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set current game state to PLAYING
        gameState = GameState.PLAYING;

        // Scrolling Background
        Background.getBackgroundInstance().update(batch);

        if (!enemies.isEmpty()) {
            for (int i = 0; i < enemies.size(); i++) {
                if (!enemies.get(i).isDead()) {
                    enemies.get(i).draw(batch);
                }
            }
        }

        player.draw();

        if (!player.hasFired()) {
            BitmapFont hintFont = new BitmapFont(
                    Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                    Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                    false);
            // Scale up the font slightly to make it more legible on larger screens for DEFAULT
            hintFont.getData().setScale(1, 1);
            hintFont.setColor(1f, 1f, 1f, 0.5f);
            Label hint = new Label(hintFont, Constants.HINT_1,
                    0f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                    Label.Alignment.CENTER, Label.Alignment.CENTER);
            batch.begin();
            hint.draw(batch);
            batch.end();
        }

        // Draw HUD with running score and level counters
        BitmapFont hudFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);

        hudFont.getData().setScale(1, 1);
        hudFont.setColor(1f, 1f, 1f, 1f);

        Label runningScore = new Label(hudFont, "Score: " + scoreCounter,
                (camera.position.x) + 100f,Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, 100f, Constants.BUTTON_HEIGHT,
                Label.Alignment.RIGHT, Label.Alignment.CENTER);

        Label runningLevel = new Label(hudFont, "Level: " + levelCounter,
                (camera.position.x / 2) - 100f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.LEFT, Label.Alignment.CENTER);

        Label runningLife = new Label(hudFont, "Lives left: " + lives,
                (camera.position.x / 2) - 100f, (camera.position.y / 2) - 125f, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.LEFT, Label.Alignment.CENTER);

        batch.begin();
        runningScore.draw(batch);
        runningLevel.draw(batch);
        runningLife.draw(batch);
        if(player.hasFired()) pauseButton.draw(batch);
        batch.end();

        this.update(deltaTime);

        if (showHitboxes) {
            ShapeRenderer sr = new ShapeRenderer();
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            sr.setAutoShapeType(true);
            // Get players hitbox
            sr.rect(this.player.getBoundingRectangle().getX(), this.player.getBoundingRectangle().getY(), this.player.getBoundingRectangle().getWidth(), this.player.getBoundingRectangle().getHeight());
            // Get spawned player bullets hitboxes
            for (int i = 0; i < player.getSpawnedBullets().size(); i++) {
                sr.rect(this.player.getSpawnedBullets().get(i).getBoundingRectangle().getX(),
                        this.player.getSpawnedBullets().get(i).getBoundingRectangle().getY(),
                        this.player.getSpawnedBullets().get(i).getBoundingRectangle().getWidth(),
                        this.player.getSpawnedBullets().get(i).getBoundingRectangle().getHeight());
            }
            sr.end();
        }
    }

    private void updatePause() {
        if (gameState != GameState.PAUSE) {
            return;
        }
        // Process user's selected action presented through buttons' state in PAUSE case
        if (backToMenuButton.isDown) {
            backToMenuActive = true;
        } else if (resumeButton.isDown) {
            resumeActive = true;

        } else if (backToMenuActive) {
            SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getMenuScreen());
        } else if (resumeActive) {
            resumeActive = false;
            backToMenuActive = false;
            initial.setVolume(Constants.MUSIC_VOLUME);
            bgm.setVolume(Constants.MUSIC_VOLUME);
            gameState = GameState.PLAYING;
        }
    }

    private boolean gameIsPaused() {
        return pauseActive || pauseButton.isDown;
    }

    private void update(float deltaTime) {

        if (!musicConfigured && player.hasFired()) {
            this.configureMusic();
            tutorialSpeech.stop();
        }

        if (this.lives == 0) {
            this.gameState = GameState.FAIL;
        }

        if (gameState == GameState.FAIL) {

            ScoreIO scoreIO = new ScoreIO();
            for (Score score : scoreIO.getScores()) {
                if (new Score("newScore", levelCounter - 1, scoreCounter).higherScoreThan(score)) {
                    // Pass in score to get new high score screen
                    this.spawnedGameOverScreen = true;
                    SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getGameOverScreen(score, true));
                    break;
                } else {
                    continue;
                }
            }
            // Generic game over screen
            if (!spawnedGameOverScreen) {
                Score score = new Score("NONAME", levelCounter - 1, scoreCounter);
                SpaceShooter.getSpaceShooterInstance().setScreen(SpaceShooter.getSpaceShooterInstance().getGameOverScreen(score, false));
            }

            this.dispose();
        }

        if (gameIsPaused()) gameState = GameState.PAUSE;
        // Get amount of time game has been on GameScreen so we can calculate things such as when to fire next bullet
        this.timeElapsed = System.currentTimeMillis();

        input.poll();

        camera.update();

        int aliveEnemies = 0;
        for (int i = 0; i < enemies.size(); i++) {

            if (enemies.get(i).isDead()) {
                continue;
            }

            Enemy enemy = enemies.get(i);
          
            if (enemy.getY() <= 0) {
                enemy.setPos(camera, enemy.getX(), - Gdx.graphics.getHeight() / 2f + enemy.getHeight() / 2);
            }
          
            // De-spawn enemies when they leave bottom of screen
            if (enemy.getY() + enemy.getHeight() <= 0) {
                enemy.dead = true;

            } else {
                enemy.move(deltaTime);
                enemy.update(deltaTime);

                // Check if enemy's bullets hit player
                for (int j = 0; j < enemy.bullets.size(); j++) {
                    if (enemy.bullets.get(j).hasExpired()) {
                        Bullet bullet = enemy.bullets.remove(j);
                        continue;
                    }

                    if (enemy.bullets.get(j).getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
                        lives -= 1;
                        if (lives <= 0) {
                            lives = 0;
                        }

                        // Remove bullet on player hit
                        enemy.bullets.get(j).setExpired(true);
                    }
                }

                // Check if player's bullets hit enemy
                for (int k = 0; k < player.bullets.size(); k++) {

                    // Remove bullet on first enemy hit
                    if (player.bullets.get(k).hasExpired()) {
                        Bullet bullet = player.bullets.remove(k);
                        continue;
                    }

                    if (player.bullets.get(k).getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                        Sound enemyHitSound = Gdx.audio.newSound(Gdx.files.internal(Constants.HIT_SND));
                        enemyHitSound.play();
                        enemy.dead = true;

                        // Calculate score and life
                        switch (enemy.enemykind) {
                            case NORMAL:
                                scoreCounter += 50;
                                break;
                            case BOSS:
                                scoreCounter += 200;
                                break;
                            case BOUNTY:
                                scoreCounter += 100;
                                if (lives == Constants.PLAYER_MAX_LIVES) {
                                    scoreCounter += 500;
                                } else {
                                    lives += 1;
                                }
                                break;
                        }
                        player.bullets.get(k).setExpired(true);
                    }
                }
            }
            if (!enemy.isDead()) {
                aliveEnemies += 1;
            }
        }
        // Move to next level if all enemies are dead
        if (player.hasFired() && aliveEnemies == 0 && enemies.size() == enemiesMax) {
            enemies.clear();
            levelCounter += 1;
            enemiesMax = MathUtils.round(levelCounter * Constants.ENEMY_NUMBER_BASE / 2);
        }

        player.move(deltaTime);
        player.update(timeElapsed);
        // Spawn enemy once start hint has been dismissed
        if (player.hasFired() && enemies.size() < enemiesMax) {
            int rnd = MathUtils.random(1, 20);
            if (rnd == 10) {
                Enemy newEnemy;
                rnd = MathUtils.random(1, 100);
                if (rnd <= Constants.SPAWN_RATE_BOSS) {
                    newEnemy = new Enemy(Enemy.EnemyKind.BOSS);
                } else if (rnd < Constants.SPAWN_RATE_BOUNTY) {
                    newEnemy = new Enemy(Enemy.EnemyKind.BOUNTY);
                } else {
                    newEnemy = new Enemy(Enemy.EnemyKind.NORMAL);
                }

                float enemyStartX =  MathUtils.random(Gdx.graphics.getWidth() / 2f + newEnemy.getWidth() / 2, - Gdx.graphics.getWidth() / 2f + newEnemy.getWidth() / 2);
                float enemyStartY = -Gdx.graphics.getHeight() / 2f + newEnemy.getHeight() / 2;

                newEnemy.setPos(camera, enemyStartX, enemyStartY);
                enemies.add(newEnemy);
            }
        }

        // DEBUG: Toggle hitboxes if H is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (showHitboxes) {
                showHitboxes = false;
            } else {
                showHitboxes = true;
            }
        }
    }

    /**
     * Draw the pause message along with the Continue and Quit buttons
     */
    public void drawPaused() {
        Background.getBackgroundInstance().update(batch);

        batch.begin();

        // Font
        // Create font for buttons' labels
        BitmapFont buttonFont = new BitmapFont(
                Gdx.files.internal(Constants.FONT_FONT_FILENAME),
                Gdx.files.internal(Constants.FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens for DEFAULT
        buttonFont.getData().setScale(2, 2);

        // Determine position to draw
        float x = (camera.viewportWidth - Constants.BUTTON_WIDTH) / 2;
        //float y = (Gdx.graphics.getHeight() - 3 * (BUTTON_HEIGHT + BUTTON_SPACING)) / 2;
        float y = camera.viewportHeight - 2 * Constants.BUTTON_HEIGHT;

        // Draw paused message
        Label pauseLabel = new Label(buttonFont, "PAUSED",
                0, y, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );
        pauseLabel.draw(batch);

        // Draw Resume button with "TRY AGAIN" label
        resumeButton.setX(x);
        resumeButton.setY(y - (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING));
        resumeButton.setText(buttonFont, "Continue", Label.Alignment.CENTER, Label.Alignment.CENTER);
        resumeButton.draw(batch);

        // Draw Quit button
        backToMenuButton.setX(x);
        backToMenuButton.setY(y - 2 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING));
        backToMenuButton.setText(buttonFont, "Back To Menu", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        initial.dispose();
        bgm.dispose();
        backToMenuButton.dispose();
        resumeButton.dispose();
        pauseButton.dispose();
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.remove(i);
        }
    }

    public Player getPlayer() {
        return this.player;
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
    public void show() {
        this.create();
    }

    @Override
    public void hide() {}

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
        switch (gameState) {
            case PLAYING:
                // Check if the pauseButton is down based on the touched point's coordinate
                pauseButton.update(true, screenX, screenY);
                break;

            case FAIL:
                // Check if the restartButton or backToMenuButton is down based on the touched point's coordinate
                backToMenuButton.update(true, screenX, screenY);
                break;

            case PAUSE:
                // Check if the resumeButton or quitButton is down based on the touched point's coordinate
                resumeButton.update(true, screenX, screenY);
                if (resumeButton.isDown) {
                    pauseActive = false;
                    pauseButton.isDown = false;
                }
                backToMenuButton.update(true, screenX, screenY);
                break;
        }
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
        switch (gameState) {
            case PLAYING:
                // Set the state of pauseButton to up
                pauseButton.update(false, screenX, screenY);
                break;

            case FAIL:
                // Set the state of restartButton or backToMenuButton to up
                backToMenuButton.update(false, screenX, screenY);
                break;

            case PAUSE:
                // Set the state of resumeButton or quitButton to up
                resumeButton.update(false, screenX, screenY);
                backToMenuButton.update(false, screenX, screenY);
                break;
        }
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

    public InputPoller getInputPoller() {
        return input;
    }
}
