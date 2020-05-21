package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**Created by Michael Stopa on 5/4/2016. */
public class Enemies {
    //Constants
    public static final int MAX_ENEMIES = 12;
    public static final int EXPLOSION_PIECES = 50;
    public enum Type { NONE, BUGSHIP }

    //Type Data
    TextureRegion enemyBugShip;
    Sound hit;
    Sound death;
    /**We don't want this sound to play too much, so we put it on a really short cooldown
     * to keep it from being overplayed */
    float hitReplayCooldown = 0f;
    float hitReplayTime = 0.08f;

    //Entity Data
    public Type[] type = new Type[MAX_ENEMIES];
    public Sprite[] sprite = new Sprite[MAX_ENEMIES];
    public Vector2[] velocity = new Vector2[MAX_ENEMIES];
    public Vector2[] impulse = new Vector2[MAX_ENEMIES];
    public Rectangle[] collider = new Rectangle[MAX_ENEMIES];
    public int[] health = new int[MAX_ENEMIES];
    public float[] hitEffect = new float[MAX_ENEMIES];

    public Enemies() {
        for (int i = 0; i < MAX_ENEMIES; i++) {
            type[i] = Type.NONE;
            velocity[i] = new Vector2();
            impulse[i] = new Vector2();
            collider[i] = new Rectangle();
            health[i] = 0;
            hitEffect[i] = 0f;
        }
    }

    public void init() {
        enemyBugShip = new TextureRegion(GameScreen.global.ships, 445, 61, 58, 68);
        hit = Gdx.audio.newSound(Gdx.files.internal("Hit_Hurt2.wav"));
        death = Gdx.audio.newSound(Gdx.files.internal("Explosion3.wav"));
        for (int i = 0; i < MAX_ENEMIES; i++) {
            sprite[i] = new Sprite(enemyBugShip);
        }
    }

    public void dispose() {
        hit.dispose();
        death.dispose();
    }

    public int spawn(Type t) {
        //An early-fail stupid check
        if (t == null) return -1;
        //Find a free index by looping through from the beginning
        int i = -1;
        for (int free = 0; free < MAX_ENEMIES; free++) {
            if (type[free] == Type.NONE) {
                i = free;
                break;
            }
        }
        //Return a fail indicator if no free index was found
        if (i < 0) return -1;

        //Register the index as in-use
        type[i] = t;

        //Type-specific initialization
        GameScreen g = GameScreen.global;
        switch(t) {
            case BUGSHIP: {
                sprite[i].setTexture(enemyBugShip.getTexture());
                sprite[i].setRegion(enemyBugShip.getRegionX(), enemyBugShip.getRegionY(),
                        enemyBugShip.getRegionWidth(), enemyBugShip.getRegionHeight());
                //Spawn at a random point along the top of the screen
                sprite[i].setCenter(
                        MathUtils.random(enemyBugShip.getRegionWidth(),
                                Gdx.graphics.getWidth() - enemyBugShip.getRegionWidth()),
                        Gdx.graphics.getHeight());
                collider[i].setSize(sprite[i].getWidth(), sprite[i].getRegionHeight());
                velocity[i].set(0f, -20f - MathUtils.random(5f)); //Vary up their speed
                health[i] = 20;
                impulse[i].setZero();

                //Spawn impulse
                if (g.nebulaStrikeLevel >= 17) {
                    impulse[i].y = -750;
                }
                break;
            }
        }

        return i;
    }

    public void update(float deltaTime) {
        GameScreen g = GameScreen.global;
        if (hitReplayCooldown > 0f) hitReplayCooldown -= deltaTime;

        //Spawn new enemies
        int max = (g.nebulaStrikeLevel >= 19) ? MAX_ENEMIES : 4;
        if (getNumAlive() < max) spawn(Type.BUGSHIP);

        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] == Type.NONE) continue;

            //Erratic Movement
            if (g.nebulaStrikeLevel >= 18) {
                long time = System.currentTimeMillis();
                time += i * 1000;
                time %= 6000;
                float maneuver = (float)time / 6000.0f * 2.0f * (float)Math.PI;
                velocity[i].x = (float)Math.sin(maneuver) * 5.0f;
            }

            sprite[i].translate(velocity[i].x * deltaTime, velocity[i].y * deltaTime);
            //Move faster (dodgy hack, just change the actual move speed
            if (g.nebulaStrikeLevel >= 19) {
                sprite[i].translate(velocity[i].x * 3f * deltaTime, velocity[i].y * 3f * deltaTime);
            }

            sprite[i].translate(impulse[i].x * deltaTime, impulse[i].y * deltaTime);
            impulse[i].scl(1.0f - Math.min(1.0f, deltaTime * 10.0f));
            collider[i].setPosition(sprite[i].getX(), sprite[i].getY());
            if (! g.worldCollider.overlaps(collider[i])) {
                type[i] = Type.NONE;
            }
            //Engine Trail
            if (g.nebulaStrikeLevel >= 2) {
                Particles p = GameScreen.global.particles;
                int j = p.spawn(Particles.Type.ENEMY_TRAIL);
                p.x[j] = sprite[i].getX() + 29 + MathUtils.random(-1, 1);
                p.y[j] = sprite[i].getY() + 50 + MathUtils.random(-2, 2);
            }

            if (hitEffect[i] >= 0f) hitEffect[i] -= deltaTime;
        }
    }

    public void damage(int i, int dmg) {
        GameScreen g = GameScreen.global;
        health[i] -= dmg;
        hitEffect[i] = 0.03f;

        //HIt sound
        if (g.nebulaStrikeLevel >= 11 && hitReplayCooldown <= 0f) {
            hit.play();
            hitReplayCooldown = hitReplayTime;
        }

        //Knockback
        if (g.nebulaStrikeLevel >= 14) {
            impulse[i].y += 30f + MathUtils.random(10f);
            impulse[i].x += MathUtils.random(-10f, 10f);
        }

        if (health[i] < 1) {
            //Death Effects
            if (g.nebulaStrikeLevel >= 15) {
                death.play();
                g.screenShake += 0.1f;
                Particles p = g.particles;
                for (int x = 0; x < EXPLOSION_PIECES; x++) {
                    int pa = p.spawn(Particles.Type.EXPLOSION);
                    p.x[pa] = sprite[i].getX() + sprite[i].getRegionWidth() / 2f;
                    p.y[pa] = sprite[i].getY() + sprite[i].getRegionHeight() / 2f;
                }
            }
            type[i] = Type.NONE;
        }
    }

    public int getNumAlive() {
        int count = 0;
        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] != Type.NONE) count++;
        }
        return count;
    }

    public void render(SpriteBatch batch) {
        GameScreen g = GameScreen.global;
        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] == Type.NONE) continue;
            if (hitEffect[i] > 0.0f && g.nebulaStrikeLevel >= 13) {
                batch.setColor(1, 1, 1, 1);
                batch.setShader(g.alphaShader);
                sprite[i].draw(batch);
                batch.setShader(null);
            } else {
                sprite[i].draw(batch);
            }
        }
    }
}
