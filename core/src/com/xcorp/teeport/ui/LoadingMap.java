package com.xcorp.teeport.ui;


import com.xcorp.teeport.GameScreen;
import com.xcorp.teeport.Player;
import com.xcorp.teeport.Teeport;
import com.xcorp.teeport.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingMap implements Screen {
    private SpriteBatch spriteBatch;
    private Texture splsh;
    Teeport game;

    int time, teleports, spawns;

    public LoadingMap(Teeport g) {
        this.game = g;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.9137f, 0.949f, 0.9764f, 1);
        spriteBatch.begin();

        spriteBatch.draw(splsh, 0, 0);
        AssetsScreen.font64.draw(this.spriteBatch, "Time: " + (int) (GameScreen.currentTime - GameScreen.startTime) / 1000 + " s", 50,
                Gdx.graphics.getHeight() - 10);

        AssetsScreen.font64.draw(this.spriteBatch, "Teleportations: " + Player.teleportations, 70, Gdx.graphics.getHeight() - 110);

        AssetsScreen.font64.draw(this.spriteBatch, "Spawned Teleporters: " + Weapon.shots, 90, Gdx.graphics.getHeight() - 220);


        AssetsScreen.font32.setColor(Color.ORANGE);
        AssetsScreen.font32.draw(this.spriteBatch, "Best: " + time + " s", 70,
                Gdx.graphics.getHeight() - 70);

        AssetsScreen.font32.draw(this.spriteBatch, "Best: " + teleports, 90, Gdx.graphics.getHeight() - 170);

        AssetsScreen.font32.draw(this.spriteBatch, "Best: " + spawns, 110, Gdx.graphics.getHeight() - 280);

        spriteBatch.end();

        if (Gdx.input.justTouched()) {
            if (GameScreen.nextMapToLoad == 12) {
                game.setScreen(game.endScreen);
                GameScreen.nextMapToLoad = 1;
                Player.teleportations = 0;
                Weapon.shots = 0;
            } else {
                game.setScreen(game.gameScreen);
                Player.teleportations = 0;
                Weapon.shots = 0;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(AssetsScreen.camera.combined);
        splsh = new Texture(Gdx.files.internal("ui/uiBackground.png"));

        Preferences stats = Gdx.app.getPreferences("stats" + (GameScreen.nextMapToLoad - 1));
        if (!stats.contains("time")) time = 999;
        else time = stats.getInteger("time");
        if (!stats.contains("teleports")) teleports = 999;
        else teleports = stats.getInteger("teleports");
        if (!stats.contains("spawns")) spawns = 999;
        else spawns = stats.getInteger("spawns");

        stats.clear();
        if (time > (int) (GameScreen.currentTime - GameScreen.startTime) / 1000)
            stats.putInteger("time", (int) (GameScreen.currentTime - GameScreen.startTime) / 1000);
        else
            stats.putInteger("time", time);

        if (teleports > Player.teleportations)
            stats.putInteger("teleports", Player.teleportations);
        else
            stats.putInteger("teleports", teleports);


        if (spawns > Weapon.shots)
            stats.putInteger("spawns", Weapon.shots);
        else
            stats.putInteger("spawns", spawns);

        stats.flush();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

}