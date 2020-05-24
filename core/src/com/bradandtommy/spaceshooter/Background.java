package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Background {
    private SpriteBatch batch;
    private Sprite sprite;
    private Texture bgSheet;
    private Animation bgAnimation;
    private float stateTime;

    private float x, y;

    private static final int ROWS = 4;
    private static final int COLUMNS = 3;
    private static final float FRAME_DURATION = 0.120f;

    public Background() {
        this.batch = new SpriteBatch();
        this.sprite = new Sprite();
        bgSheet = new Texture(Gdx.files.internal("sprites/Background.png"));

        TextureRegion[][] temp = TextureRegion.split(bgSheet, bgSheet.getWidth() / COLUMNS, bgSheet.getHeight() / ROWS);
        TextureRegion[] bgFrames = new TextureRegion[ROWS * COLUMNS];
        int index = 0;
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                bgFrames[index++] = temp[i][j];
            }
        }
        bgAnimation = new Animation<TextureRegion>(FRAME_DURATION, bgFrames);
    }

    public void setPos(OrthographicCamera camera, float x, float y) {
        this.setX(camera.position.x - x);
        this.setY(camera.position.y - y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        TextureRegion[][] temp = TextureRegion.split(bgSheet, bgSheet.getWidth() / COLUMNS, bgSheet.getHeight() / ROWS);
        Sprite s = new Sprite(temp[0][0]);
        return s.getWidth();
    }

    public float getHeight() {
        TextureRegion[][] temp = TextureRegion.split(bgSheet, bgSheet.getWidth() / COLUMNS, bgSheet.getHeight() / ROWS);
        Sprite s = new Sprite(temp[0][0]);
        return s.getHeight();
    }

    public void setX(float newX) {
        this.x = newX;
    }

    public void setY(float newY) {
        this.y = newY;
    }

    public void draw() {
        batch.begin();
        stateTime += Gdx.graphics.getDeltaTime();
        this.sprite = render(stateTime);
        batch.draw(sprite, this.getX(), this.getY());
        batch.end();
    }

    public Sprite render(float stateTime) {
        return new Sprite((TextureRegion) bgAnimation.getKeyFrame(stateTime, true));
    }
}
