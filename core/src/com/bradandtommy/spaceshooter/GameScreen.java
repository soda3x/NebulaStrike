package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents the game screen.
 */
public class GameScreen implements Screen, InputProcessor {

    // The enum for states of the game
    public enum GameState { PLAYING, FAIL, SUCCESS, PAUSE };

    // The constants for the tilemap which is the game's background
    private static final String TILEMAP_FILENAME = "street.tmx";
    private static final int STREET_LANES = 8;

    // The constants for the frog which is the game's protagonist
    private static final String FROG_TEXTURESHEET_FILENAME = "froggy.png";
    private static final int FROG_FRAME_COLS = 4;
    private static final int FROG_FRAME_ROWS = 2;
    private static final int FROG_SPEED = 128;
    // Size and margin of frog image in a corresponding frame
    private static final float FROG_ACTUAL_WIDTH = 30;
    private static final float FROG_ACTUAL_HEIGHT = 20;
    private static final float FROG_ACTUAL_MARGIN_LEFT = 16;
    private static final float FROG_ACTUAL_MARGIN_BOTTOM = 16;

    // The constants for the passing cars which is the game's obstacles
    private static final String CAR_TEXTURE_FILENAME = "car.png";
    private static final String CAR_TEXTURE_FILENAME2 = "car2.png";
    private static final int CAR_TOTAL_PER_LANE = 3;
    private static final int CAR_SPEED_MIN = 96;
    private static final int CAR_SPEED_INCREASE_PER_LANE = 12;

    // The constants for the splat which is the image of dead frog
    private static final String SPLAT_TEXTURE_FILENAME = "splat.png";

    // The constants for the button
    private static final String BUTTON_LONG_UP_TEXTURE_FILENAME = "buttonLong_blue.png";
    private static final String BUTTON_LONG_DOWN_TEXTURE_FILENAME = "buttonLong_beige_pressed.png";

    // The constants for sound
    private static final String NEWGAME_SOUND_FILENAME = "sound/button-1_newgame.mp3";
    private static final String RESUME_SOUND_FILENAME = "sound/button-2_resume.mp3";
    private static final String RETURN_SOUND_FILENAME = "sound/button-3_back2menu.mp3";
    private static final String ALERT_SOUND_FILENAME = "sound/button-4_alert.mp3";

    // The constants for music
    private static final String PLAYING_MUSIC_FILENAME = "sound/Bog-Creatures-On-the-Move_Looping.mp3";
    private static final String GAMEOVER_MUSIC_FILENAME = "sound/Funny-game-over-sound.mp3";
    private static final String WINNING_MUSIC_FILENAME = "sound/Winning-sound-effect.mp3";

    // The constants for font
    private static final String FONT_FONT_FILENAME = "good_neighbors.fnt";
    private static final String FONT_IMAGE_FILENAME = "good_neighbors.png";

    // The game application object
    private MyGdxGame game;

    // The game's current state
    private GameState gameState = GameState.PLAYING;

    // The objects for drawing
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private BitmapFont bmfont;

    // Tile Map and its renderer
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    // Sound when back key is pressed
    private Sound backKeySound;

    // Bacground music for states
    private Music backgroundMusic;
    private Music gameOverMusic;
    private Music winningMusic;

    // Player Frog
    private Frog frog;

    // Array of car objects
    private Car[][] cars;

    // Goal collision
    private Rectangle goalRectangle;

    // Texture of dead frog
    private Texture splatTexture;

    //Buttons
    private Button restartButton;
    private Button backToMenuButton;
    private Button resumeButton;
    private Button quitButton;

    //Just use this to only restart when the restart button is released instead of immediately as it's pressed
    private boolean restartActive;
    private boolean backToMenuActive;
    private boolean resumeActive;
    private boolean quitActive;

    // Flag for BACK key is pressed
    private boolean isBackKeyPressed;

    /**
     * Constructor to keep a reference to the main Game class
     * @param game main Game class
     */
    public GameScreen(MyGdxGame game){
        this.game = game;
    }

    /**
     * Create necessary object and do some appropriate setup when the game is opened
     */
    public void create() {

        // Rendering
        spriteBatch = new SpriteBatch();

        // Initiate the TiledMap and its renderer
        tiledMap = new TmxMapLoader().load(TILEMAP_FILENAME);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Set the camera
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth , screenHeight);

        // Frog
        frog = new Frog(FROG_TEXTURESHEET_FILENAME,
                FROG_FRAME_COLS,
                FROG_FRAME_ROWS,
                FROG_ACTUAL_WIDTH,
                FROG_ACTUAL_HEIGHT,
                FROG_ACTUAL_MARGIN_LEFT,
                FROG_ACTUAL_MARGIN_BOTTOM
        );

        //Cars
        cars = new Car[STREET_LANES][CAR_TOTAL_PER_LANE];
        for (int i = 0; i < STREET_LANES; i++){
            for (int j = 0; j < CAR_TOTAL_PER_LANE; j++){
                if (i % 2 == 0) {
                    cars[i][j] = new Car(CAR_TEXTURE_FILENAME);
                } else {
                    cars[i][j] = new Car(CAR_TEXTURE_FILENAME2);
                }
            }
        }

        // Goal
        float laneHeight = Gdx.graphics.getHeight() / (STREET_LANES + 2f);
        goalRectangle = new Rectangle(0, 9f * laneHeight + 0.8f * frog.getBoundingRectangle().getHeight(),
                Gdx.graphics.getWidth(), laneHeight - 0.8f * frog.getBoundingRectangle().getHeight());

        // Splat
        splatTexture = new Texture(SPLAT_TEXTURE_FILENAME);

        //Buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonHeight = screenHeight / (STREET_LANES + 2);
        float buttonWidth = buttonHeight * 5;
        float buttonX = (screenWidth - buttonWidth) / 2;
        float buttonY = 0f;

        //Button to start again
        restartButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, buttonLongTexture, buttonLongDownTexture);
        restartButton.setSound(NEWGAME_SOUND_FILENAME);

        //Button to back to main menu
        backToMenuButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, buttonLongTexture, buttonLongDownTexture);
        backToMenuButton.setSound(RETURN_SOUND_FILENAME);

        // Button to resume
        resumeButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, buttonLongTexture, buttonLongDownTexture);
        resumeButton.setSound(RESUME_SOUND_FILENAME);

        // Button to quit
        quitButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, buttonLongTexture, buttonLongDownTexture);
        quitButton.setSound(ALERT_SOUND_FILENAME);

        // Sound in case the BACK KEY is pressed
        backKeySound = Gdx.audio.newSound(Gdx.files.internal(ALERT_SOUND_FILENAME));

        // Music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(PLAYING_MUSIC_FILENAME));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal(GAMEOVER_MUSIC_FILENAME));
        winningMusic = Gdx.audio.newMusic(Gdx.files.internal(WINNING_MUSIC_FILENAME));

        //Default font for text
        bmfont = new BitmapFont(
                Gdx.files.internal(FONT_FONT_FILENAME),
                Gdx.files.internal(FONT_IMAGE_FILENAME),
                false);
        // Scale up the font slightly to make it more legible on larger screens
        bmfont.getData().setScale(2, 2);

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);
        // Enable catching BACK key on Android phone
        Gdx.input.setCatchBackKey(true);

        // Start new game
        newGame();
    }

    /**
     * Initiate game environment for the new game
     */
    private void newGame() {

        // Set current game state to PLAYING
        gameState = GameState.PLAYING;

        // Set the button flags to false
        restartButton.isDown = false;
        backToMenuButton.isDown = false;
        resumeButton.isDown = false;
        quitButton.isDown = false;

        isBackKeyPressed = false;

        //Translate camera to center of screen
        camera.position.x = Gdx.graphics.getWidth() / 2;
        camera.position.y = Gdx.graphics.getHeight() / 2;

        //Frog start location
        float centerX = Gdx.graphics.getWidth() / 2;
        float centerY = frog.getHeight() / 2;
        frog.setCenterPoint(new Vector2(centerX, centerY));
        frog.setTowardTargetCenterPoint(new Vector2(centerX, centerY));

        // Car start  location
        float laneHeight = Gdx.graphics.getHeight() / (STREET_LANES + 2);
        float carHeight = cars[0][0].getHeight();
        float carWidth = cars[0][0].getWidth();
        float carX;
        float carY;
        for (int i = 0; i < STREET_LANES; i++){
            carY = (i + 1) * laneHeight + (laneHeight - carHeight) / 2;
            for (int j = 0; j < CAR_TOTAL_PER_LANE; j++){
                if (i % 2 == 0) {
                    carX = generateCarX(j, carWidth, carHeight/2);
                } else {
                    carX = Gdx.graphics.getWidth() - generateCarX(j, carWidth, carHeight/2);
                }
                cars[i][j].setX(carX);
                cars[i][j].setY(carY);
            }
        }

        // Reset active flags
        restartActive = false;
        backToMenuActive = false;
        resumeActive = false;
        quitActive = false;

        // Init music's state
        gameOverMusic.setLooping(false);
        gameOverMusic.stop();
        winningMusic.setLooping(false);
        winningMusic.stop();
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    /**
     * Main game loop, call/do all logic and rendering.
     * @param elapsedTime the elapsed time since the previous rendering time
     */
    public void render(float elapsedTime) {

        // Update the Game State
        update(elapsedTime);

        // Clear the screen before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // Render Map
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // Draw cars
        for (int i = 0; i < STREET_LANES; i++) {
            for (int j = 0; j < CAR_TOTAL_PER_LANE; j++){
                Car car = cars[i][j];
                car.draw(spriteBatch);
            }
        }

        switch (gameState) {
            //if gameState is Running: Draw Controls if any
            case PLAYING:
                //Draw frog
                frog.draw(spriteBatch);
                break;

            //if gameState is Success: Draw congratulation message, Restart button with "START AGAIN" label, Back button
            case SUCCESS:
                //Draw frog
                frog.draw(spriteBatch);

                // Pause background music and present winning
                backgroundMusic.pause();
                drawTargetReached(spriteBatch);

                break;

            //if gameState is Fail: Draw game over message, Restart button with "TRY AGAIN" label, Back button
            case FAIL:
                // Draw splat instead of frog
                spriteBatch.begin();
                float splatX = frog.getCenterPoint().x - splatTexture.getWidth() / 2;
                float splatY = frog.getCenterPoint().y - splatTexture.getHeight() / 2;
                spriteBatch.draw(splatTexture, splatX, splatY);
                spriteBatch.end();

                // Pause background music and present game over
                backgroundMusic.pause();
                drawGameOver(spriteBatch);

                break;

            //if gameState is Pause: stop the background music
            case PAUSE:

                //Draw frog
                frog.draw(spriteBatch);

                // Pause background music and present pause
                backgroundMusic.pause();
                drawPaused(spriteBatch);

                break;
        }
    }

    /**
     * Method for all game logic.
     * This method is called at the start of GameCore.render() before
     * any actual drawing is done.
     * @param elapsedTime the elapsed time since the previous rendering time
     */
    private void update(float elapsedTime) {
        //Update Game State based on input
        switch (gameState) {

            case PLAYING:
                // Update position of frog
                frog.updateCenterPoint(FROG_SPEED, elapsedTime);

                // Update position of cars
                for (int i = 0; i < STREET_LANES; i++) {
                    for (int j = 0; j < CAR_TOTAL_PER_LANE; j++){
                        Car car = cars[i][j];
                        float carX = car.getX();
                        if (i % 2 == 0) {
                            carX = carX + (CAR_SPEED_MIN + i * CAR_SPEED_INCREASE_PER_LANE) * elapsedTime;
                            if (carX >= Gdx.graphics.getWidth()) {
                                carX = -car.getWidth();
                            }

                        } else {
                            carX = carX - (CAR_SPEED_MIN + i * CAR_SPEED_INCREASE_PER_LANE) * elapsedTime;
                            if (carX <= -car.getWidth()) {
                                carX = Gdx.graphics.getWidth();
                            }
                        }
                        car.setX(carX);

                        // Check if frog has collided with a car
                        if (frog.getBoundingRectangle().overlaps(car.getBoundingRectangle())) {
                            //Game over
                            gameOverMusic.play();
                            gameState = GameState.FAIL;
                        }

                    }
                }

                //Check if frog has met the winning condition
                if (gameState != GameState.SUCCESS && frog.getBoundingRectangle().overlaps(goalRectangle)) {
                    //Player has won!
                    winningMusic.play();
                    gameState = GameState.SUCCESS;
                } else if (isBackKeyPressed) {      //Check if BACK key has been pressed for pausing
                    gameState = GameState.PAUSE;
                }

                break;

            case SUCCESS:
            case FAIL:
                // Process user's selected action presented through buttons' state
                if (restartButton.isDown) {
                    restartActive = true;
                } else if (backToMenuButton.isDown) {
                    backToMenuActive = true;
                } else if (restartActive) {
                    newGame();
                } else if (backToMenuActive) {
                    game.setScreen(MyGdxGame.menuScreen);
                }

                break;

            case PAUSE:
                // Process user's selected action presented through buttons' state in PAUSE case
                if (quitButton.isDown) {
                    quitActive = true;
                } else if (resumeButton.isDown) {
                    resumeActive = true;
                } else if (quitActive) {
                    Gdx.app.exit();
                } else if (resumeActive) {
                    isBackKeyPressed = false;
                    resumeActive = false;
                    quitActive = false;
                    backgroundMusic.play();
                    gameState = GameState.PLAYING;
                }

                break;
        }
    }

    /**
     * Random generate the x coordinate of specified car in a lane
     * @param indexPerLane index of the car in a lane
     * @param carWidth width of the car
     * @param minDistance minimum distance between the car and the neighbour car in the same lane
     * @return the generated x coordinate of the car
     */
    private float generateCarX(int indexPerLane, float carWidth, float minDistance) {
        // The width of the part for each car
        int partWidth = (int) (Gdx.graphics.getWidth() / 3 - carWidth - minDistance);
        // The width of each small segment in each part for each car
        int segmentWidth = partWidth / 10;
        // Random generate the distance from begin each part
        int randomDistance = MathUtils.random(0, 9) * segmentWidth;
        // Return the random x coordinate for the car specified by the given index per the lane
        return indexPerLane * Gdx.graphics.getWidth() / 3f + randomDistance;
    }

    /**
     * Draw the winning message along with the Start again and Back to menu buttons
     * @param batch sprite batch
     */
    public void drawTargetReached(SpriteBatch batch) {
        batch.begin();

        // Determine position to draw
        float laneHeight = Gdx.graphics.getHeight() / (STREET_LANES + 2);
        float x = 0f;
        float y = 6 * laneHeight;

        // Draw winning congratulation message
        Label winLabel = new Label(bmfont, "YOU WIN. Congrats!",
                x, y, Gdx.graphics.getWidth(), laneHeight,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );
        winLabel.draw(batch);

        // Draw Restart button with "TRY AGAIN" label
        restartButton.setY(y - laneHeight);
        restartButton.setText(bmfont, "START AGAIN", Label.Alignment.CENTER, Label.Alignment.CENTER);
        restartButton.draw(batch);

        // Draw Back button
        backToMenuButton.setY(y - 2 * laneHeight);
        backToMenuButton.setText(bmfont, "BACK TO MENU", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.draw(batch);

        batch.end();

    }


    /**
     * Draw the losing message along with the Try again and Back to menu buttons
     * @param batch sprite batch
     */
    public void drawGameOver(SpriteBatch batch) {
        batch.begin();

        // Determine position to draw
        float laneHeight = Gdx.graphics.getHeight() / (STREET_LANES + 2);
        float x = 0f;
        float y = 0f;
        // If frog is in the top haft of the screen, text should be at right below of frog
        if (frog.getY() > Gdx.graphics.getHeight() / 2 - laneHeight) {
            y = frog.getY() - laneHeight;
        } else {        // If frog is in the bottom haft of the screen, text should be at above of frog
            y = frog.getY() + 4 * laneHeight;
        }

        // Draw game over message
        Label gameoverLabel = new Label(bmfont, "GAME OVER",
                x, y, Gdx.graphics.getWidth(), laneHeight,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );
        gameoverLabel.draw(batch);

        // Draw Restart button with "TRY AGAIN" label
        restartButton.setY(y - laneHeight);
        restartButton.setText(bmfont,"TRY AGAIN", Label.Alignment.CENTER, Label.Alignment.CENTER);
        restartButton.draw(batch);

        // Draw Back button
        backToMenuButton.setY(y - 2 * laneHeight);
        backToMenuButton.setText(bmfont,"BACK TO MENU", Label.Alignment.CENTER, Label.Alignment.CENTER);
        backToMenuButton.draw(batch);

        batch.end();
    }

    /**
     * Draw the pause message along with the Continue and Quit buttons
     * @param batch sprite batch
     */
    public void drawPaused(SpriteBatch batch) {
        batch.begin();

        // Determine position to draw
        float laneHeight = Gdx.graphics.getHeight() / (STREET_LANES + 2);
        float x = 0f;
        float y = 5 * laneHeight;

        // Draw paused message
        Label pauseLabel = new Label(bmfont, "PAUSED",
                x, y, Gdx.graphics.getWidth(), laneHeight,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );
        pauseLabel.draw(batch);

        // Draw Resume button with "TRY AGAIN" label
        resumeButton.setY(y - laneHeight);
        resumeButton.setText(bmfont, "CONTINUE", Label.Alignment.CENTER, Label.Alignment.CENTER);
        resumeButton.draw(batch);

        // Draw Quit button
        quitButton.setY(y - 2 * laneHeight);
        quitButton.setText(bmfont, "QUIT", Label.Alignment.CENTER, Label.Alignment.CENTER);
        quitButton.draw(batch);
        batch.end();
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
     * Cleanup done after the game closes.
     */
    @Override
    public void dispose() {

        spriteBatch.dispose();

        tiledMap.dispose();

        frog.dispose();

        for (int i = 0; i < STREET_LANES; i++) {
            for (int j = 0; j < CAR_TOTAL_PER_LANE; j++){
                cars[i][j].dispose();
            }
        }

        splatTexture.dispose();

        restartButton.dispose();
        backToMenuButton.dispose();
        resumeButton.dispose();
        quitButton.dispose();

        backKeySound.dispose();
        backgroundMusic.dispose();
        gameOverMusic.dispose();
        winningMusic.dispose();

        bmfont.dispose();
    }

    /**
     * Show the game screen
     */
    @Override
    public void show() {
        create();
    }

    /**
     * Hide the game screen
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

        if (gameState == GameState.PLAYING) {
            if (keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE){
                // Respond to the back button click
                backKeySound.play(1F);
                isBackKeyPressed = true;
                return true;
            }
        }
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
        switch (gameState) {
            case PLAYING:
                // Set frog's toward target center point to the touched point
                frog.setTowardTargetCenterPoint(new Vector2(screenX, Gdx.graphics.getHeight() - screenY));
                break;

            case SUCCESS:
            case FAIL:
                // Check if the restartButton or backToMenuButton is down based on the touched point's coordinate
                restartButton.update(true, screenX, screenY);
                backToMenuButton.update(true, screenX, screenY);
                break;

            case PAUSE:
                // Check if the resumeButton or quitButton is down based on the touched point's coordinate
                resumeButton.update(true, screenX, screenY);
                quitButton.update(true, screenX, screenY);
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
                break;

            case SUCCESS:
            case FAIL:
                // Set the state of restartButton or backToMenuButton to up
                restartButton.update(false, screenX, screenY);
                backToMenuButton.update(false, screenX, screenY);
                break;

            case PAUSE:
                // Set the state of resumeButton or quitButton to up
                resumeButton.update(false, screenX, screenY);
                quitButton.update(false, screenX, screenY);
                break;
        }
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
