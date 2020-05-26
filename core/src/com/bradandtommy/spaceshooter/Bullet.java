package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {

    public static final int SPEED = 250;
    private Sprite sprite;
    private float x, y;
    private boolean expired = false;
    private float time;

    public Bullet(float x, float y, String bulletType) {
        this.x = x;
        this.y = y;
        Texture texture = new Texture(bulletType);
        sprite = new Sprite(texture);
    }

    public void update(float deltaTime) {
        this.y += SPEED * deltaTime;
        time -= deltaTime;
        if (this.y >= Gdx.graphics.getHeight()) {
            this.expired = true;
        }
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
        return new Rectangle(this.getX(), this.getY(), sprite.getWidth(), sprite.getHeight());
    }

}