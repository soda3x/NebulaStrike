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

public class Player {
    private SpriteBatch batch;
    private Sprite sprite;
    private Texture playerSheet;
    private Animation playerAnimation;
    private float x, y;
    private boolean dead = false;
    private float stateTime;
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private Vector2 movement;
    private Vector2 velocity;

    public Player() {
        this.batch = new SpriteBatch();
        this.movement = new Vector2();
        this.velocity = new Vector2();
        this.sprite = new Sprite();
        this.dead = false;
        playerSheet = new Texture(Gdx.files.internal("sprites/Player.png"));

        TextureRegion[][] temp = TextureRegion.split(playerSheet, playerSheet.getWidth() / COLUMNS, playerSheet.getHeight() / ROWS);
        TextureRegion[] playerFrames = new TextureRegion[ROWS * COLUMNS];
        int index = 0;
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                playerFrames[index++] = temp[i][j];
            }
        }

        playerAnimation = new Animation<TextureRegion>(0.033f, playerFrames);
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
