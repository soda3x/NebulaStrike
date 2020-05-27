package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

    // ADDED BY TOMMY
    // The enum for states of the game
    public enum GameState { PLAYING, FAIL, PAUSE };

    //Having a static reference to this central class
    public static GameScreen global;
    // Main Game class
    private SpaceShooter game;

    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;
    private OrthographicCamera camera;
    private Player player;
    private Background bg;
    private Music initial;
    private Music bgm;
    private boolean showHitboxes = false;
    private InputPoller input;
    private Texture bg1, bg2;
    float yMax, yCoordBg1, yCoordBg2;
    final int BG_MOVE_SPEED = 200;
    private Score score;
    private int lives;
    private long timeElapsed;

    // ADDED BY TOMMY

    //Buttons
    private Button backToMenuButton;
    private Button resumeButton;
    private Button pauseButton;

    //Just use this to only do action when the action button is released instead of immediately as it's pressed
    private boolean backToMenuActive;
    private boolean resumeActive;
    private boolean pauseActive;

    // The game's current state
    public GameState gameState;

    private ArrayList<Enemy> enemies;

    // ADDED BY TOMMY
    public GameScreen(SpaceShooter game) {
        global = this;
        this.game = game;
    }

    private void create() {
        gameState = GameState.PLAYING;
        score = new Score();
        score.setLevel(1);
        score.setScore(0);
        lives = 3;
        input = new InputPoller();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

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

        camera = new OrthographicCamera(w, h);
        camera.setToOrtho(false, w, h);

        this.player = new Player();
        this.bg = new Background();
        player.setPos(camera, player.getWidth() / 2, 200);
        bg.setPos(camera, bg.getWidth() / 2, bg.getHeight() / 2);

        // ADDED BY TOMMY
        this.enemies = new ArrayList<Enemy>();

        // Buttons
        //------------------------------------
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
        pauseButton = new Button(Gdx.graphics.getWidth() - 50, 10, 32, 32, new Texture("pause.png"), new Texture("pause_pressed.png"));

        //------------------------------------

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        bg1 = new Texture(Gdx.files.internal("sprites/Background_alt.png"));
        bg2 = new Texture(Gdx.files.internal("sprites/Background_alt.png"));
        yMax = 480;
        yCoordBg1 = 0;
        yCoordBg2 = yMax;

        // ADDED BY TOMMY
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

    public void render(float deltaTime) {

        // ADDED BY TOMMY
        updatePause();
        if (gameState == GameState.PAUSE) {
            // Pause background music and present the PAUSED
            //backgroundMusic.pause();
            drawPaused(batch);
            return;
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ADDED BY TOMMY
        // Set current game state to PLAYING
        gameState = GameState.PLAYING;

        // Scrolling Background
        yCoordBg1 -= BG_MOVE_SPEED * deltaTime;
        yCoordBg2 = yCoordBg1 - yMax;  // We move the background, not the camera
        if (yCoordBg1 < 0) {
            yCoordBg1 = yMax;
            yCoordBg2 = 0;
        }

        batch.begin();
        batch.draw(bg1, 0, yCoordBg1);
        batch.draw(bg2, 0, yCoordBg2);
        batch.end();

        // ADDED BY TOMMY
        if (enemies.size() > 0) {
            for (Enemy enemy : enemies) {
                enemy.draw(batch);
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

        Label runningScore = new Label(hudFont, "Score: " + score.getScore(),
                (camera.position.x) + 100f,Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, 100f, Constants.BUTTON_HEIGHT,
                Label.Alignment.RIGHT, Label.Alignment.CENTER);

        Label runningLevel = new Label(hudFont, "Level: " + score.getLevel(),
                (camera.position.x / 2) - 100f, Gdx.graphics.getHeight() - Constants.BUTTON_HEIGHT, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.LEFT, Label.Alignment.CENTER);

        Label runningLife = new Label(hudFont, "Lives left: " + lives,
                (camera.position.x / 2) - 100f, (camera.position.y / 2) - 125f, Gdx.graphics.getWidth(), Constants.BUTTON_HEIGHT,
                Label.Alignment.LEFT, Label.Alignment.CENTER);

        //pauseButton.draw(uiBatch);

        batch.begin();
        runningScore.draw(batch);
        runningLevel.draw(batch);
        runningLife.draw(batch);
        pauseButton.draw(batch);
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
            for (int i = 0; i < player.getSpawnedBullets().size(); ++i) {
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
            game.setScreen(game.getMenuScreen());
            return;
        } else if (resumeActive) {
            //isBackKeyPressed = false;
            resumeActive = false;
            backToMenuActive = false;

            //backgroundMusic.play();
            gameState = GameState.PLAYING;
        }
    }

    private void update(float deltaTime) {

        // ADDED BY TOMMY
        if (pauseButton.isDown) {
            pauseActive = true;
            return;
        } else if (pauseActive) {
            pauseActive = false;
            gameState = GameState.PAUSE;
            return;
        }

        // Get amount of time game has been on GameScreen so we can calculate things such as when to fire next bullet
        this.timeElapsed = System.currentTimeMillis();

        input.poll();

        camera.update();

        // ADDED BY TOMMY
        for(int i = enemies.size() - 1; i >= 0; i--){
            Enemy enemy = enemies.get(i);
            if (enemy.getY() <= 0) {
                enemy.dead = true;
            } else {
                //enemy.move(Gdx.graphics.getDeltaTime());
                //enemy.update(timeElapsed);
                enemy.move(deltaTime);
                enemy.update(deltaTime);

                // Check if enemy's bullets hit player
                for (Bullet enBullet : enemy.bullets) {
                    if (enBullet.getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
                        lives -= 1;
                        if (lives <= 0) {
                            lives = 0;
                        }
                    }
                }

                // Check if player's bullets hit enemy
                for (Bullet pyBullet : player.bullets) {
                    if (pyBullet.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                        enemy.dead = true;
                        score.addScore(50);
                    }
                }
            }
            if (enemy.isDead()) {
                enemies.remove(i);
            }
        }

        player.move(deltaTime);
        player.update(timeElapsed);

        // Spawn enemy - ADDDED BY TOMMY
        if (enemies.size() < 10) {
            int rnd = MathUtils.random(1, 20);
            if (rnd == 10) {
                Enemy enemy = new Enemy();
                float enemyStartX = MathUtils.random(camera.position.x - enemy.getWidth() / 2, camera.position.x - camera.viewportWidth + enemy.getWidth());
                float enemyStartY = camera.position.y - camera.viewportHeight;
                enemy.setPos(camera, enemyStartX, enemyStartY);
                enemies.add(enemy);
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
     * @param batch sprite batch
     */
    public void drawPaused(SpriteBatch batch) {
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
        resumeButton.setText(buttonFont, "CONTINUE", Label.Alignment.CENTER, Label.Alignment.CENTER);
        resumeButton.draw(batch);

        // Draw Quit button
        backToMenuButton.setX(x);
        backToMenuButton.setY(y - 2 * (Constants.BUTTON_HEIGHT + Constants.BUTTON_SPACING));
        backToMenuButton.setText(buttonFont, "BACK TO MENU", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
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
                // Set frog's toward target center point to the touched point
                //frog.setTowardTargetCenterPoint(new Vector2(screenX, Gdx.graphics.getHeight() - screenY));

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
