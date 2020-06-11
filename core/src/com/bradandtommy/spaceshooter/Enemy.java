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

public class Enemy {

    public enum EnemyKind { NORMAL, BOSS, BOUNTY }

    private Sprite sprite;
    private Texture enemySheet;
    private Animation enemyAnimation;
    private TextureRegion[] enemyFrames;
    private float stateTime;

    private Animation shootingAnimation;
    private TextureRegion[] shootingFrames;
    private Animation noShootingAnimation;
    private TextureRegion[] noShootingFrames;
    public EnemyKind enemykind;


    public boolean dead = false;

    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final float FRAME_DURATION = 0.033f;

    private float x, y;
    private Vector2 movement;
    private Vector2 velocity;
    private boolean hasFired;
    private long timeElapsedSinceLastCalled;
    private long timeElapsedSinceBossFired;
    private final long shootCooldownMillis = 400;
    private final long bossShootCooldownMillis = 2310;
    private int health = 1;
    private boolean bossFinishedShooting;

    public ArrayList<Bullet> bullets;

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

    public void initSprite() {
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

        /**
         * Build shooting frames
         */
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

    public void switchSprite(Boolean isShooting) {
        if (isShooting) {
            enemyFrames = shootingFrames;
            enemyAnimation = shootingAnimation;
        } else {
            enemyFrames = noShootingFrames;
            enemyAnimation = noShootingAnimation;
        }
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

    public void setX(float newX) {
        this.x = newX;
    }

    public void setY(float newY) {
        this.y = newY;
    }

    public boolean isDead() {
        return this.dead;
    }

    public void draw(SpriteBatch batch) {
        /** Draw enemy */
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

    public void move(float deltaTime) {
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();

        //movement.set(0f, 0f);
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

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int h) {
        this.health = h;
    }

    public boolean hasFired() {
        return hasFired;
    }

    public float getWidth() {
        return enemyFrames[0].getRegionWidth();
    }

    public float getHeight() {
        return enemyFrames[0].getRegionHeight();
    }

    public Rectangle getBoundingRectangle() {
        if (enemykind == EnemyKind.BOSS) {
            return new Rectangle(this.getX() + 50, this.getY() + 100, getWidth() - 100, getHeight() - 200);
        }
        return new Rectangle(this.getX() + 20, this.getY() + 22, getWidth() / 3f, getHeight() / 3f);
    }

    public void dispose(){
        this.sprite.getTexture().dispose();
        this.bullets = new ArrayList<Bullet>();
        Bullet bullet;
        for (int i = bullets.size() - 1; i >= 0; i--) {
            bullet = bullets.remove(i);
            bullet.dispose();
        }
        enemySheet.dispose();
    }
}
