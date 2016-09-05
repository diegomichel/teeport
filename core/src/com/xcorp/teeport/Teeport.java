package com.xcorp.teeport;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

import com.xcorp.teeport.ui.*;

public class Teeport extends Game implements Screen{
	public Splash splashScreen;
	public GameScreen gameScreen;
	public MainMenu	mainMenu;
	public Screen	loadingMap;
	public AssetsScreen assetsScreen;
	public Screen	endScreen;

	@Override
	public void create () {
		assetsScreen = new AssetsScreen(this);
		gameScreen = new GameScreen(this);
		mainMenu = new MainMenu(this);
		loadingMap = new LoadingMap(this);
		endScreen = new EndScreen(this);

		setScreen(assetsScreen);
	}

    @Override
    public void render(float delta) {
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
	public void dispose () {
	}
}
