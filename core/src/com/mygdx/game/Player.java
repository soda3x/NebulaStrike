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
public class Player {
    /**Sprite is a useful class for storing a texture region, tint, and position, along
     * with scale and rotation */
    Sprite sprite;
    /**The Sprite class doesn't include a collider of its own so you need to keep a
     * separate one on hand. */
    Rectangle collider;
    TextureRegion playerRedShip;
    Sound laserShot;
    Sound punchShot;
    Sound mixedShot;

    /**Velocity is how far the entity will move over one second, this allows us to scale
     * it by deltaTime every frame, giving a nice smooth movement regardless of
     * framerate */
    Vector2 velocity = new Vector2();
    Vector2 movement = new Vector2();
    float maxSpeed = 500.0f;
    float acceleration = 12.0f;
    /**Used by the fancier version of the movement algorithm to determine how easy it is
     * turn and slow down. A lower value gives floatier deceleration and makes it harder
     * to turn while moving at full speed as the amount you turn is determined by how much
     * friction allows for it every frame. */
    float traction = 2500.0f;

    float shootingCooldown = 0f;
    float shootingCooldownSlow = 0.5f;
    float shootingCooldownFast = 0.08f;

    public void init() {
        /* You can easily find the region of a sheet you want by opening up the image in
         * GIMP or some other editing software, and dragging a box around the sprite you
         * want with one of the tools (I use crop) and checking where the bounds of the
         * rectangle are on the tool options */
        playerRedShip = new TextureRegion(GameScreen.global.ships, 450, 210, 72, 68);
        sprite = new Sprite(playerRedShip);
        sprite.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 5);
        collider = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

        laserShot = Gdx.audio.newSound(Gdx.files.internal("Laser_Shoot9b.wav"));
        punchShot = Gdx.audio.newSound(Gdx.files.internal("Punch.wav"));
        mixedShot = Gdx.audio.newSound(Gdx.files.internal("PunchLaser.wav"));
    }

    public void dispose() {
        laserShot.dispose();
        punchShot.dispose();
        mixedShot.dispose();
    }

    /**Naive movement system, move a flat distance every frame. Very jerky and looks
     * a little nasty */
    public void moveBadly(float deltaTime) {
        GameScreen g = GameScreen.global;
        velocity.set(0f, 0f);
        if (g.input.moveLeft.isDown) velocity.x -= 1;
        if (g.input.moveRight.isDown) velocity.x += 1;
        if (g.input.moveDown.isDown) velocity.y -= 1;
        if (g.input.moveUp.isDown) velocity.y += 1;
        /* We don't want the ship to move faster if we're moving diagonally, so we check
         * the length of the velocity vector, normalizing it back if it's greater than
         * one, which will only happen if two directions are pressed */
        if (velocity.len2() > 1.0f) velocity.nor();
        if (g.nebulaStrikeLevel == 0)
            velocity.scl(maxSpeed / 2f);
        else
            velocity.scl(maxSpeed);

        sprite.translate(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    /**A much better movement algorithm, inspired by the one used in the Quake engines.
     * Has a subtle speed-up and slow-down that feels really satisfying to move with */
    public void moveSmoother(float deltaTime) {
        GameScreen g = GameScreen.global;
        /* Instead of applying velocity directly, have it persist between frames so
         * we can have noticable acceleration/deceleration */
        movement.set(0f, 0f);
        if (g.input.moveLeft.isDown) movement.x -= 1;
        if (g.input.moveRight.isDown) movement.x += 1;
        if (g.input.moveDown.isDown) movement.y -= 1;
        if (g.input.moveUp.isDown) movement.y += 1;
        if (movement.len2() > 1.0f) movement.nor();

        //Acceleration
        if (movement.len2() > 0.1f) {
            if (movement.len2() > 1.0f) movement.nor();
            float speed = maxSpeed;
            float max = speed - velocity.len();
            if (max > 0.0f) {
                float accel = acceleration * deltaTime * speed;
                if (accel > max) accel = max;
                velocity.add(movement.scl(accel));
            }
        }

        sprite.translate(velocity.x * deltaTime, velocity.y * deltaTime);

        // Friction
        if (Math.abs(velocity.len2()) < 0.01f) {
            /* Floating-point numbers are very innaccurate and will rarely hit absolute
             * zero, so if we're effectively still, set velocity to zero explicitly to
             * prevent any jittering.*/
            velocity.set(0f, 0f);
        } else {
            float currentSpeed = velocity.len();
            float control = currentSpeed < traction ? traction : currentSpeed;
            float drop = control * deltaTime;
            float newSpeed = currentSpeed - drop;
            if (newSpeed < 0.0f) newSpeed = 0.0f;
            newSpeed /= currentSpeed;
            velocity.scl(newSpeed);
        }
    }

    public void engineTrail() {
        Particles p = GameScreen.global.particles;
        int i = p.spawn(Particles.Type.PLAYER_TRAIL);
        p.x[i] = sprite.getX() + 23 + MathUtils.random(-1, 1);
        p.y[i] = sprite.getY() + 8 + MathUtils.random(-2, 2);
        i = p.spawn(Particles.Type.PLAYER_TRAIL);
        p.x[i] = sprite.getX() + 48 + MathUtils.random(-1, 1);
        p.y[i] = sprite.getY() + 8 + MathUtils.random(-2, 2);
    }

    public void update(float deltaTime) {
        GameScreen g = GameScreen.global;

        //Movement
        if (g.nebulaStrikeLevel >= 1) {
            moveSmoother(deltaTime);
        } else {
            moveBadly(deltaTime);
        }

        //Shooting
        if (shootingCooldown <= 0f && g.input.shoot.isDown) {
            Projectiles p = g.projectiles;
            int i = p.spawn(g.nebulaStrikeLevel >= 3 ? Projectiles.Type.BETTER_BULLET : Projectiles.Type.BORING_BULLET);
            p.x[i] = sprite.getX() + 36;
            p.y[i] = sprite.getY() + 62;
            shootingCooldown = shootingCooldownSlow;

            //Fire More
            if (g.nebulaStrikeLevel >= 4) {
                float cone = 40f;
                int j = p.spawn(Projectiles.Type.BETTER_BULLET);
                p.x[j] = sprite.getX() + 22;
                p.y[j] = sprite.getY() + 47;
                p.vX[j] = -cone; // Make it fire off on an angle
                int k = p.spawn(Projectiles.Type.BETTER_BULLET);
                p.x[k] = sprite.getX() + 48;
                p.y[k] = sprite.getY() + 47;
                p.vX[k] = cone;

                //Fire Faster
                if (g.nebulaStrikeLevel >= 5) {
                    p.vY[i] = 1200;
                    p.vY[j] = 1200;
                    p.vY[k] = 1200;
                    shootingCooldown = shootingCooldownFast;
                }

                //Scatter out
                if (g.nebulaStrikeLevel >= 6) {
                    float scatter = 30f;
                    p.vX[i] += MathUtils.random(-scatter, scatter);
                    p.vX[j] += MathUtils.random(-scatter, scatter);
                    p.vX[k] += MathUtils.random(-scatter, scatter);
                    p.vY[i] += MathUtils.random(-scatter, scatter);
                    p.vY[j] += MathUtils.random(-scatter, scatter);
                    p.vY[k] += MathUtils.random(-scatter, scatter);
                }

                //Muzzle Flashes
                if (g.nebulaStrikeLevel >= 7) {
                    Particles pr = g.particles;
                    int x = pr.spawn(Particles.Type.MUZZLE_FLASH);
                    pr.x[x] = 36;
                    pr.y[x] = 72;
                    x = pr.spawn(Particles.Type.MUZZLE_FLASH);
                    pr.x[x] = 22;
                    pr.y[x] = 57;
                    x = pr.spawn(Particles.Type.MUZZLE_FLASH);
                    pr.x[x] = 48;
                    pr.y[x] = 57;
                }

                if (g.nebulaStrikeLevel == 8) {
                    laserShot.play();
                } else if (g.nebulaStrikeLevel == 9) {
                    punchShot.play();
                } else if (g.nebulaStrikeLevel >= 10) {
                    mixedShot.play();
                }
            }
        } else {
            shootingCooldown -= deltaTime;
        }

        //Engine Trail
        if (g.nebulaStrikeLevel >= 2) engineTrail();

        //Bounds checking to stop player moving off the screen
        if (sprite.getX() < 0) {
            sprite.setX(0);
            velocity.x = 0;
        }
        if (sprite.getY() < 0) {
            sprite.setY(0);
            velocity.y = 0;
        }
        if (sprite.getX() + sprite.getWidth() > Gdx.graphics.getWidth()) {
            sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
            velocity.x = 0;
        }
        if (sprite.getY() + sprite.getHeight() > Gdx.graphics.getHeight()) {
            sprite.setY(Gdx.graphics.getHeight() - sprite.getHeight());
            velocity.y = 0;
        }
        //Update the collider's position to match the sprite as they are otherwise unlinked
        collider.setPosition(sprite.getX(), sprite.getY());
    }

    public void render(SpriteBatch batch) {
        //Drawing sprites is really straight forward
        sprite.draw(batch);
    }
}
