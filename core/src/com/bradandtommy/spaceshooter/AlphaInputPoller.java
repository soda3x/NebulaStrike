package com.bradandtommy.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;

/**
 * Class used to listen for alphabetical keyboard input for score entry
 */
public class AlphaInputPoller {

    /**
     * Class to contain keyboard event
     */
    public class Action {

        //
        public boolean isDown = false;
        public boolean prev = false;
        public int key;

        /**
         * Constructor to add action to actions list
         * @param key
         */
        public Action(int key) {
            this.key = key;
            actions.add(this);
        }

        /**
         * Poll for user input
         */
        public void poll() {
            prev = isDown;
            isDown = Gdx.input.isKeyPressed(key);
        }

        /**
         * Check if button pressed or not
         * @return pressed state
         */
        public boolean pressed() {
            return isDown && !prev;
        }
    }

    // init list of keyboard actions
    public ArrayList<AlphaInputPoller.Action> actions = new ArrayList<AlphaInputPoller.Action>();

    // Add alphabet to array
    public AlphaInputPoller.Action a = new AlphaInputPoller.Action(Input.Keys.A);
    public AlphaInputPoller.Action b = new AlphaInputPoller.Action(Input.Keys.B);
    public AlphaInputPoller.Action c = new AlphaInputPoller.Action(Input.Keys.C);
    public AlphaInputPoller.Action d = new AlphaInputPoller.Action(Input.Keys.D);
    public AlphaInputPoller.Action e = new AlphaInputPoller.Action(Input.Keys.E);
    public AlphaInputPoller.Action f = new AlphaInputPoller.Action(Input.Keys.F);
    public AlphaInputPoller.Action g = new AlphaInputPoller.Action(Input.Keys.G);
    public AlphaInputPoller.Action h = new AlphaInputPoller.Action(Input.Keys.H);
    public AlphaInputPoller.Action i = new AlphaInputPoller.Action(Input.Keys.I);
    public AlphaInputPoller.Action j = new AlphaInputPoller.Action(Input.Keys.J);
    public AlphaInputPoller.Action k = new AlphaInputPoller.Action(Input.Keys.K);
    public AlphaInputPoller.Action l = new AlphaInputPoller.Action(Input.Keys.L);
    public AlphaInputPoller.Action m = new AlphaInputPoller.Action(Input.Keys.M);
    public AlphaInputPoller.Action n = new AlphaInputPoller.Action(Input.Keys.N);
    public AlphaInputPoller.Action o = new AlphaInputPoller.Action(Input.Keys.O);
    public AlphaInputPoller.Action p = new AlphaInputPoller.Action(Input.Keys.P);
    public AlphaInputPoller.Action q = new AlphaInputPoller.Action(Input.Keys.Q);
    public AlphaInputPoller.Action r = new AlphaInputPoller.Action(Input.Keys.R);
    public AlphaInputPoller.Action s = new AlphaInputPoller.Action(Input.Keys.S);
    public AlphaInputPoller.Action t = new AlphaInputPoller.Action(Input.Keys.T);
    public AlphaInputPoller.Action u = new AlphaInputPoller.Action(Input.Keys.U);
    public AlphaInputPoller.Action v = new AlphaInputPoller.Action(Input.Keys.V);
    public AlphaInputPoller.Action w = new AlphaInputPoller.Action(Input.Keys.W);
    public AlphaInputPoller.Action x = new AlphaInputPoller.Action(Input.Keys.X);
    public AlphaInputPoller.Action y = new AlphaInputPoller.Action(Input.Keys.Y);
    public AlphaInputPoller.Action z = new AlphaInputPoller.Action(Input.Keys.Z);
    public AlphaInputPoller.Action back = new AlphaInputPoller.Action(Input.Keys.BACKSPACE);
    public AlphaInputPoller.Action confirm = new AlphaInputPoller.Action(Input.Keys.ENTER);

    /**
     * Poll for user input
     */
    public void poll() {
        for (AlphaInputPoller.Action a: actions) {
            a.poll();
        }
    }

    /**
     * Pass in string to replace with based on keyboard input
     * @param field
     */
    public void stringBuffer(String[] field) {
        if (this.a.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "A";
                    break;
                }
            }
        }

        if (this.b.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "B";
                    break;
                }
            }
        }

        if (this.c.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "C";
                    break;
                }
            }
        }

        if (this.d.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "D";
                    break;
                }
            }
        }

        if (this.e.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "E";
                    break;
                }
            }
        }

        if (this.f.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "F";
                    break;
                }
            }
        }

        if (this.g.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "G";
                    break;
                }
            }
        }

        if (this.h.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "H";
                    break;
                }
            }
        }

        if (this.i.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "I";
                    break;
                }
            }
        }

        if (this.j.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "J";
                    break;
                }
            }
        }

        if (this.k.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "K";
                    break;
                }
            }
        }

        if (this.l.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "L";
                    break;
                }
            }
        }

        if (this.m.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "M";
                    break;
                }
            }
        }

        if (this.n.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "N";
                    break;
                }
            }
        }

        if (this.o.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "O";
                    break;
                }
            }
        }

        if (this.p.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "P";
                    break;
                }
            }
        }

        if (this.q.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "Q";
                    break;
                }
            }
        }

        if (this.r.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "R";
                    break;
                }
            }
        }

        if (this.s.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "S";
                    break;
                }
            }
        }

        if (this.t.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "T";
                    break;
                }
            }
        }

        if (this.u.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "U";
                    break;
                }
            }
        }

        if (this.v.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "V";
                    break;
                }
            }
        }

        if (this.w.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "W";
                    break;
                }
            }
        }

        if (this.x.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "X";
                    break;
                }
            }
        }

        if (this.y.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "Y";
                    break;
                }
            }
        }

        if (this.z.pressed()) {
            for (int i = 0; i < field.length; ++i) {
                if (field[i] == null) {
                    field[i] = "Z";
                    break;
                }
            }
        }

        if (this.back.pressed()) {
            for (int i = field.length - 1; i > -1; --i) {
                if (field[i] != null) {
                    field[i] = null;
                    break;
                }
            }
        }
    }
}
