package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player {
    private SpriteBatch batch;
    private Sprite sprite;
    private Texture playerSheet;
    private Animation playerAnimation;
    private float stateTime;

    private boolean dead = false;

    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final float FRAME_DURATION = 0.033f;

    private float x, y;
    private Vector2 movement;
    private Vector2 velocity;
    private boolean hasFired;

    private ArrayList<Bullet> bullets;


    public Player() {
        this.batch = new SpriteBatch();
        this.movement = new Vector2();
        this.velocity = new Vector2();
        this.sprite = new Sprite();
        this.dead = false;
        this.initSprite(Constants.PLAYER_SPRITESHEET);
        this.bullets = new ArrayList<Bullet>();
    }

    public void initSprite(String spriteFilePath) {
        playerSheet = new Texture(Gdx.files.internal(spriteFilePath));

        TextureRegion[][] temp = TextureRegion.split(playerSheet, playerSheet.getWidth() / COLUMNS, playerSheet.getHeight() / ROWS);
        TextureRegion[] playerFrames = new TextureRegion[ROWS * COLUMNS];
        int index = 0;
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                playerFrames[index++] = temp[i][j];
            }
        }
        playerAnimation = new Animation<TextureRegion>(FRAME_DURATION, playerFrames);
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

    public void draw() {
        batch.begin();
        stateTime += Gdx.graphics.getDeltaTime();
        this.sprite = render(stateTime);
        batch.draw(sprite, this.getX(), this.getY());
        batch.end();
    }

    public Sprite render(float stateTime) {
        return new Sprite((TextureRegion) playerAnimation.getKeyFrame(stateTime, true));
    }

    public void move(float deltaTime) {
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();
        /* Instead of applying velocity directly, have it persist between frames so
         * we can have noticeable acceleration/deceleration */
        movement.set(0f, 0f);
        if (g.getInputPoller().moveLeft.isDown) movement.x -= 1;
        if (g.getInputPoller().moveRight.isDown) movement.x += 1;
        if (g.getInputPoller().moveDown.isDown) movement.y -= 1;
        if (g.getInputPoller().moveUp.isDown) movement.y += 1;
        if (movement.len2() > 1.0f) movement.nor();

        //Acceleration
        if (movement.len2() > 0.1f) {
            if (movement.len2() > 1.0f) movement.nor();
            float speed = Constants.PLAYER_SPEED;
            float max = speed - velocity.len();
            if (max > 0.0f) {
                float accel = Constants.PLAYER_ACCEL * deltaTime * speed;
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
            float control = Math.max(currentSpeed, Constants.PLAYER_TRACTION);
            float drop = control * deltaTime;
            float newSpeed = currentSpeed - drop;
            if (newSpeed < 0.0f) newSpeed = 0.0f;
            newSpeed /= currentSpeed;
            velocity.scl(newSpeed);
        }
    }

    public void update() {
        // Move bullets
        for (Bullet bullet : bullets) {
            bullet.update(Gdx.graphics.getDeltaTime());
        }
        // Check if player is shooting
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();
        if (g.getInputPoller().shoot.isDown) {
            initSprite(Constants.PLAYER_SPRITESHEET_ALT);
            bullets.add(new Bullet(this.getX(), this.getY(), Constants.PLAYER_BULLET));
            for (int i = 0; i < bullets.size(); ++i) {
                if (bullets.get(i).hasExpired()) {
                    bullets.remove(bullets.get(i));
                    continue;
                }
                bullets.get(i).draw(this.batch);
                Gdx.app.log("DEBUG", "Drawing bullet");
            }
        } else {
            initSprite(Constants.PLAYER_SPRITESHEET);
        }
    }

    public boolean hasFired() {
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();
        if (g.getInputPoller().shoot.isDown) {
            hasFired = true;
        }
        return hasFired;
    }

    public float getWidth() {
        TextureRegion[][] temp = TextureRegion.split(playerSheet, playerSheet.getWidth() / COLUMNS, playerSheet.getHeight() / ROWS);
        Sprite s = new Sprite(temp[0][0]);
        return s.getWidth();
    }

    public float getHeight() {
        TextureRegion[][] temp = TextureRegion.split(playerSheet, playerSheet.getWidth() / COLUMNS, playerSheet.getHeight() / ROWS);
        Sprite s = new Sprite(temp[0][0]);
        return s.getHeight();
    }

    public Rectangle getBoundingRectangle() {
        TextureRegion[][] temp = TextureRegion.split(playerSheet, playerSheet.getWidth() / COLUMNS, playerSheet.getHeight() / ROWS);
        Sprite s = new Sprite(temp[0][0]);
        // 14 and 2.5f are used to tune the hitbox
        return new Rectangle(this.getX() + 20, this.getY() + 22, s.getWidth() / 3f, s.getHeight() / 3f);
    }



}
