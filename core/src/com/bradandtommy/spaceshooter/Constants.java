package com.bradandtommy.spaceshooter;

public class Constants {
    // The constants for the buttons
    public static final String BUTTON_LONG_UP_TEXTURE_FILENAME = "buttonLong_blue.png";
    public static final String BUTTON_LONG_DOWN_TEXTURE_FILENAME = "buttonLong_beige_pressed.png";
    public static final float BUTTON_WIDTH = 160f;
    public static final float BUTTON_HEIGHT = 80f;
    public static final float BUTTON_SPACING = 10f;

    // The constants for button sounds
    public static final String BUTTON_SND_1 = "sound/button1.mp3";
    public static final String BUTTON_SND_2 = "sound/button2.wav";

    // The constants for music
    public static final String MENU_MUSIC_FILENAME = "music/menu.mp3";
    public static final String SCOREBOARD_MUSIC_FILENAME = "music/scores.mp3";
    public static final String GAMESCREEN_INITIAL_MUSIC = "music/start.mp3";
    public static final String GAMESCREEN_MUSIC_LOOP = "music/level.mp3";
    public static final float MUSIC_VOLUME = 0f;

    // The constants for font
    public static final String FONT_FONT_FILENAME = "good_neighbors.fnt";
    public static final String FONT_IMAGE_FILENAME = "good_neighbors.png";
    public static final String FONT_FONT_FILENAME2 = "gui/default.fnt";
    public static final String FONT_IMAGE_FILENAME2 = "gui/default.png";

    // The constants for player
    public static final String PLAYER_SPRITESHEET = "sprites/Player.png";
    public static final String PLAYER_SPRITESHEET_ALT = "sprites/Player_shooting.png";
    public static final String PLAYER_BULLET = "sprites/Bullet_player.png";
    /*
    public static final float PLAYER_SPEED = 300.0f;
    public static final float PLAYER_ACCEL = 100.0f;
    public static final float PLAYER_TRACTION = 500.0f;
     */
    public static final float PLAYER_SPEED = 120.0f;
    public static final float PLAYER_ACCEL = 40.0f;
    public static final float PLAYER_TRACTION = 200.0f;


    // The constants for enemy
    public static final String ENEMY_SPRITESHEET = "sprites/Enemy.png";
    public static final String ENEMY_SPRITESHEET_ALT = "sprites/Enemy_shooting.png";
    public static final String ENEMY_BULLET = "sprites/Bullet_enemy.png";
    /*
    public static final float ENEMY_SPEED = 100.0f;
    public static final float ENEMY_ACCEL = 50.0f;
    public static final float ENEMY_TRACTION = 500.0f;
     */
    public static final float ENEMY_SPEED = 60.0f;
    public static final float ENEMY_ACCEL = 20.0f;
    public static final float ENEMY_TRACTION = 100.0f;


    // In game hints
    public static final String HINT_1 = "Use W, A, S or D to dash in a direction quickly\r\nPress a combination of these keys for more precise movement\r\nShoot to dismiss this message! (Spacebar)";
}
