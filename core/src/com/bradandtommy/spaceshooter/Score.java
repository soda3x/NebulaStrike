package com.bradandtommy.spaceshooter;

/**
 * Data type to contain a score and relevant extra data
 */
public class Score {
    private String name;
    private long level;
    private long score;

    public Score(String name, long level, long score) {
        this.name = name;
        this.level = level;
        this.score = score;
    }

    public String getName() {
        return this.name;
    }

    public boolean higherScoreThan(Score other) {
        return this.getScore() > other.getScore();
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public long getScore() {
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
