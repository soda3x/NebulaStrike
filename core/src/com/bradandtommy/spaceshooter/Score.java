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

    public long getLevel() {
        return this.level;
    }

    public long getScore() {
        return this.score;
    }
}
