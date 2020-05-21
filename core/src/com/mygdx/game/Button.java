package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represent button
 */
public class Button {
    // Coordinate and size of button
    private float x;
    private float y;
    private float w;
    private float h;

    // Texture for button in up-clicked state and down-clicked state
    private Texture textureUp;
    private Texture textureDown;

    // Button's text label
    private Label textLabel;

    // Sound when the button is clicked
    private Sound sound;

    // Flags for Button click
    public boolean isDown = false;
    public boolean isDownPrev = false;

    /**
     *  Coordinate and size properties
     */
    public void setX(float x) {this.x = x;}
    public float getX() {return this.x;}
    public void setY(float y) {this.y = y;}
    public float getY() {return this.y;}
    public void setW(float w) {this.w = w;}
    public float getW() {return this.w;}
    public void setH(float h) {this.h = h;}
    public float getH() {return this.h;}

    /**
     * Button class's constructor
     * @param x button's x coordinate
     * @param y button's y coordinate
     * @param w button's width
     * @param y button's height
     * @param textureUp Texture for button in up-clicked state
     * @param textureDown Texture for button in down-clicked state
     */
    public Button(float x, float y, float w, float h, Texture textureUp, Texture textureDown) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        isDown = false;
        isDownPrev = false;

        this.textureUp = textureUp;
        this.textureDown = textureDown;

        this.textLabel = null;
        this.sound = null;
    }

    /**
     * Set text to the button's label based on the given parameters
     * @param font font of the text
     * @param text input text
     * @param horizontalAlign align the text horizontally on the button
     * @param verticalAlign align the text vertically on the button
     */
    public void setText(BitmapFont font, String text, Label.Alignment horizontalAlign, Label.Alignment verticalAlign) {
        if (font != null && !text.equals("")) {
            this.textLabel = new Label(font, text, this.x, this.y, this.w, this.h, horizontalAlign, verticalAlign);
        }
    }

    /**
     * Set sound, which is played when the button is clicked, to the given sound
     * @param soundFilePath the the sound file path
     */
    public void setSound(String soundFilePath) {
        if (!soundFilePath.isEmpty()) {
            sound = Gdx.audio.newSound(Gdx.files.internal(soundFilePath));
        }
    }

    /**
     * Update the button
     * @param checkTouch flag for checking touch/mouse click
     * @param touchX the touch/mouse click position's x coordinate
     * @param touchY the touch/mouse click position's y coordinate
     */
    public void update(boolean checkTouch, int touchX, int touchY) {
        isDown = false;

        if (checkTouch) {
            int h2 = Gdx.graphics.getHeight();
            // Touch coordinates have origin in top-left instead of bottom left

            isDownPrev = isDown;
            if (touchX >= x && touchX <= x + w && h2 - touchY >= y && h2 - touchY <= y + h) {
                isDown = true;
            }
        }
    }

    /**
     * Draw the button
     * @param batch sprite batch
     */
    public void draw(SpriteBatch batch) {
        if (batch == null) {
            return;
        }
        // If the button is released
        if (! isDown) {
            batch.draw(textureUp, x, y, w, h);
        } else {    // If the button is pressed/touched down
            batch.draw(textureDown, x, y, w, h);
            if (justPressed() && sound != null) {
                sound.play(1F);
            }
        }
        if (textLabel != null) {
            textLabel.draw(batch);
        }
    }
    /**
     * Check if the button has just been pressed
     * @return true if the button has just been pressed, otherwise, return false
     */
    public boolean justPressed() {
        return isDown && !isDownPrev;
    }

    /**
     * Cleanup
     */
    public void dispose() {
        if (sound != null) {
            sound.dispose();
        }
    }
}
