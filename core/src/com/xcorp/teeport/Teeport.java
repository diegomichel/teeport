package com.xcorp.teeport;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.ui.EndScreen;
import com.xcorp.teeport.ui.LoadingMap;
import com.xcorp.teeport.ui.MainMenu;

public class Teeport extends Game implements Screen {
    public GameScreen gameScreen;
    public MainMenu mainMenu;
    public Screen loadingMap;
    public AssetsScreen assetsScreen;
    public Screen endScreen;

    @Override
    public void create() {
        assetsScreen = new AssetsScreen(this);
        gameScreen = new GameScreen(this);
        mainMenu = new MainMenu(this);
        loadingMap = new LoadingMap(this);
        endScreen = new EndScreen(this);

        setScreen(assetsScreen);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void hide() {

    }
}
