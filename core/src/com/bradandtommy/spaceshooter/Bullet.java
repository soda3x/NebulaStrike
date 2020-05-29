package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    // The enum for owner of bullets
    public enum BulletOwner { PLAYER, ENEMY }

    public static final int SPEED = 250;
    private Sprite sprite;
    private float x, y;
    private boolean expired = false;
    private float time;

    private BulletOwner owner;

    public Bullet(BulletOwner owner, float x, float y, String bulletType) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        Texture texture = new Texture(bulletType);
        sprite = new Sprite(texture);
    }

    public void update(float deltaTime) {
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

    public boolean hasExpired() { return expired; }

    public void draw(SpriteBatch batch) {
        batch.begin();
        batch.draw(sprite, x, y);
        batch.end();
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public float getWidth() { return sprite.getWidth(); }

    public float getHeight() { return sprite.getHeight(); }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(this.getX() + 15, this.getY() + 20, sprite.getWidth() - 30f, sprite.getHeight() - 50);
    }



}