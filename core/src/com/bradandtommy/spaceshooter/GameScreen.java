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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

class GameScreen implements Screen, InputProcessor {
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

    private void create() {
        input = new InputPoller();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        initial = Gdx.audio.newMusic(Gdx.files.internal("music/start.mp3"));
        initial.setLooping(false);
        initial.play();

        if (initial.isPlaying()) {
            bgm = Gdx.audio.newMusic(Gdx.files.internal("music/level.mp3"));
            bgm.setLooping(true);
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

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        bg1 = new Texture(Gdx.files.internal("sprites/Background_alt.png"));
        bg2 = new Texture(Gdx.files.internal("sprites/Background_alt.png"));
        yMax = 480;
        yCoordBg1 = 0;
        yCoordBg2 = yMax;
    }

    public void render(float f) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.update();

        // Scrolling Background
        yCoordBg1 -= BG_MOVE_SPEED * Gdx.graphics.getDeltaTime();
        yCoordBg2 = yCoordBg1 - yMax;  // We move the background, not the camera
        if (yCoordBg1 < 0) {
            yCoordBg1 = yMax;
            yCoordBg2 = 0;
        }
        batch.begin();
        batch.draw(bg1, 0, yCoordBg1);
        batch.draw(bg2, 0, yCoordBg2);
        batch.end();

        player.draw();
//        bg.draw();

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

        if (showHitboxes) {
            ShapeRenderer sr = new ShapeRenderer();
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            sr.setAutoShapeType(true);
            sr.rect(this.player.getBoundingRectangle().getX(), this.player.getBoundingRectangle().getY(), this.player.getBoundingRectangle().getWidth(), this.player.getBoundingRectangle().getHeight());
            sr.end();
        }
    }

    private void update() {
        input.poll();

        camera.update();

        player.move(Gdx.graphics.getDeltaTime());

        // DEBUG: Toggle hitboxes if H is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (showHitboxes) {
                showHitboxes = false;
            } else {
                showHitboxes = true;
            }
        }
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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
