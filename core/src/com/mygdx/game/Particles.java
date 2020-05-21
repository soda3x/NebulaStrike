package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**Created by Michael Stopa on 5/4/2016. */
public class Particles {
    //Constants
    public static final int MAX_PARTICLES = 512;
    public static final float PLAYER_TRAIL_LIFETIME = 0.1f;
    public static final float IMPACT_LIFETIME = 0.2f;
    public static final float IMPACT_SCATTER = 60f;
    public static final float EXPLOSION_LIFETIME = 0.6f;
    public static final float EXPLOSION_SCATTER = 600f;
    public enum Type { NONE, PLAYER_TRAIL, ENEMY_TRAIL, MUZZLE_FLASH, IMPACT, EXPLOSION }

    //Type Data
    TextureRegion playerTrailTextures[] = new TextureRegion[3];
    TextureRegion enemyTrailTextures[] = new TextureRegion[3];
    TextureRegion muzzleFlash;
    TextureRegion impact;
    TextureRegion explosions[] = new TextureRegion[3];

    //Entity Data
    public Type[] type = new Type[MAX_PARTICLES];
    /**Position in the world. */
    public float[] x = new float[MAX_PARTICLES];
    public float[] y = new float[MAX_PARTICLES];
    /**Velocity applied per-second. */
    public float[] vX = new float[MAX_PARTICLES];
    public float[] vY = new float[MAX_PARTICLES];
    public float[] lifetime = new float[MAX_PARTICLES];

    public Particles() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            type[i] = Type.NONE;
        }
    }

    public void init() {
        GameScreen g = GameScreen.global;

        playerTrailTextures[0] = new TextureRegion(g.bullets, 167, 215, 11, 14);
        playerTrailTextures[1] = new TextureRegion(g.bullets, 183, 213, 11, 14);
        playerTrailTextures[2] = new TextureRegion(g.bullets, 201, 212, 11, 14);

        enemyTrailTextures[0] = new TextureRegion(g.bullets, 167, 107, 11, 14);
        enemyTrailTextures[0].flip(false, true);
        enemyTrailTextures[1] = new TextureRegion(g.bullets, 183, 105, 11, 14);
        enemyTrailTextures[1].flip(false, true);
        enemyTrailTextures[2] = new TextureRegion(g.bullets, 201, 104, 11, 14);
        enemyTrailTextures[2].flip(false, true);

        muzzleFlash = new TextureRegion(g.bullets, 201, 187, 11, 18);
        muzzleFlash.flip(false, true);

        impact = new TextureRegion(g.bullets, 201, 212, 11, 14);

        explosions[0] = new TextureRegion(g.bullets, 236, 57, 8, 8);
        explosions[1] = new TextureRegion(g.bullets, 253, 56, 9, 9);
        explosions[2] = new TextureRegion(g.bullets, 271, 54, 11, 11);
    }

    public void dispose() {
        //TextureRegions don't need to be disposed, only their parent textures
    }

    public int spawn(Type t) {
        //An early-fail stupid check
        if (t == null) return -1;
        //Find a free index by looping through from the beginning
        int i = -1;
        for (int free = 0; free < MAX_PARTICLES; free++) {
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
            case PLAYER_TRAIL: {
                vX[i] = 0f;
                vY[i] = -600f;
                lifetime[i] = PLAYER_TRAIL_LIFETIME;
                break;
            } case ENEMY_TRAIL: {
                vX[i] = 0f;
                vY[i] = 600f;
                lifetime[i] = PLAYER_TRAIL_LIFETIME;
                break;
            } case MUZZLE_FLASH: {
                vX[i] = 0f;
                vY[i] = 50f;
                lifetime[i] = 0.03f;
                break;
            } case IMPACT: {
                vX[i] = MathUtils.random(-IMPACT_SCATTER, IMPACT_SCATTER);
                vY[i] = MathUtils.random(-IMPACT_SCATTER, IMPACT_SCATTER);
                lifetime[i] = IMPACT_LIFETIME;
                break;
            } case EXPLOSION: {
                vX[i] = MathUtils.random(-EXPLOSION_SCATTER, EXPLOSION_SCATTER);
                vY[i] = MathUtils.random(-EXPLOSION_SCATTER, EXPLOSION_SCATTER);
                lifetime[i] = EXPLOSION_LIFETIME;
                break;
            }

        }

        return i;
    }

    public void update(float deltaTime) {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (type[i] == Type.NONE) continue;
            //Recycle dead particles to free their memory for use by new particles
            if (lifetime[i] < 0f) {
                type[i] = Type.NONE;
                continue;
            }
            lifetime[i] -= deltaTime;
            x[i] += vX[i] * deltaTime;
            y[i] += vY[i] * deltaTime;
        }
    }

    public void render(SpriteBatch batch) {
        GameScreen g = GameScreen.global;
        for (int i = 0; i < MAX_PARTICLES; i++) {
            switch(type[i]) {
                case NONE: break;
                case PLAYER_TRAIL: {
                    int tex = (int) Math.max(lifetime[i] / PLAYER_TRAIL_LIFETIME * playerTrailTextures.length, 0);
                    TextureRegion reg = playerTrailTextures[tex];
                    batch.setColor(1f, 1f, 1f, Math.max(lifetime[i] / PLAYER_TRAIL_LIFETIME, 0f));
                    batch.draw(reg,
                            x[i] - reg.getRegionWidth()/2f,
                            y[i] - reg.getRegionHeight()/2f);
                    break;
                } case ENEMY_TRAIL: {
                    int tex = (int) Math.max(lifetime[i] / PLAYER_TRAIL_LIFETIME * enemyTrailTextures.length, 0);
                    TextureRegion reg = enemyTrailTextures[tex];
                    batch.setColor(1f, 1f, 1f, Math.max(lifetime[i] / PLAYER_TRAIL_LIFETIME, 0f));
                    batch.draw(reg,
                            x[i] - reg.getRegionWidth()/2f,
                            y[i] - reg.getRegionHeight()/2f);
                    break;
                } case MUZZLE_FLASH: {
                    TextureRegion reg = muzzleFlash;
                    batch.setColor(1, 1, 1, 1);
                    /* Muzzle flashes look a lot better when snapped to their source.
                     * Doing so is simply a matter of drawing it relative to whatever
                     * entity spawned it, the player in this case */
                    batch.draw(reg,
                            g.player.sprite.getX() + x[i] - reg.getRegionWidth(),
                            g.player.sprite.getY() + y[i] - reg.getRegionHeight(),
                            reg.getRegionWidth() * 2, reg.getRegionHeight() * 2);
                    break;
                } case IMPACT: {
                    TextureRegion reg = impact;
                    batch.setColor(1, 1, 1, Math.max(lifetime[i] / IMPACT_LIFETIME, 0f));
                    batch.draw(reg,
                            x[i] - reg.getRegionWidth(),
                            y[i] - reg.getRegionHeight());
                    break;
                } case EXPLOSION: {
                    //Pick a random one to display
                    TextureRegion reg = explosions[i % explosions.length];
                    batch.setColor(1, 1, 1, Math.max(lifetime[i] / EXPLOSION_LIFETIME, 0f));
                    batch.draw(reg,
                            x[i] - reg.getRegionWidth(),
                            y[i] - reg.getRegionHeight());
                    break;
                }
            }
        }
    }
}
