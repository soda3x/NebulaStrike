package com.bradandtommy.spaceshooter;

/**
 * Data type to contain a score and relevant extra data
 */
public class Score {

    // Player's name, last level played along with the score
    private String name;
    private long level;
    private long score;

    /**
     * Score's constructor
     * @param name player's name
     * @param level last level
     * @param score last played score
     */
    public Score(String name, long level, long score) {
        this.name = name;
        this.level = level;
        this.score = score;
    }

    /**
     * Name, score and level properties
     */
    public String getName() {
        return this.name;
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

    /**
     * Add level
     * @param value number that need to add to the current level
     */
    public void addLevel(int value) {
        this.level += value;
    }

    /**
     * Add score
     * @param value number that need to add to the current score
     */
    public void addScore(int value) {
        this.score += value;
    }

    /**
     * Check for the high score
     * @param other other previous player's score
     * @return the higher score
     */
    public boolean higherScoreThan(Score other) {
        return this.getScore() > other.getScore();
    }
}
