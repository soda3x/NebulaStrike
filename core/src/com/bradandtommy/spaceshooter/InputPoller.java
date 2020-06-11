package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;

/**
 * Class for listening to specific keyboard events related to gameplay
 */
public class InputPoller {

    /**
     * Key action
     */
    public class Action {

        //
        public boolean isDown = false;
        public boolean prev = false;
        public int key;

        /**
         *
         * @param key
         */
        public Action(int key) {
            this.key = key;
            actions.add(this);
        }

        /**
         *
         */
        public void poll() {
            prev = isDown;
            isDown = Gdx.input.isKeyPressed(key);
        }

        /**
         * Return whether key pressed or not
         * @return boolean pressed
         */
        public boolean pressed() {
            return isDown && !prev;
        }
    }

    // Collection of actions
    public ArrayList<Action> actions = new ArrayList<Action>();

    // Actions for movement
    public Action moveUp = new Action(Input.Keys.W);
    public Action moveDown = new Action(Input.Keys.S);
    public Action moveLeft = new Action(Input.Keys.A);
    public Action moveRight = new Action(Input.Keys.D);

    // Action for shooting
    public Action shoot = new Action(Input.Keys.SPACE);

    /**
     * Poll for user input
     */
    public void poll() {
        for (Action a: actions) {
            a.poll();
        }
    }
}
