package com.xcorp.teeport.utils;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.xcorp.teeport.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import com.xcorp.teeport.controllers.Controls;

// TODO: Auto-generated Javadoc

/**
 * The Class Utils.
 */
public class Utils {
    /**
     * Angle between two points.
     *
     * @param p1 the p1
     * @param p2 the p2
     * @return the float
     */
    public static float anglePP(Vector2 p1, Vector2 p2) {
        double y = p2.y - p1.y;
        double x = p2.x - p1.x;
        return (float) (Math.atan2(y, x) * 180 / Math.PI);
    }

    /**
     * Degrees2 pi radians.
     *
     * @param degrees the degrees
     * @return the float
     */
    public static float degrees2PiRadians(float degrees) {
        return Math.round(degrees / 180);
    }

    /**
     * Distance between two points.
     *
     * @param p1 the p1
     * @param p2 the p2
     * @return the float
     */
    public static float distancePP(Vector2 p1, Vector2 p2) {
        return (float) Math.sqrt((double) (p2.x - p1.x) * (p2.x - p1.x)
                + (p2.y - p1.y) * (p2.y - p1.y));
    }

    public static void drawPoint(Vector2 position) {
        //GameScreen.batch.begin();
        Texture xTexture = new Texture(Gdx.files.internal("8px.png"));
        Sprite xSprite = new com.badlogic.gdx.graphics.g2d.Sprite(xTexture);
        xSprite.setPosition(position.x, position.y);
        xSprite.draw(GameScreen.batch);
        //GameScreen.batch.end();
    }

    public static void drawRedPoint(Vector2 position) {
        GameScreen.batch.begin();
        Texture xTexture = new Texture(Gdx.files.internal("8pxr.png"));
        Sprite xSprite = new com.badlogic.gdx.graphics.g2d.Sprite(xTexture);
        xSprite.setPosition(position.x, position.y);
        xSprite.draw(GameScreen.batch);
        GameScreen.batch.end();
    }

    /**
     * Gets the mouse in fixed box2d coordenates.
     *
     * @return the mouse in fixed box2d coordenates
     */
    public static Vector2 getMouseInFixedBox2dCoordenates() {
        Vector2 mouseCoordenates = Utils.getMouseInFixedWorldCoordinates();
        Utils.vectorInBox2dCoordinates(mouseCoordenates);
        return mouseCoordenates;
    }

    /**
     * Gets the mouse in fixed world coordinates.
     *
     * @return the mouse in fixed world coordinates
     */
    public static Vector2 getMouseInFixedWorldCoordinates() {
        if (Controls.joystickAPointer != -1) {
            if (Controls.joystickAPointer == 0) {
            }
        }

        Vector2 mouseCoordenates = new Vector2(Controls.lastMouseX, Controls.lastMouseY);
        return mouseCoordenates;
    }

    public static Vector2 intersectionPoint(float x1, float y1, float x2,
                                            float y2, float x3, float y3, float x4, float y4) {
        float bx = x2 - x1;
        float by = y2 - y1;
        float dx = x4 - x3;
        float dy = y4 - y3;
        float b_dot_d_perp = bx * dy - by * dx;
        if (b_dot_d_perp == 0)
            return null;
        float cx = x3 - x1;
        float cy = y3 - y1;
        float t = (cx * dy - cy * dx) / b_dot_d_perp;
        if (t < 0 || t > 1)
            return null;
        float u = (cx * by - cy * bx) / b_dot_d_perp;
        if (u < 0 || u > 1)
            return null;
        return new Vector2(x1 + t * bx, y1 + t * by);
    }

    public static String objectToString(Object x) {
        return ToStringBuilder.reflectionToString(x,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Pi radians2 degrees.
     *
     * @param piRadians the pi radians
     * @return the float
     */
    public static float piRadians2Degrees(float piRadians) {
        return Math.round(piRadians * 180);
    }

    /**
     * Polar to rectangular coordinates.
     *
     * @param angle the angle
     * @param r     the r
     * @return the vector2
     */
    public static Vector2 polar2Rectangular(float angle, float r) {
        Vector2 vector = new Vector2();
        vector.x = (float) (r * Math.cos(angle * Math.PI / 180));
        vector.y = (float) (r * Math.sin(angle * Math.PI / 180));
        return vector;
    }

    /**
     * Prints the object.
     *
     * @param x the x
     */
    public static void printObject(Object x) {
        Gdx.app.log("X:", ToStringBuilder.reflectionToString(x,
                ToStringStyle.MULTI_LINE_STYLE));
    }

    public static Line rotateLineAroundFirstPoint(Line line, float angle) {
        float distance = Utils.distancePP(line.pointA, line.pointB);
        angle += Utils.anglePP(line.pointA, line.pointB);
        line.pointB = line.pointA.cpy();
        line.pointB.add(Utils.polar2Rectangular(angle, distance));
        return line;
    }

    /**
     * Vector in box2d coordinates.
     *
     * @param vector the vector
     */
    public static void vectorInBox2dCoordinates(Vector2 vector) {
        vector.scl(Settings.WORLD_TO_BOX);
    }

    /**
     * Vector in world coordinates.
     *
     * @param vector the vector
     */
    public static void vectorInWorldCoordinates(Vector2 vector) {
        vector.scl(Settings.BOX_TO_WORLD);
    }
}