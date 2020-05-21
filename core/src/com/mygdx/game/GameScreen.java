package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen, InputProcessor {
    // The enum for states of the game
    public enum GameState { PLAYING, FAIL, SUCCESS, PAUSE }

    /**Having a static reference to this central class comes in very useful, even if this
     * idea (referred to as a "singleton") is considered bad practice and should be
     * employed as little as possible. */
    public static GameScreen global;

    public static final float SCREEN_SHAKE_MAX_AMOUNT = 4.5f;
    public static final float SCREEN_SHAKE_THRESHOLD = 0.15f;

    // The constants for the button
    private static final String BUTTON_LONG_UP_TEXTURE_FILENAME = "buttonLong_blue.png";
    private static final String BUTTON_LONG_DOWN_TEXTURE_FILENAME = "buttonLong_beige_pressed.png";
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 80f;
    private static final float BUTTON_SPACING = 10f;

    /**Keeps track of each nebulaStrike effect implemented from the list below */
    int nebulaStrikeLevel = 0;
    /**This "pulse" is a subtle color change in the text on the screen, this is really
     * nice to use in buttons */
    float nebulaStrikeChangePulse = 0f;
    String[] nebulaStrikeText = {
            "0: Base Game (Really Boring...)",
            "1: Smoother Movement",
            "2: Engine Trails",
            "3: Bigger Bullets",
            "4: More Bullets",
            "5: Faster Bullets",
            "6: Scattering Bullets",
            "7: Muzzle Flashes",
            "8: Shooting Sounds",
            "9: Experiment",
            "10: Mix Sounds Together",
            "11: Impact Sounds",
            "12: Impact Particles",
            "13: Impact Flashes",
            "14: Impact Knockback",
            "15: Death Explosions",
            "16: Screenshake",
            "17: Enemy Spawn Impulse",
            "18: Erratic Enemy Movement",
            "19: More Enemies",
            "20: Scrolling Background",
            "21: Music (Now we're talkin\'!)",
            "Credits",
            "Further Reading",
            ""
    };
    String credits;
    String further;

    float screenShake = 0f;
    Vector2 shakeTranslate = new Vector2();
    float backgroundY = 0f;
    float overlayY = 0f;
    float backgroundSpeed = 300f;

    SpriteBatch batch;
    /**A separate batch for drawing ui elements is useful as it can be drawn without
     * any influence from the camera */
    SpriteBatch uiBatch;
    /**There are only two textures, a pair of spritesheets from OpenGameArt.org,
     * everything else is just a region made from these sheets */
    Texture ships;
    Texture bullets;
    Texture background;
    Texture overlay;
    BitmapFont font;
    Color tintColor = new Color(122f/255f, 160f/255f, 122f/255f, 1f);
    Color baseColor = new Color(122f/255f, 160f/255f, 122f/255f, 1f);
    Color pulseColor = new Color(28f/255f, 233f/255f, 228f/255f, 1f);
    Music music;
    Sound playerDeath;
    public ShaderProgram alphaShader;

    /**Having even a static camera is useful for adding screen-shake. It may also be
     * worth using a second OrthographicCamera for transforming the UI, even in
     * 3d games as well. */
    OrthographicCamera camera = new OrthographicCamera();
    /**Useful for checking if an object has fallen out of the world */
    Rectangle worldCollider = new Rectangle(0, 0, 1024, 728);
    InputPoller input = new InputPoller();
    Enemies enemies = new Enemies();
    Particles particles = new Particles();
    Player player = new Player();
    Projectiles projectiles = new Projectiles();

    // The game's current state
    private GameState gameState = GameState.PLAYING;
    //Buttons
    private Button restartButton;
    private Button backToMenuButton;
    private Button resumeButton;
    private Button quitButton;
    //Just use this to only do action when the action button is released instead of immediately as it's pressed
    private boolean restartActive;
    private boolean backToMenuActive;
    private boolean resumeActive;
    private boolean quitActive;

    public GameScreen() {
        global = this;
    }

    /**As a rule of thumb, make sure object constructors don't rely on the LibGDX API.
     * Any initialization that actually requires LibGDX to be functioning should be put
     * in a special init() method to be called here in this method */
    public void create () {
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        ships = new Texture("clayster2012-OGABY3-shipsheet.png");
        bullets = new Texture("Master484-CC0-M484BulletCollection2-B2A.png");
        background = new Texture("Background.png");
        overlay = new Texture("Overlay.png");
        font = new BitmapFont(
                Gdx.files.internal("Aldrich24.fnt"),
                Gdx.files.internal("Aldrich24.png"), false);

        credits = Gdx.files.internal("credits.txt").readString();
        further = Gdx.files.internal("further.txt").readString();

        music = Gdx.audio.newMusic(Gdx.files.internal("SummonTheRawk.mp3"));
        music.setLooping(true);
        playerDeath = Gdx.audio.newSound(Gdx.files.internal("railgun.wav"));
        String defaultVert = batch.getShader().getVertexShaderSource();
        String alphaFrag = Gdx.files.internal("alpha_frag.glsl").readString();
        alphaShader = new ShaderProgram(defaultVert, alphaFrag);

        player.init();
        enemies.init();
        projectiles.init();
        particles.init();

        // Set current game state to PLAYING
        gameState = GameState.PLAYING;
        // Buttons
        Texture buttonLongTexture;
        Texture buttonLongDownTexture;
        buttonLongTexture = new Texture(BUTTON_LONG_UP_TEXTURE_FILENAME);
        buttonLongDownTexture = new Texture(BUTTON_LONG_DOWN_TEXTURE_FILENAME);
        float buttonX = (Gdx.graphics.getHeight() - BUTTON_WIDTH) / 2;
        float buttonY = 0f;
        // Button to start again
        restartButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        //restartButton.setSound(NEWGAME_SOUND_FILENAME);
        // Button to back to main menu
        backToMenuButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        //backToMenuButton.setSound(RETURN_SOUND_FILENAME);
        // Button to resume
        resumeButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
        //resumeButton.setSound(RESUME_SOUND_FILENAME);
        // Button to quit
        quitButton = new Button(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonLongTexture, buttonLongDownTexture);
       // quitButton.setSound(ALERT_SOUND_FILENAME);

        // Set the buttons' state to up
        restartButton.isDown = false;
        backToMenuButton.isDown = false;
        resumeButton.isDown = false;
        quitButton.isDown = false;
        // Reset active flags
        restartActive = false;
        backToMenuActive = false;
        resumeActive = false;
        quitActive = false;

        // Enable receiving all touch and key input events
        Gdx.input.setInputProcessor(this);

        Gdx.gl.glClearColor(0, 0, 0.01f, 0);
    }

    @Override
    public void resize(int w, int h) {
        worldCollider.width = w;
        worldCollider.height = h;
        camera.viewportWidth = w;
        camera.viewportHeight = h;
        camera.position.x = w/2f;
        camera.position.y = h/2f;
    }

    @Override
    public void dispose() {
        player.dispose();
        enemies.dispose();
        background.dispose();
        overlay.dispose();
        projectiles.dispose();
        particles.dispose();
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        input.poll();

        //Update Game State based on input
        switch (gameState) {
            case PLAYING:
                //Screenshake
                if (screenShake > 0.0f) {
                    screenShake -= deltaTime;
                } else {
                    screenShake = 0.0f;
                }
                if (nebulaStrikeLevel >= 16) {
                    float seed = MathUtils.random((float)Math.PI * 2.0f);
                    float shake = Math.min(screenShake, SCREEN_SHAKE_THRESHOLD) * SCREEN_SHAKE_MAX_AMOUNT * (1 / SCREEN_SHAKE_THRESHOLD);
                    shakeTranslate.set((float) Math.sin(seed) * shake, (float) Math.cos(seed) * shake);
                    camera.translate(shakeTranslate);
                }

                //Background
                backgroundY -= deltaTime * backgroundSpeed;
                backgroundY %= background.getHeight();
                overlayY += deltaTime * 2f * backgroundSpeed;
                overlayY %= overlay.getHeight();

                if (input.exit.pressed()) {
                    // Gdx.app.exit();
                    gameState = GameState.PAUSE;
                    return;
                }
                nebulaStrikeChangePulse = Math.max(0f, nebulaStrikeChangePulse - deltaTime * 3f);
                if (input.next.pressed()) {
                    nebulaStrikeLevel = Math.min(nebulaStrikeLevel + 1, nebulaStrikeText.length - 1);
                    nebulaStrikeChangePulse = 1f;
                    if (nebulaStrikeLevel == 24) {
                        playerDeath.play();
                        for (int x = 0; x < Enemies.EXPLOSION_PIECES * 3; x++) {
                            int pa = particles.spawn(Particles.Type.EXPLOSION);
                            particles.x[pa] = player.sprite.getX() + player.sprite.getRegionWidth() / 2f;
                            particles.y[pa] = player.sprite.getY() + player.sprite.getRegionHeight() / 2f;
                        }
                        screenShake += 1.0f;
                    }
                }
                if (input.prev.pressed()) {
                    nebulaStrikeLevel = Math.max(nebulaStrikeLevel - 1, 0);
                    nebulaStrikeChangePulse = 1f;
                }

                if (nebulaStrikeLevel < 24)
                    player.update(deltaTime);
                enemies.update(deltaTime);
                projectiles.update(deltaTime);
                particles.update(deltaTime);

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
                    //isBackKeyPressed = false;
                    resumeActive = false;
                    quitActive = false;

                    //backgroundMusic.play();
                    gameState = GameState.PLAYING;
                }

                break;
        }

    }

    @Override
    public void render (float elapsedTime) {
        update();

        if (gameState == GameState.PAUSE) {
            // Pause background music and present the PAUSED
            //backgroundMusic.pause();
            drawPaused(uiBatch);
            return;
        }

        //This is a vital function call to clear the screen before drawing anything
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();

        if (nebulaStrikeLevel >= 20) {
            float w = Gdx.graphics.getWidth() / 2f - background.getWidth() / 2f;
            batch.setColor(1, 1, 1, 1);
            batch.draw(background, w, w + backgroundY);
            batch.draw(background, w, w + backgroundY + background.getHeight());
            batch.draw(overlay, w, w + overlayY);
            batch.draw(overlay, w, w + overlayY - overlay.getHeight());
        }

        particles.render(batch);
        enemies.render(batch);
        projectiles.render(batch);
        if (nebulaStrikeLevel < 24)
            player.render(batch);
        batch.end();

        uiBatch.begin();

        //This little section draws the text in the corner
        tintColor.set(baseColor);
        tintColor.lerp(pulseColor, nebulaStrikeChangePulse);
        font.setColor(tintColor);
        font.draw(uiBatch, nebulaStrikeText[nebulaStrikeLevel], 10, Gdx.graphics.getHeight() - 10);

        //Music
        if (nebulaStrikeLevel >= 21 && !music.isPlaying()) {
            music.play();
        } else if (nebulaStrikeLevel < 21 && music.isPlaying()) {
            music.pause();
        }

        //Credits
        font.setColor(baseColor);
        if (nebulaStrikeLevel == 22) {
            /* The last three arguments to BitmapFont.draw in this particular
             * configuration allow you to specify what intended line width you want,
             * whether you want the text to be right, center, or left aligned, and finally
             * if you want the text to wrap around to the chosen target width.
             * Keep in mind, even if you pick center- or right-aligned, you still place
             * the text according to the top-left of the rectangle bounding all the text
             * provided in the call. */
            font.draw(uiBatch, credits, Gdx.graphics.getWidth() * 0.05f,
                    Gdx.graphics.getHeight() * 7f / 8f, Gdx.graphics.getWidth() * 0.9f, 1, true);
        } else if (nebulaStrikeLevel == 23) {
            font.draw(uiBatch, further, Gdx.graphics.getWidth() * 0.05f,
                    Gdx.graphics.getHeight() * 3f / 4f, Gdx.graphics.getWidth() * 0.9f, 1, true);
        } else if (nebulaStrikeLevel == 24) {
            font.draw(uiBatch, "FIN", Gdx.graphics.getWidth() * 0.05f,
                    Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth() * 0.9f, 1, true);
        }
        uiBatch.end();
    }

    /**
     * Draw the pause message along with the Continue and Quit buttons
     * @param batch sprite batch
     */
    public void drawPaused(SpriteBatch batch) {
        batch.begin();

        // Determine position to draw
        float x = (camera.viewportWidth - BUTTON_WIDTH) / 2;
        //float y = (Gdx.graphics.getHeight() - 3 * (BUTTON_HEIGHT + BUTTON_SPACING)) / 2;
        float y = camera.viewportHeight - 2 * BUTTON_HEIGHT;

        // Draw paused message
        Label pauseLabel = new Label(font, "PAUSED",
                0, y, Gdx.graphics.getWidth(), BUTTON_HEIGHT,
                Label.Alignment.CENTER, Label.Alignment.CENTER
        );
        pauseLabel.draw(batch);

        // Draw Resume button with "TRY AGAIN" label
        resumeButton.setX(x);
        resumeButton.setY(y - (BUTTON_HEIGHT + BUTTON_SPACING));
        resumeButton.setText(font, "CONTINUE", Label.Alignment.CENTER, Label.Alignment.CENTER);
        resumeButton.draw(batch);

        // Draw Quit button
        quitButton.setX(x);
        quitButton.setY(y - 2 * (BUTTON_HEIGHT + BUTTON_SPACING));
        quitButton.setText(font, "QUIT", Label.Alignment.CENTER, Label.Alignment.CENTER);
        quitButton.draw(batch);
        batch.end();


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

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    /**
     * Called when a key was pressed
     * @param keycode one of the constants in Input.Keys
     * @return whether the input was processed
     */
    @Override
    public boolean keyDown(int keycode) {
        /*
        if (gameState == GameState.PLAYING) {

            if (keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE){
                // Respond to the back button click
                backKeySound.play(1F);
                isBackKeyPressed = true;
                return true;
            }
        }
        */
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
                //frog.setTowardTargetCenterPoint(new Vector2(screenX, Gdx.graphics.getHeight() - screenY));
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
