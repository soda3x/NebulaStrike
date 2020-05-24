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
    private Music initial;
    private Music bgm;
    private boolean showHitboxes = false;
    private InputPoller input;

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
        player.setPos(camera, player.getWidth() / 2, 200);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
    }

    public void render(float f) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.update();

        player.draw();

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
