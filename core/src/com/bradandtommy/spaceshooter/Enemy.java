package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
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

    // ADDED BY TOMMY
    // The enum for enemy kinds
    public enum EnemyKind { NORMAL, BOSS, BOUNTY }

    private Sprite sprite;
    private Texture enemySheet;
    private Animation enemyAnimation;
    private TextureRegion[] enemyFrames;
    private float stateTime;

    // ADDED BY TOMMY
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

    public ArrayList<Bullet> bullets;

    public Enemy(EnemyKind enemykind) {
        // ADDED BY TOMMY - including the parameter
        this.enemykind = enemykind;

        this.movement = new Vector2();
        this.velocity = new Vector2();
        this.sprite = new Sprite();
        this.dead = false;

        // REPLACED BY TOMMY
        //this.initSprite(Constants.ENEMY_SPRITESHEET);
        this.initSprite();
        this.switchSprite(false);

        this.bullets = new ArrayList<Bullet>();
        stateTime = 0.0f;
    }

    /*public void initSprite(String spriteFilePath) {
        enemySheet = new Texture(Gdx.files.internal(spriteFilePath));

        TextureRegion[][] temp = TextureRegion.split(enemySheet, enemySheet.getWidth() / COLUMNS, enemySheet.getHeight() / ROWS);
        enemyFrames = new TextureRegion[ROWS * COLUMNS];
        int index = 0;
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                //temp[i][j].flip(false, true);
                enemyFrames[index++] = temp[i][j];
            }
        }
        enemyAnimation = new Animation<TextureRegion>(FRAME_DURATION, enemyFrames);
    }*/

    // REPLACE BY TOMMY - the old init with this new init
    public void initSprite() {
        String textureSheetPath = "";

        /**
         * Build no shooting frames
         */
        switch (enemykind) {
            case NORMAL:
                textureSheetPath = Constants.ENEMY_NORMAL_SPRITESHEET;
                break;
            case BOSS:
                textureSheetPath = Constants.ENEMY_BOSS_SPRITESHEET;
                break;
            case BOUNTY:
                textureSheetPath = Constants.ENEMY_BOUNTY_SPRITESHEET;
                break;
        }
        Texture enemySheet = new Texture(Gdx.files.internal(textureSheetPath));
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
                textureSheetPath = Constants.ENEMY_NORMAL_SPRITESHEET_ALT;
                break;
            case BOSS:
                textureSheetPath = Constants.ENEMY_BOSS_SPRITESHEET_ALT;
                break;
            case BOUNTY:
                textureSheetPath = Constants.ENEMY_BOUNTY_SPRITESHEET_ALT;
                break;
        }
        enemySheet = new Texture(Gdx.files.internal(textureSheetPath));
        temp = TextureRegion.split(enemySheet, enemySheet.getWidth() / COLUMNS, enemySheet.getHeight() / ROWS);
        shootingFrames = new TextureRegion[ROWS * COLUMNS];
        index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                shootingFrames[index++] = temp[i][j];
            }
        }
        shootingAnimation = new Animation<TextureRegion>(FRAME_DURATION, shootingFrames);

        enemySheet.dispose();
    }

    // ADDED BY TOMMY - switch frames
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
        movement.y -= 1;

        // ADDED BY TOMMY - enemies move toward to player
        if (this.x > g.getPlayer().getX()) {
            movement.x -= 1;
        } else if (this.x < g.getPlayer().getX()) {
            movement.x += 1;
        }

        if (movement.len2() > 1.0f) movement.nor();

        //Acceleration
        if (movement.len2() > 0.1f) {
            if (movement.len2() > 1.0f) movement.nor();
            float speed = Constants.ENEMY_SPEED;
            float max = speed - velocity.len();
            if (max > 0.0f) {
                float accel = Constants.ENEMY_ACCEL * deltaTime * speed;
                if (accel > max) accel = max;
                velocity.add(movement.scl(accel));
            }
        }

        this.setX(velocity.x * deltaTime + getX());
        this.setY(velocity.y * deltaTime + getY());

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

    public void update(float deltaTime) {
        // Move bullets
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }
        // Enemy is random shooting
        int rnd = MathUtils.random(1,100);
        if (rnd == 50) {
            hasFired = true;
            switchSprite(true);
            bullets.add(new Bullet(Bullet.BulletOwner.ENEMY, this.getX(), this.getY(), Constants.ENEMY_BULLET));
            for (int i = 0; i < bullets.size(); i++) {
                if (bullets.get(i).hasExpired()) {

                    //bullets.remove(bullets.get(i));
                    Bullet bullet = bullets.remove(i);
                    //bullet.dispose();
                    continue;
                }
            }
        } else {
            switchSprite(false);
        }
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
        return new Rectangle(this.getX() + 20, this.getY() + 22, getWidth() / 3f, getHeight() / 3f);
    }

}
