package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represent label
 */
public class Label {

    // The enum for alignment
    public enum Alignment { LEFT, CENTER, RIGHT };

    // Font and text
    private BitmapFont font;
    private String text;

    // Coordinate and size of label
    private float x;
    private float y;
    private float w;
    private float h;

    // Label's text alignment
    private Alignment horizontalAlign;
    private Alignment verticalAlign;

    /**
     *  Font, text, coordinate, size and text alignment properties
     */
    public void setFont(BitmapFont font) {this.font = font;}
    public BitmapFont getFont() {return this.font;}

    public void setText(String text) {this.text = text;}
    public String getText() {return this.text;}

    public void setX(float x) {this.x = x;}
    public float getX() {return this.x;}

    public void setY(float y) {this.y = y;}
    public float getY() {return this.y;}

    public void setW(float w) {this.w = w;}
    public float getW() {return this.w;}

    public void setH(float h) {this.h = h;}
    public float getH() {return this.h;}

    public void setHorizontalAlign(Alignment horizontalAlign) {this.horizontalAlign = horizontalAlign;}
    public Alignment getHorizontalAlign() {return this.horizontalAlign;}

    public void setVerticalAlign(Alignment verticalAlign) {this.verticalAlign = verticalAlign;}
    public Alignment getVerticalAlign() {return this.verticalAlign;}

    /**
     * Label's class constructor
     * @param font text font
     * @param text label's text
     * @param x label's x coordinate
     * @param y label's y coordinate
     * @param w label's width
     * @param h label's height
     * @param horizontalAlign label's text horizontal alignment
     * @param verticalAlign label's text vertical alignment
     */
    public Label(BitmapFont font, String text,
                 float x, float y, float w, float h,
                 Alignment horizontalAlign, Alignment verticalAlign ) {
        if (font != null) {
            this.font = font;
        }
        this.text = text;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.horizontalAlign = horizontalAlign;
        this.verticalAlign = verticalAlign;
    }

    /**
     * Draw the label
     * @param batch sprite batch
     */
    public void draw(SpriteBatch batch) {

        if (batch == null) {
            return;
        }

        // Detect width and height of the text
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(this.font, this.text);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        // Coordinate of the text
        float posX = this.x;
        float posY = this.y;

        // Calculate x of text depend on the expected horizontal alignment
        switch (this.horizontalAlign) {
            case LEFT:
                posX = this.x;
                break;
            case CENTER:
                posX = this.x + (this.w - textWidth) / 2;
                break;
            case RIGHT:
                posX = this.x + this.w - textWidth;
                break;
        }

        // Calculate y of text depend on the expected vertical alignment
        switch (this.verticalAlign) {
            case LEFT:
                posY = this.y;
                break;
            case CENTER:
                posY = this.y + (this.h - textHeight) / 2;
                break;
            case RIGHT:
                posY = this.y + (this.h - textHeight);
                break;
        }

        // Draw text
        if (this.font != null) {
            this.font.draw(batch, this.text, posX, posY);
        }
    }
}
