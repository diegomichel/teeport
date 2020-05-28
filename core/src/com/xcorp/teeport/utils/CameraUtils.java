package com.xcorp.teeport.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.xcorp.teeport.TileMap;

public class CameraUtils {
    public static void followBody(OrthographicCamera camera, Body body) {
        if (body == null)
            return;
        // if(Teeport.map == null) return;
        Vector2 player = body.getPosition().cpy();
        float cameraHalfWidth = camera.viewportWidth / 2;
        float cameraHalfHeight = camera.viewportHeight / 2;

        Utils.vectorInWorldCoordinates(player);

        float cameraMaxX = Float.parseFloat(TileMap.map.getProperties().get("width").toString()) * Float.parseFloat(TileMap.map.getProperties().get("tilewidth").toString())
                - cameraHalfWidth;
        float cameraMaxY = Float.parseFloat(TileMap.map.getProperties().get("height").toString()) * Float.parseFloat(TileMap.map.getProperties().get("tileheight").toString())
                - cameraHalfHeight;

        if (!(player.y > camera.position.y - camera.viewportHeight / 2
                && player.y < camera.position.y + camera.viewportHeight / 2)) {
            if (player.y < camera.position.y && camera.position.y > cameraHalfHeight
                    || player.y > camera.position.y
                    && camera.position.y < cameraMaxY) {
                camera.position.y = player.y;
            }
        }

        // Add this if controls get to hard on android
        // if (player.x > (camera.position.x - camera.viewportWidth/2)
        // && player.x < (camera.position.x + camera.viewportWidth/2)) {
        // Utils.printObject(camera.position);
        // Utils.printObject(player);
        //
        // } else {

        if (player.x < camera.position.x && camera.position.x > cameraHalfWidth
                || player.x > camera.position.x
                && camera.position.x < cameraMaxX) {
            camera.position.x = player.x;
            // }
        }

    }
}