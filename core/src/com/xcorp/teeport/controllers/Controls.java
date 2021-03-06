package com.xcorp.teeport.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.GameScreen;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.ui.AssetsScreen;


public class Controls implements InputProcessor {
    public static int joystickAPointer = -1;
    public static float lastMouseX = 0;
    public static float lastMouseY = 0;

    public static boolean useJoystickToAim = false;
    public static Vector2 target;

    public Joystick joystickA;
    public Joystick joystickB;

    public Controls() {
        Gdx.input.setCatchBackKey(true);
        joystickA = new Joystick(new Texture(Gdx.files.internal("controls.png")));
        joystickB = new Joystick(new Texture(Gdx.files.internal("controls2.png")));
    }

    public void draw() {
        joystickA.draw();
        joystickB.draw();
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
            GameScreen.game.setScreen(GameScreen.game.mainMenu);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 cursorPosition = new Vector3(screenX, screenY, 0);
        AssetsScreen.camera.unproject(cursorPosition);

        if (!Settings.CONTROLS_VISUAL_CONTROLS) {
            Vector2 playerPosition = GameScreen.player.body.getPosition();
            playerPosition.scl(Settings.BOX_TO_WORLD);

            lastMouseX = cursorPosition.x;
            lastMouseY = cursorPosition.y;

            this.joystickA.cursorPosition.set(cursorPosition.x, cursorPosition.y);
        }

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean dragged = false;
        if (Settings.CONTROLS_VISUAL_CONTROLS) {
            boolean rigthScreenSide = (screenX > Gdx.graphics.getWidth() / 2);
            if (rigthScreenSide) {
                this.joystickA.turnOn(pointer, screenX, screenY);
            } else {
                this.joystickB.turnOn(pointer, screenX, screenY);
            }
        } else {
            this.joystickA.turnOn(pointer, GameScreen.player.body.getPosition().x * Settings.BOX_TO_WORLD, GameScreen.player.body.getPosition().y * Settings.BOX_TO_WORLD);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 cursorPosition = new Vector3(screenX, screenY, 0);
        AssetsScreen.camera.unproject(cursorPosition);

        if (Settings.CONTROLS_VISUAL_CONTROLS) {
            if (pointer == this.joystickA.pointer) {
                if (this.joystickA.touchTime + Settings.CONTROLS_MILLISECONDS_TO_RELEASE < TimeUtils.millis()) {
                    this.joystickA.cursorPosition.set(cursorPosition.x, cursorPosition.y);
                    Controls.target = null;
                }
            }
            if (pointer == this.joystickB.pointer) {
                this.joystickB.cursorPosition.set(cursorPosition.x, cursorPosition.y);
            }
        } else {
            if (this.joystickA.touchTime + Settings.CONTROLS_MILLISECONDS_TO_RELEASE < TimeUtils.millis()) {
                this.joystickA.cursorPosition.set(cursorPosition.x, cursorPosition.y);
                Controls.target = null;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Settings.CONTROLS_VISUAL_CONTROLS) {
            if (pointer == this.joystickA.pointer) {
                if (this.joystickA.touchTime + Settings.CONTROLS_MILLISECONDS_TO_RELEASE > TimeUtils.millis()) {
                    GameScreen.player.weapon.fire();
                } else {
                    Controls.target = GameScreen.player.weapon.aimTarget.cpy();
                }
                this.joystickA.turnOff();

            }
            if (pointer == this.joystickB.pointer) {
                this.joystickB.turnOff();
            }
        } else {
            if (pointer == this.joystickA.pointer) {
                if (this.joystickA.touchTime + Settings.CONTROLS_MILLISECONDS_TO_RELEASE > TimeUtils.millis()) {
                    GameScreen.player.weapon.fire();
                } else {
                    Controls.target = GameScreen.player.weapon.aimTarget.cpy();
                }
                this.joystickA.turnOff();

            }
        }
        return false;
    }
}
