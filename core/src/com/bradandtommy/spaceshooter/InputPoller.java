package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;

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

    public void poll() {

        for (Action a: actions) {
            a.poll();
        }
    }
}
