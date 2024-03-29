package com.bradandtommy.spaceshooter;

/**
 * Containing all of the constants for the game
 */
public class Constants {

    // The constants for the buttons
    public static final String BUTTON_LONG_UP_TEXTURE_FILENAME = "buttonLong_blue.png";
    public static final String BUTTON_LONG_DOWN_TEXTURE_FILENAME = "buttonLong_blue.png";
    public static final String BUTTON_PAUSE = "pause.png";
    public static final String BUTTON_PAUSE_DOWN = "pause_pressed.png";
    public static final float BUTTON_WIDTH = 160f;
    public static final float BUTTON_HEIGHT = 80f;
    public static final float BUTTON_SPACING = 5f;

    // The constants for scrolling background
    public static final String SCROLLING_BG_IMAGE = "sprites/Background_alt.png";

    // The constants for button sounds
    public static final String BUTTON_SND_1 = "sound/button1.mp3";
    public static final String BUTTON_SND_2 = "sound/button2.wav";
    public static final String PLAYER_SHOOT_SND = "sound/player_shoot.mp3";
    public static final String HIT_SND = "sound/enemy_hit.mp3";
    public static final String TUTORIAL_SPEECH = "sound/tutorial_speech.mp3";

    // The constants for music
    public static final String MENU_MUSIC_FILENAME = "music/menu.mp3";
    public static final String SCOREBOARD_MUSIC_FILENAME = "music/scores.mp3";
    public static final String GAMESCREEN_INITIAL_MUSIC = "music/start.mp3";
    public static final String GAMESCREEN_MUSIC_LOOP = "music/level.mp3";
    public static final String GAMESCREEN_BOSS_LOOP = "music/boss.mp3";
    public static final String GAMEOVER_FAIL_MUSIC = "music/gameover.mp3";
    public static final String GAMEOVER_WIN_MUSIC = "music/gameover_newscore.mp3";
    public static final String CREDITS_MUSIC_LOOP = "music/credits.mp3";
    public static final float MUSIC_VOLUME = 0.75f;

    // The constants for font
    public static final String FONT_FONT_FILENAME = "good_neighbors.fnt";
    public static final String FONT_IMAGE_FILENAME = "good_neighbors.png";
    public static final String FONT_FONT_FILENAME2 = "gui/default.fnt";
    public static final String FONT_IMAGE_FILENAME2 = "gui/default.png";

    // The constants for player
    public static final String PLAYER_SPRITESHEET = "sprites/Player.png";
    public static final String PLAYER_SPRITESHEET_ALT = "sprites/Player_shooting.png";
    public static final String PLAYER_BULLET = "sprites/Bullet_player.png";

    public static final float PLAYER_SPEED = 300.0f;
    public static final float PLAYER_ACCEL = 100.0f;
    public static final float PLAYER_TRACTION = 500.0f;
    public static final int PLAYER_INIT_LIVES = 3;
    public static final int PLAYER_MAX_LIVES = 5;

    // Constant for the enemies including the boss
    public static final String ENEMY_NORMAL_SPRITESHEET = "sprites/Enemy.png";
    public static final String ENEMY_NORMAL_SPRITESHEET_ALT = "sprites/Enemy_shooting.png";
    public static final String ENEMY_BOSS_SPRITESHEET = "sprites/Boss.png";
    public static final String ENEMY_BOSS_SPRITESHEET_ALT = "sprites/Boss_shooting.png";
    public static final String ENEMY_BOUNTY_SPRITESHEET = "sprites/Bounty.png";
    public static final String ENEMY_BOUNTY_SPRITESHEET_ALT = "sprites/Bounty_shooting.png";
    public static final String ENEMY_BULLET = "sprites/Bullet_enemy.png";
    public static final String ENEMY_BULLET_BOUNTY = "sprites/Bullet_enemy_bounty.png";
    public static final String ENEMY_BULLET_BOSS = "sprites/Bullet_boss.png";

    public static final float ENEMY_NUMBER_BASE = 10;

    public static final float ENEMY_SPEED = 60.0f;
    public static final float ENEMY_ACCEL = 20.0f;
    public static final float ENEMY_TRACTION = 100.0f;

    public static final int SPAWN_RATE_BOSS = 15;
    public static final int SPAWN_RATE_BOUNTY = 33;

    // In game hints
    public static final String HINT_1 = "Use W, A, S or D to dash in a direction quickly\r\nPress a combination of these keys for more precise movement\r\nShoot to dismiss this message! (Spacebar)";
}
