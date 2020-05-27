package com.bradandtommy.spaceshooter;

/**
 * Data type to contain a score and relevant extra data
 */
public class Score {
    // MODIFIED BY TOMMY
    private String name;
    private int level;
    private int score;

    public Score() {
        this.name = "";
        this.level = 0;
        this.score = 0;
    }

    public Score(String name, int level, int score) {
        this.name = name;
        this.level = level;
        this.score = score;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return this.score;
    }
    public void setScore(int score) { this.score = score; }

    public void addLevel(int value) {
        this.level += value;
    }
    public void addScore(int value) {
        this.score += value;
    }
}
