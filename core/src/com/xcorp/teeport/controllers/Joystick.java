package com.xcorp.teeport.controllers;


import com.xcorp.teeport.ui.AssetsScreen;

import com.xcorp.teeport.GameScreen;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class Joystick {
    public Vector2 center;
    public Vector2 cursorPosition;

    Texture backgroundTexture;
    Texture cursorTexture;

    Sprite backgroundSprite;
    Sprite cursorSprite;

    public int pointer;
    public boolean active;

    public long touchTime;

    public Joystick(Texture background) {
        this.backgroundTexture = background;
        this.cursorTexture = new Texture(Gdx.files.internal("joystickCursor.png"));

        center = new Vector2();
        cursorPosition = new Vector2();
        active = false;
        pointer = -1;
    }

    public void draw() {
        if (!Settings.CONTROLS_VISUAL_CONTROLS) return;
        if (!this.active) return;

        Vector2 center = getProjectedCenter();

        backgroundSprite = new com.badlogic.gdx.graphics.g2d.Sprite(this.backgroundTexture);
        backgroundSprite.setPosition(center.x - backgroundSprite.getWidth() / 2, center.y - backgroundSprite.getHeight() / 2);
        backgroundSprite.draw(GameScreen.batch);

        float angle = this.getAngle();
        float distance = this.getDistance();
        if (distance > backgroundSprite.getWidth() / 2)
            distance = backgroundSprite.getWidth() / 2;

        Vector2 limitedCursorPosition = Utils.polar2Rectangular(angle, distance);
        limitedCursorPosition.add(center.x, center.y);

        cursorSprite = new com.badlogic.gdx.graphics.g2d.Sprite(this.cursorTexture);
        cursorSprite.setPosition(limitedCursorPosition.x - cursorSprite.getWidth() / 2, limitedCursorPosition.y - cursorSprite.getHeight() / 2);
        cursorSprite.draw(GameScreen.batch);
    }

    public float getAngle() {
        return Utils.anglePP(getProjectedCenter(), this.cursorPosition);
    }

    public float getDistance() {
        return Utils.distancePP(getProjectedCenter(), this.cursorPosition);
    }

    public Vector2 getDirectionVector() {
        Vector2 direction = this.cursorPosition.cpy();
        direction.add(-getProjectedCenter().x, -getProjectedCenter().y);
        return direction;

    }

    public void turnOff() {
        this.pointer = -1;
        this.active = false;
    }

    public void turnOn(int pointer, float f, float g) {
        this.pointer = pointer;
        this.active = true;
        this.touchTime = TimeUtils.millis();
        this.center.set(f, g);
    }

    public Vector2 getProjectedCenter() {
        Vector3 centerPosition = new Vector3(this.center.x, this.center.y, 0);
        AssetsScreen.camera.unproject(centerPosition);

        Vector2 projectedCenter = new Vector2(centerPosition.x, centerPosition.y);

        return projectedCenter;
    }
}