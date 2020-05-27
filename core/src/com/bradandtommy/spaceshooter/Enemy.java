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
    private Sprite sprite;
    private Texture enemySheet;
    private Animation enemyAnimation;
    private TextureRegion[] enemyFrames;
    private float stateTime;

    public boolean dead = false;

    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final float FRAME_DURATION = 0.033f;

    private float x, y;
    private Vector2 movement;
    private Vector2 velocity;
    private boolean hasFired;

    public ArrayList<Bullet> bullets;

    public Enemy() {
        this.movement = new Vector2();
        this.velocity = new Vector2();
        this.sprite = new Sprite();
        this.dead = false;
        this.initSprite(Constants.ENEMY_SPRITESHEET);
        this.bullets = new ArrayList<Bullet>();
        stateTime = 0.0f;
    }

    public void initSprite(String spriteFilePath) {
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
            initSprite(Constants.ENEMY_SPRITESHEET_ALT);
            bullets.add(new Bullet(Bullet.BulletOwner.ENEMY, this.getX(), this.getY(), Constants.ENEMY_BULLET));
            for (int i = 0; i < bullets.size(); ++i) {
                if (bullets.get(i).hasExpired()) {
                    bullets.remove(bullets.get(i));
                    continue;
                }
            }
        } else {
            initSprite(Constants.ENEMY_SPRITESHEET);
        }
    }

    public boolean hasFired() {
        /*
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();
        if (g.getInputPoller().shoot.isDown) {
            hasFired = true;
        }
         */
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
