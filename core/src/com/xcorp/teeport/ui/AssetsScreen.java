package com.xcorp.teeport.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.xcorp.teeport.Messages;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.Teeport;
import com.xcorp.teeport.utils.MusicManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AssetsScreen implements Screen, AssetErrorListener {
    public static AssetManager manager;

    public static Map<String, String> sounds = new HashMap<String, String>();
    public static Map<String, String> textures = new HashMap<String, String>();
    public static Map<String, String> music = new HashMap<String, String>();

    static Sound defaultSound;
    static Texture defaultTexture;

    Teeport game;

    public static BitmapFont font16;
    public static BitmapFont font32;
    public static BitmapFont font64;
    public static SpriteBatch batch;

    public static Texture splash;

    public static MusicManager musicManager;
    public static OrthographicCamera camera;

    public AssetsScreen(Teeport g) {
        game = g;


        AssetsScreen.camera = new OrthographicCamera();
        AssetsScreen.camera.setToOrtho(false, Settings.SCREEN_WIDTH,
                Settings.SCREEN_HEIGHT);

        musicManager = new MusicManager();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(AssetsScreen.camera.combined);

        font16 = new BitmapFont(Gdx.files.internal("fonts/font16.fnt"), false);
        font32 = new BitmapFont(Gdx.files.internal("fonts/font32.fnt"), false);
        font64 = new BitmapFont(Gdx.files.internal("fonts/font64.fnt"), false);

        manager = new AssetManager();

        defaultSound = Gdx.audio.newSound(Gdx.files.internal("sound/default.mp3"));
        defaultTexture = new Texture(Gdx.files.internal("textures/default.png"));
        splash = new Texture(Gdx.files.internal("ui/uiBackground.png"));

        sounds.put("jumpSound", "sound/172205__fins__jumping.CC0.mp3");//Credited
        sounds.put("portalSpawnSound", "sound/146725__fins__laser.CC0.mp3"); //Credited
        sounds.put("teleportSound", "sound/life_pickup_B.Y.Blender.ogg"); //Credited
        sounds.put("shitSound", "sound/146717__fins__button.CC0.mp3"); //Credited
        sounds.put("dieSound", "sound/71274__qubodup__dull-decompression-punch-impact-explodes-96khz.mp3");
        sounds.put("complete", "sound/162473__kastenfrosch__successful.mp3"); // Credit
        sounds.put("error", "sound/171497__fins__error.mp3"); // errro


        music.put("background1", "music/background1.ogg");
        music.put("background3", "music/background3.ogg");
        music.put("background4", "music/background4.ogg");
        music.put("background5", "music/background5.ogg");

        textures.put("characterTexture", "character.png");
        textures.put("characterEyesTexture", "eyes.png");
        textures.put("princessTexture", "characterFemale.png");
        textures.put("princessEyesTexture", "eyesFemale.png");

        textures.put("gunTexture", "portalGun.png");
        textures.put("boxTexture", "box.png");

        textures.put("portalRedPrint", "portalRedPrint.png");
        textures.put("portalBluePrint", "portalBluePrint.png");

        textures.put("trajectoryPoint", "trajectoryPoint.png");
        textures.put("trajectoryPointBlue", "trajectoryPointBlue.png");
        textures.put("trajectoryPointOrange", "trajectoryPointOrange.png");

        textures.put("shitTexture", "character.png");
        textures.put("splashTexture", "ui/uiBackground.png");

        load();
    }

    public void load() {
        for (Entry<String, String> e : sounds.entrySet()) {
            manager.load(e.getValue(), Sound.class);
        }

        for (Entry<String, String> e : textures.entrySet()) {
            manager.load(e.getValue(), Texture.class);
        }

        for (Entry<String, String> e : music.entrySet()) {
            manager.load(e.getValue(), Music.class);
        }

    }

    public void unload() {

    }

    public void dispose() {
        manager.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.9137f, 0.949f, 0.9764f, 1);
        if (manager.update()) {
            if (manager.getProgress() == 1) {
                if (Gdx.input.justTouched()) {
                    game.setScreen((Screen) game.mainMenu);
                }
            }
        }

        // display loading information
        batch.begin();
        batch.draw(splash, 0, 0);
        font16.draw(batch, "Loading: " + manager.getProgress() * 100, -230 + Settings.SCREEN_WIDTH / 2, -50 + Settings.SCREEN_HEIGHT / 2);
        batch.end();

    }

    @Override
    public void error(AssetDescriptor descriptor, Throwable t) {
        Gdx.app.error("AssetManagerTest", "couldn't load asset '" + descriptor.fileName + "'", (Exception) t);
    }

    public static Sound getSound(String fileName) {
        if (manager.isLoaded(AssetsScreen.sounds.get(fileName))) {
            return manager.get(AssetsScreen.sounds.get(fileName));
        } else {
            Messages.warning(new Object(), "Sound is not in assets: " + fileName);
            return defaultSound;
        }
    }

    public static Texture getTexture(String fileName) {
        if (manager.isLoaded(AssetsScreen.textures.get(fileName))) {
            return manager.get(AssetsScreen.textures.get(fileName));
        } else {
            Messages.warning(new Object(), "Texture is not in assets: " + fileName);
            return defaultTexture;
        }
    }

    public static Music getMusic(String fileName) {
        if (manager.isLoaded(AssetsScreen.music.get(fileName))) {
            return manager.get(AssetsScreen.music.get(fileName));
        } else {
            Messages.warning(new Object(), "Music is not in assets: " + fileName);
            return null;
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

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
}
