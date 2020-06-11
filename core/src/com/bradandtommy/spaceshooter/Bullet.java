package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Class used to store the enemies and player's bullets
 */
public class Bullet {

    // The enum for owner of bullets
    public enum BulletOwner { PLAYER, ENEMY, BOSS }

    // Constant for the bullet speed
    public static final int SPEED = 250;

    // Sprite for drawing the bullet
    private Sprite sprite;

    // Bullet's coordinate
    private float x, y;

    // Expiring flag for the bullet
    private boolean expired = false;

    //
    private float time;

    // Instance for bullet owner
    private BulletOwner owner;

    /**
     * Bullet class constructor
     * @param owner bullet's owner
     * @param x x coordinate for the bullet
     * @param y y coordinate for the bullet
     * @param bulletType type of bullet
     */
    public Bullet(BulletOwner owner, float x, float y, String bulletType) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        Texture texture = new Texture(bulletType);
        sprite = new Sprite(texture);
    }

    /**
     * Updating the bullet
     * @param deltaTime delta time since the previous rendering time
     */
    public void update(float deltaTime) {

        // Assigning the bullet to the appropriated owner
        if (this.owner == BulletOwner.PLAYER) {
            this.y += SPEED * deltaTime;
            if (this.y >= Gdx.graphics.getHeight()) {
                this.expired = true;
            }
        } else {
            this.y -= ((SPEED + Constants.ENEMY_SPEED)  * deltaTime);
            if (this.y <= 0) {
                this.expired = true;
            }
        }
        time -= deltaTime;

    }

    /**
     * Draw the bullet
     * @param batch sprite batch
     */
    public void draw(SpriteBatch batch) {
        batch.begin();
        batch.draw(sprite, x, y);
        batch.end();
    }

    /**
     * Expired flag properties
     */
    public boolean hasExpired() { return expired; }

    public void setExpired(boolean expired){
        this.expired = expired;
    }

    /**
     * Coordinate and size properties
     */
    public float getX() { return x; }
    public float getY() { return y; }

    public float getWidth() { return sprite.getWidth(); }

    public float getHeight() { return sprite.getHeight(); }

    /**
     * Rectangle boundary for the bullet
     * @return the bullet's bound box
     */
    public Rectangle getBoundingRectangle() {
        if (owner == BulletOwner.BOSS) {
            return new Rectangle(this.getX() + 64, this.getY(), sprite.getWidth() - 128, sprite.getHeight());
        }
        return new Rectangle(this.getX() + 15, this.getY() + 20, sprite.getWidth() - 30f, sprite.getHeight() - 50);
    }
}