package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Presenting the enemies including the boss
 */
public class Enemy {

    // Enum for the enemy kind
    public enum EnemyKind { NORMAL, BOSS, BOUNTY }

    // Enemy's texture sheet and its corresponding frames along with its animation
    private Sprite sprite;
    private Texture enemySheet;
    private Animation enemyAnimation;
    private TextureRegion[] enemyFrames;
    private float stateTime;

    // Shooting animation
    private Animation shootingAnimation;
    private TextureRegion[] shootingFrames;
    private Animation noShootingAnimation;
    private TextureRegion[] noShootingFrames;

    // Instance for storing enemy kind
    public EnemyKind enemykind;

    // Constant for the enemy's frame
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final float FRAME_DURATION = 0.033f;

    // Enemy's size, coordinate, movement and velocity
    private float x, y;
    private Vector2 movement;
    private Vector2 velocity;

    // Flag for has fired
    private boolean hasFired;

    // Flag to determine whether the enemy is dead or not and set it to false by default
    public boolean dead = false;

    // Elapsed time for last call and when the boss fires its bullet (beam)
    private long timeElapsedSinceLastCalled;
    private long timeElapsedSinceBossFired;

    // Shooting cooldown
    private final long shootCooldownMillis = 400;
    private final long bossShootCooldownMillis = 2310;

    // Health
    private int health = 1;

    // Flag for checking boss has finished shooting or not
    private boolean bossFinishedShooting;

    // Array list for storing the bullets
    public ArrayList<Bullet> bullets;

    /**
     * Enemy's constructor
     * @param enemykind enemy kind
     */
    public Enemy(EnemyKind enemykind) {
        this.enemykind = enemykind;

        this.movement = new Vector2();
        this.velocity = new Vector2();
        this.sprite = new Sprite();
        this.dead = false;
        this.bossFinishedShooting = true;
        this.timeElapsedSinceBossFired = System.currentTimeMillis();

        this.initSprite();
        this.switchSprite(false);

        this.bullets = new ArrayList<Bullet>();
        stateTime = 0.0f;
    }

    /**
     * Initialising the enemy sprite
     */
    public void initSprite() {

        // Enemy sprite sheets
        switch (enemykind) {
            case NORMAL:
                enemySheet = new Texture(Constants.ENEMY_NORMAL_SPRITESHEET);
                this.health = 1;
                break;
            case BOSS:
                enemySheet = new Texture(Constants.ENEMY_BOSS_SPRITESHEET);
                this.health = 10;
                break;
            case BOUNTY:
                enemySheet = new Texture(Constants.ENEMY_BOUNTY_SPRITESHEET);
                this.health = 2;
                break;
        }
        TextureRegion[][] temp = TextureRegion.split(enemySheet, enemySheet.getWidth() / COLUMNS, enemySheet.getHeight() / ROWS);
        noShootingFrames = new TextureRegion[ROWS * COLUMNS];
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                noShootingFrames[index++] = temp[i][j];
            }
        }
        noShootingAnimation = new Animation<TextureRegion>(FRAME_DURATION, noShootingFrames);

        //Build shooting frames
        switch (enemykind) {
            case NORMAL:
                enemySheet = new Texture(Constants.ENEMY_NORMAL_SPRITESHEET_ALT);
                break;
            case BOSS:
                enemySheet = new Texture(Constants.ENEMY_BOSS_SPRITESHEET_ALT);
                break;
            case BOUNTY:
                enemySheet = new Texture(Constants.ENEMY_BOUNTY_SPRITESHEET_ALT);
                break;
        }
        temp = TextureRegion.split(enemySheet, enemySheet.getWidth() / COLUMNS, enemySheet.getHeight() / ROWS);
        shootingFrames = new TextureRegion[ROWS * COLUMNS];
        index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                shootingFrames[index++] = temp[i][j];
            }
        }
        shootingAnimation = new Animation<TextureRegion>(FRAME_DURATION, shootingFrames);
    }

    /**
     * Switching to shooting sprite and vice versa
     * @param isShooting flag to determine whether the enemy is shooting or not
     */
    public void switchSprite(Boolean isShooting) {
        if (isShooting) {
            enemyFrames = shootingFrames;
            enemyAnimation = shootingAnimation;
        } else {
            enemyFrames = noShootingFrames;
            enemyAnimation = noShootingAnimation;
        }
    }

    /**
     * Set the camera
     * @param camera orthographic camera
     * @param x camera's x coordiate
     * @param y camera's y coordiate
     */
    public void setPos(OrthographicCamera camera, float x, float y) {
        this.setX(camera.position.x - x);
        this.setY(camera.position.y - y);
    }

    /**
     * Properties for enemies' size, coordinate and health
     */
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float newX) {
        this.x = newX;
    }

    public void setY(float newY) {
        this.y = newY;
    }

    public float getWidth() {
        return enemyFrames[0].getRegionWidth();
    }

    public float getHeight() {
        return enemyFrames[0].getRegionHeight();
    }

    public int getHealth() { return this.health; }

    public void setHealth(int h) {
        this.health = h;
    }

    /**
     * Getter method for retrieving the that determines whether the enemy is dead or not
     * @return the enemy is dead flag
     */
    public boolean isDead() {
        return this.dead;
    }

    /**
     * Getter method for retrieving the has fired flag
     * @return
     */
    public boolean hasFired() {
        return hasFired;
    }

    /**
     * Draw the enemy
     * @param batch sprite batch
     */
    public void draw(SpriteBatch batch) {

        //Draw enemy
        batch.begin();

        // Determine current frame corresponding to the state time
        stateTime += Gdx.graphics.getDeltaTime();
        this.sprite = new Sprite((TextureRegion) enemyAnimation.getKeyFrame(stateTime, true));
        batch.draw(sprite, this.getX(), this.getY());
        batch.end();

        // Draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    /**
     * Enemies' movement
     * @param deltaTime delta time since the previous rendering time
     */
    public void move(float deltaTime) {
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();

        if (enemykind != EnemyKind.BOSS) {
            movement.y -= 1;
        }

        // enemies move toward to player
        if ((this.x + 50) > g.getPlayer().getX()) {
            movement.x -= 1;
        } else if ((this.x + 50) < g.getPlayer().getX()) {
            movement.x += 1;
        }

        if (movement.len2() > 1.0f) movement.nor();

        // Acceleration
        if (movement.len2() > 0.1f) {
            if (movement.len2() > 1.0f) movement.nor();
            float speed = Constants.ENEMY_SPEED;
            if (enemykind == EnemyKind.BOUNTY) {
                speed = Constants.ENEMY_SPEED * 3;
            }
            float max = speed - velocity.len();
            if (max > 0.0f) {
                float accel = Constants.ENEMY_ACCEL * deltaTime * speed;
                if (accel > max) accel = max;
                velocity.add(movement.scl(accel));
            }
        }

        this.setX(velocity.x * deltaTime + getX());

        if (enemykind != EnemyKind.BOSS) {
            this.setY(velocity.y * deltaTime + getY());
        }

        // Friction
        if (Math.abs(velocity.len2()) < 0.01f) {
            /* Floating-point numbers are very inaccurate and will rarely hit absolute
             * zero, so if we're effectively still, set velocity to zero explicitly to
             * prevent any jittering.*/
            velocity.set(0f, 0f);
        } else {
            float currentSpeed = velocity.len();
            float control = Math.max(currentSpeed, Constants.ENEMY_TRACTION);
            float drop = control * deltaTime;
            float newSpeed = currentSpeed - drop;
            if (newSpeed < 0.0f) newSpeed = 0.0f;
            newSpeed /= currentSpeed;
            velocity.scl(newSpeed);
        }
    }

    /**
     * Enemies' firing rate
     * @param kind enemy kind
     * @param timeElapsedWhenCalled elapsed time when called
     * @return false by default
     */
    private boolean firingTimer(EnemyKind kind, long timeElapsedWhenCalled) {
        long cooldown = 0;

        if (kind != EnemyKind.BOSS) {
            cooldown = shootCooldownMillis;
        } else {
            cooldown = bossShootCooldownMillis;
        }

        if (timeElapsedWhenCalled - timeElapsedSinceLastCalled >= cooldown) {
            timeElapsedSinceLastCalled = timeElapsedWhenCalled;
            return true;
        }
        return false;
    }

    /**
     * Updating the enemy
     * @param deltaTime delta time since the previous rendering time
     * @param camera orthographic camera
     */
    public void update(float deltaTime, OrthographicCamera camera) {

        // Move bullets
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }
        // Enemy is random shooting
        int rnd = MathUtils.random(1,100);
        if (enemykind != EnemyKind.BOSS) {
            if (rnd == 50) {
                hasFired = true;
                switchSprite(true);
                // Switch bullet type based on enemy type
                if (this.enemykind == EnemyKind.NORMAL) {
                    bullets.add(new Bullet(Bullet.BulletOwner.ENEMY, this.getX(), this.getY(), Constants.ENEMY_BULLET));

                } else if (this.enemykind == EnemyKind.BOUNTY) {
                    // Mix up attack pattern to make it more challenging
                    int shootPatternRnd = MathUtils.random(1, 100);
                    if (shootPatternRnd < 20) {
                        for (int i = 0; i < 5; ++i) {
                            bullets.add(new Bullet(Bullet.BulletOwner.ENEMY, this.getX() + (i * 15), this.getY() + (i * 15), Constants.ENEMY_BULLET_BOUNTY));
                            bullets.add(new Bullet(Bullet.BulletOwner.ENEMY, this.getX() + (-i * 15), this.getY() + (i * 15), Constants.ENEMY_BULLET_BOUNTY));
                        }
                    } else {
                        bullets.add(new Bullet(Bullet.BulletOwner.ENEMY, this.getX(), this.getY(), Constants.ENEMY_BULLET_BOUNTY));
                    }
                }
                for (int i = 0; i < bullets.size(); i++) {
                    if (bullets.get(i).hasExpired()) {
                        bullets.remove(bullets.get(i));
                    }
                }
            } else {
                if (firingTimer(this.enemykind, System.currentTimeMillis())) {
                    switchSprite(false);
                }
            }
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - timeElapsedSinceBossFired >= 3000) {
                long time = System.currentTimeMillis();
                for (int i = 0; i < 200; ++i) {
                    bullets.add(new Bullet(Bullet.BulletOwner.BOSS, this.getX(), (-camera.position.y / 2f + this.getHeight()) + i, Constants.ENEMY_BULLET_BOSS));
                }
                Sound snd = Gdx.audio.newSound(Gdx.files.internal("sound/boss_blast.mp3"));
                snd.play();
                timeElapsedSinceBossFired = currentTime;
            }
        }
    }
/**
 * Rectangle boundary for the enemy
 * @return the enemy's bound box
 */

    public Rectangle getBoundingRectangle() {
        if (enemykind == EnemyKind.BOSS) {
            return new Rectangle(this.getX() + 50, this.getY() + 100, getWidth() - 100, getHeight() - 200);
        }
        return new Rectangle(this.getX() + 20, this.getY() + 22, getWidth() / 3f, getHeight() / 3f);
    }

}
