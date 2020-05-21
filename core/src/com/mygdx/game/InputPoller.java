package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;

/**Created by Michael Stopa on 5/4/2016. */
public class InputPoller {

    public class Action {
        public boolean isDown = false;
        public boolean prev = false;
        public int key;
        public Action(int key) {
            this.key = key;
            actions.add(this);
        }
        public void poll() {
            prev = isDown;
            isDown = Gdx.input.isKeyPressed(key);
        }
        public boolean pressed() {
            return isDown && !prev;
        }
    }

    public ArrayList<Action> actions = new ArrayList<Action>();

    public Action moveUp = new Action(Input.Keys.W);
    public Action moveDown = new Action(Input.Keys.S);
    public Action moveLeft = new Action(Input.Keys.A);
    public Action moveRight = new Action(Input.Keys.D);

    public Action shoot = new Action(Input.Keys.SPACE);
    public Action next = new Action(Input.Keys.PERIOD);
    public Action prev = new Action(Input.Keys.COMMA);
    public Action exit = new Action(Input.Keys.ESCAPE);

    public void poll() {
        for (Action a: actions) a.poll();
    }
}
