package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represent the game's background code
 */
public class Background {

    // Background music
    private Texture bg1, bg2;

    // Coordinate for scrolling background
    private float yMax, yCoordBg1, yCoordBg2;

    // Constant for the scrolling background's speed
    private final int BG_MOVE_SPEED = 200;

    // Background's instance
    private static Background instance;

    /**
     *  Background constructor
     */
    private Background() {
        this.create();
    }

    /**
     * Get the class instance
     * @return the background instance
     */
    public static Background getBackgroundInstance() {
        if (instance == null) {
            instance = new Background();
        }
        return instance;
    }


    /**
     * Create the scrolling background
     */
    public void create() {
        this.bg1 = new Texture(Gdx.files.internal(Constants.SCROLLING_BG_IMAGE));
        this.bg2 = new Texture(Gdx.files.internal(Constants.SCROLLING_BG_IMAGE));
        this.yMax = 480;
        this.yCoordBg1 = 0;
        this.yCoordBg2 = yMax;
    }

    /**
     * Updating the scrolling background
     * @param batch sprite batch
     */
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
