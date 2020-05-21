package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**Created by Michael Stopa on 5/4/2016. */
public class Projectiles {
    //Constants
    public static final int MAX_PROJECTILES = 128;
    public static final int IMPACTS_PER_HIT = 5;
    public enum Type { NONE, BORING_BULLET, BETTER_BULLET }

    //Type Data
    public Rectangle collider = new Rectangle();
    public TextureRegion boringBullet;
    public TextureRegion betterBullet;

    //Entity Data
    public Type[] type = new Type[MAX_PROJECTILES];
    public float[] x = new float[MAX_PROJECTILES];
    public float[] y = new float[MAX_PROJECTILES];
    public float[] vX = new float[MAX_PROJECTILES];
    public float[] vY = new float[MAX_PROJECTILES];
    public float[] lifetime = new float[MAX_PROJECTILES];

    public Projectiles() {
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            type[i] = Type.NONE;
        }
    }

    public void init() {
        GameScreen g = GameScreen.global;

        boringBullet = new TextureRegion(g.bullets, 169, 193, 7, 12);
        betterBullet = new TextureRegion(g.bullets, 74, 191, 14, 35);
    }

    public void dispose() {

    }

    public int spawn(Type t) {
        //An early-fail stupid check
        if (t == null) return -1;
        //Find a free index by looping through from the beginning
        int i = -1;
        for (int free = 0; free < MAX_PROJECTILES; free++) {
            if (type[free] == Type.NONE) {
                i = free;
                break;
            }
        }
        //Return a fail indicator if no free index was found
        if (i < 0) return -1;

        //Register the index as in-use
        type[i] = t;
        x[i] = 0f;
        y[i] = 0f;

        //Type-specific initialization
        switch(t) {
            case BORING_BULLET: {
                vX[i] = 0f;
                vY[i] = 650f;
                lifetime[i] = 2f;
                break;
            }
            case BETTER_BULLET: {
                vX[i] = 0f;
                vY[i] = 650f;
                lifetime[i] = 2f;
                break;
            }
        }

        return i;
    }

    public void update(float deltaTime) {
        GameScreen g = GameScreen.global;
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (type[i] == Type.NONE) continue;
            //Recycle dead projectiles to free their memory for use by new particles
            if (lifetime[i] < 0f) {
                type[i] = Type.NONE;
                continue;
            }
            lifetime[i] -= deltaTime;
            x[i] += vX[i] * deltaTime;
            y[i] += vY[i] * deltaTime;

            switch (type[i]) {
                case BORING_BULLET: {
                    collider.setSize(boringBullet.getRegionWidth(), boringBullet.getRegionHeight());
                    break;
                } case BETTER_BULLET: {
                    collider.setSize(betterBullet.getRegionWidth(), betterBullet.getRegionHeight());
                    break;
                }
            }
            collider.setCenter(x[i], y[i]);

            //Check against enemies
            Enemies en = g.enemies;
            for (int e = 0; e < Enemies.MAX_ENEMIES; e++) {
                if (en.type[e] == Enemies.Type.NONE) {
                    continue;
                } else if (collider.overlaps(en.collider[e])) {
                    int dmg = 1;
                    if (g.nebulaStrikeLevel >= 19) dmg = 3;
                    en.damage(e, dmg);
                    type[i] = Type.NONE;
                    //Impact Particle
                    if (g.nebulaStrikeLevel >= 12) {
                        Particles pa = g.particles;
                        for (int m = 0; m < IMPACTS_PER_HIT; m++) {
                            int im = pa.spawn(Particles.Type.IMPACT);
                            pa.x[im] = x[i];
                            pa.y[im] = y[i];
                            pa.vX[im] += vX[i] / 10f;
                            pa.vY[im] += vY[i] / 10f;
                        }
                    }
                }
            }

            if (!collider.overlaps(g.worldCollider)) type[i] = Type.NONE;
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            TextureRegion reg = null;
            switch(type[i]) {
                case NONE: continue;
                case BORING_BULLET: {
                    reg = boringBullet;
                    break;
                }
                case BETTER_BULLET: {
                    reg = betterBullet;
                    break;
                }
            }
            batch.setColor(1, 1, 1, 1);
            batch.draw(reg,
                    x[i] - reg.getRegionWidth()/2f,
                    y[i] - reg.getRegionHeight()/2f);
        }
    }
}
