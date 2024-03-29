package com.bradandtommy.spaceshooter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Represent the player
 */
public class Player {

    // Player's texture sheet and its corresponding frames along with its animation
    private SpriteBatch batch;
    private Sprite sprite;
    private Texture playerSheet;
    private Animation playerAnimation;
    private float stateTime;

    // Constant for the player's frame
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final float FRAME_DURATION = 0.033f;

    // Player's size, coordinate, movement and velocity
    private float x, y;
    private Vector2 movement;
    private Vector2 velocity;

    // Flag for has fired
    private boolean hasFired;

    // Flag to determine whether the player is dead or not and set it to false by default
    private boolean dead;

    // Array list for storing the bullets
    public ArrayList<Bullet> bullets;

    // Elapsed time for last call and when the player fires the bullets
    private long timeElapsedSinceLastCalled;
    private final long shootCooldownMillis = 300;

    /**
     * Player's constructor
     */
    public Player() {
        this.dead = false;
        this.batch = new SpriteBatch();
        this.movement = new Vector2();
        this.velocity = new Vector2();
        this.sprite = new Sprite();
        this.dead = false;
        this.initSprite(Constants.PLAYER_SPRITESHEET);
        this.bullets = new ArrayList<Bullet>();
    }

    /**
     * Initialising the player's sprite
     * @param spriteFilePath File path for the player's sprite
     */
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
     * Properties for player's coordinate
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

    /**
     * Getter method for retrieving the flag that determines whether the player is dead or not
     * @return the eplayer is dead flag
     */
    public boolean isDead() { return this.dead; }

    /**
     * Draw the player
     */
    public void draw() {
        batch.begin();
        stateTime += Gdx.graphics.getDeltaTime();
        this.sprite = render(stateTime);
        batch.draw(sprite, this.getX(), this.getY());
        batch.end();
    }

    /**
     * Rendering the player's animation
     * @param stateTime
     * @return player's animation
     */
    public Sprite render(float stateTime) {
        return new Sprite((TextureRegion) playerAnimation.getKeyFrame(stateTime, true));
    }

    /**
     * Player's movement
     * @param deltaTime delta time since the previous rendering time
     */
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

        // Ensure the player is not out of the screen
        float tempX = velocity.x * deltaTime + getX();
        float tempY = velocity.y * deltaTime + getY();

        if (tempX < Gdx.graphics.getWidth() - this.getWidth() && tempX > 0) {
            this.setX(velocity.x * deltaTime + getX());
        }
        if (tempY < Gdx.graphics.getHeight() - this.getHeight() && tempY > 0) {
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
            float control = Math.max(currentSpeed, Constants.PLAYER_TRACTION);
            float drop = control * deltaTime;
            float newSpeed = currentSpeed - drop;
            if (newSpeed < 0.0f) newSpeed = 0.0f;
            newSpeed /= currentSpeed;
            velocity.scl(newSpeed);
        }
    }

    /**
     * Updating the player
     * @param timeElapsedWhenCalled delta time when being called
     */
    public void update(long timeElapsedWhenCalled) {
        // Move bullets
        for (Bullet bullet : bullets) {
            bullet.update(Gdx.graphics.getDeltaTime());
        }
        // Check if player is shooting
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();
        if (g.getInputPoller().shoot.isDown) {
            if (timeElapsedWhenCalled - timeElapsedSinceLastCalled >= shootCooldownMillis) {
                timeElapsedSinceLastCalled = timeElapsedWhenCalled;
                initSprite(Constants.PLAYER_SPRITESHEET_ALT);
                Sound shoot = Gdx.audio.newSound(Gdx.files.internal(Constants.PLAYER_SHOOT_SND));
                shoot.play();
                // ADDED parameter
                bullets.add(new Bullet(Bullet.BulletOwner.PLAYER, this.getX(), this.getY(), Constants.PLAYER_BULLET));
            }
        } else {
            initSprite(Constants.PLAYER_SPRITESHEET);
        }
        // Remove bullets if they go off screen
        for (int i = 0; i < bullets.size(); ++i) {
            if (bullets.get(i).hasExpired()) {
                bullets.remove(bullets.get(i));
                continue;
            }
            // Update bullet movement
            bullets.get(i).draw(this.batch);
        }
    }

    /**
     * Has fired flag for the player
     * @return the fire flag
     */
    public boolean hasFired() {
        GameScreen g = SpaceShooter.getSpaceShooterInstance().getGameScreen();
        if (g.getInputPoller().shoot.isDown) {
            hasFired = true;
        }
        return hasFired;
    }

    /**
     * Player's size property
    */
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

    /**
     * Rectangle boundary for the player
     * @return the player's bound box
     */
    public Rectangle getBoundingRectangle() {
        TextureRegion[][] temp = TextureRegion.split(playerSheet, playerSheet.getWidth() / COLUMNS, playerSheet.getHeight() / ROWS);
        Sprite s = new Sprite(temp[0][0]);
        return new Rectangle(this.getX() + 20, this.getY() + 22, s.getWidth() / 3f, s.getHeight() / 3f);
    }

    /**
     * Get the spawning bullet array
     * @return the array of bullets
     */
    public ArrayList<Bullet> getSpawnedBullets() {
        return bullets;
    }



}
