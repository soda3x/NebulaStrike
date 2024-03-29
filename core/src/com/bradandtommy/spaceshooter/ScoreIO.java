package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This class is used to read / write the local scores file (comma separated values)
 */
class ScoreIO {

    // Score
    private PriorityQueue<Score> scores;

    /**
     * ScoreIO constructor
     */
    ScoreIO() {
        scores = new PriorityQueue<Score>(new Comparator<Score>() {
            /**
             * Comparing score
             * @param s1
             * @param s2
             * @return
             */
            @Override
            public int compare(Score s1, Score s2) {
                return (int) (s2.getScore() - s1.getScore());
            }
        });
        readScoresFile();
    }

    /**
     * Read the score from the file in the local storage
     */
    private void readScoresFile() {
        FileHandle handle = Gdx.files.local("localstorage/scores");
        if (handle.exists()) {
            Gdx.app.log("DEBUG", "Scores file found");
            String readFromFile = handle.readString();
            String[] stringScores;
            if (System.getProperty("os.name").contains("Windows")) {
                stringScores = readFromFile.split("\r\n");
            } else {
                stringScores = readFromFile.split("\n");
            }
            for (String stringScore : stringScores) {
                String[] tokenizedScore = stringScore.split(",");
                Score score = new Score(tokenizedScore[0], Long.parseLong(tokenizedScore[1].trim()), Long.parseLong(tokenizedScore[2].trim()));
                scores.add(score);
            }
        } else {
            Gdx.app.log("DEBUG", "Scores file not found, creating a new one");
            try {
                boolean newFile = handle.file().createNewFile();
                if (!newFile) { Gdx.app.log("ERROR", "Cannot write new scores file"); }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the score to the local storage's file
     * @param scoreToAdd Score that needs to be written to the file
     * @return return true or false to see whether the file has been successfully written
     */
    public boolean writeToScoresFile(Score scoreToAdd) {
        String scoreString = scoreToAdd.getName() + "," + scoreToAdd.getLevel() + "," + scoreToAdd.getScore() + "\r\n";
        FileHandle handle = Gdx.files.local("localstorage/scores");

        if (handle.exists()) {
            Gdx.app.log("DEBUG", "Scores file found");
            handle.writeString(scoreString, true);
            return true;
        } else {
            Gdx.app.log("DEBUG", "Scores file not found, creating a new one");
            try {
                boolean newFile = handle.file().createNewFile();
                if (!newFile) {
                    Gdx.app.log("ERROR", "Cannot write new scores file");
                } else {
                    handle.writeString(scoreString, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Getter method to retrieve the score
     * @return the score
     */
    public PriorityQueue<Score> getScores() {
        return this.scores;
    }
}
