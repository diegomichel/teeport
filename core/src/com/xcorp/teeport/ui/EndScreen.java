package com.xcorp.teeport.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.xcorp.teeport.Particles;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.Teeport;

public class EndScreen implements Screen {
    private SpriteBatch spriteBatch;
    private Texture splsh;
    private Teeport game;
    private int touchs;
    private static OrthographicCamera camera;
    private Particles particles;

    int time, teleports, spawns;

    public EndScreen(Teeport g) {
        this.game = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Settings.SCREEN_WIDTH,
                Settings.SCREEN_HEIGHT);
        particles = new Particles("");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.9137f, 0.949f, 0.9764f, 1);
        spriteBatch.begin();

        spriteBatch.draw(splsh, 0, 0);

        if (touchs == 0) {
            AssetsScreen.font32.draw(this.spriteBatch, "Creator", 670, 270);
            AssetsScreen.font16.draw(this.spriteBatch, "Diego Michel", 670, 170);
        }
        if (touchs == 1) {
            AssetsScreen.font32.draw(this.spriteBatch, "Music", 670, 270);
            AssetsScreen.font16.draw(this.spriteBatch, "David J. Rivera", 670, 230);
            AssetsScreen.font16.draw(this.spriteBatch, "Axton Crolley", 670, 210);
            AssetsScreen.font16.draw(this.spriteBatch, "Glaneur de sons", 670, 190);
            AssetsScreen.font16.draw(this.spriteBatch, "TheRedShore", 670, 170);
        }
        if (touchs == 2) {
            AssetsScreen.font32.draw(this.spriteBatch, "Sounds", 670, 270);
            AssetsScreen.font16.draw(this.spriteBatch, "Kastenfrosch", 670, 230);
            AssetsScreen.font16.draw(this.spriteBatch, "Fins", 670, 210);
            AssetsScreen.font16.draw(this.spriteBatch, "Blender Foundation", 670, 190);
            AssetsScreen.font16.draw(this.spriteBatch, "Iwan Gabovitch (Qubodup)", 670, 170);
        }

        if (touchs == 3) {
            AssetsScreen.font32.draw(this.spriteBatch, "Graphics", 670, 270);
            AssetsScreen.font16.draw(this.spriteBatch, "Xoff", 670, 230);
            AssetsScreen.font16.draw(this.spriteBatch, "Jon Phillips (Rejon)", 670, 210);
            AssetsScreen.font16.draw(this.spriteBatch, "Nathan Eady", 670, 190);
            AssetsScreen.font16.draw(this.spriteBatch, "Mystica", 670, 170);
            AssetsScreen.font16.draw(this.spriteBatch, "Terra Deimos", 670, 150);
            AssetsScreen.font16.draw(this.spriteBatch, "Gerald_G", 670, 130);
            AssetsScreen.font16.draw(this.spriteBatch, "Matt Rumble", 670, 110);
            AssetsScreen.font16.draw(this.spriteBatch, "Rg1024", 670, 90);
        }

        if (touchs == 4) {
            AssetsScreen.font32.draw(this.spriteBatch, "Testing", 670, 270);
            AssetsScreen.font16.draw(this.spriteBatch, "Ryan James", 670, 230);
            AssetsScreen.font16.draw(this.spriteBatch, "Jesus Rubio Ramirez", 670, 210);
            AssetsScreen.font16.draw(this.spriteBatch, "Darren Kameoka", 670, 190);
        }

        if (touchs == 5) {
            AssetsScreen.font32.draw(this.spriteBatch, "Special Thanks", 670, 270);
            AssetsScreen.font16.draw(this.spriteBatch, "BadLogic Games", 670, 230);
            AssetsScreen.font16.draw(this.spriteBatch, "Flarnie Marchan (Graphics inspiration)", 670, 210);
            AssetsScreen.font16.draw(this.spriteBatch, "Teeworlds(Game inspiration)", 670, 190);
            AssetsScreen.font16.draw(this.spriteBatch, "Portal (Game inspiration)", 670, 170);
            AssetsScreen.font16.draw(this.spriteBatch, "http://opengameart.org", 670, 150);
            AssetsScreen.font16.draw(this.spriteBatch, "http://freesounds.org", 670, 130);
            AssetsScreen.font16.draw(this.spriteBatch, "http://openclipart.org", 670, 110);
            AssetsScreen.font16.draw(this.spriteBatch, "http://reddit.com/r/gamedev", 670, 90);
        }

        Vector3 position = new Vector3();
        position.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(position);
        Vector2 pos = new Vector2(position.x, position.y);


        spriteBatch.end();
        particles.corazonesA(pos);


        if (Gdx.input.justTouched()) {
            touchs++;
            //AssetsScreen.getSound("complete").play(0.3f);

            if (touchs > 6) {
                game.setScreen(game.mainMenu);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        spriteBatch = AssetsScreen.batch;
        splsh = new Texture(Gdx.files.internal("ui/endBackground.png"));
        touchs = 0;
        AssetsScreen.musicManager.play("background4");
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