package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Background {

    private Texture bg1, bg2;
    private float yMax, yCoordBg1, yCoordBg2;
    private final int BG_MOVE_SPEED = 200;
    private static Background instance;

    private Background() {
        this.create();
    }

    public static Background getBackgroundInstance() {
        if (instance == null) {
            instance = new Background();
        }
        return instance;
    }


    public void create() {
        this.bg1 = new Texture(Gdx.files.internal(Constants.SCROLLING_BG_IMAGE));
        this.bg2 = new Texture(Gdx.files.internal(Constants.SCROLLING_BG_IMAGE));
        this.yMax = 480;
        this.yCoordBg1 = 0;
        this.yCoordBg2 = yMax;
    }

    public void update(SpriteBatch batch) {
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
    }


}
