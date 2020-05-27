package com.xcorp.teeport.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.Teeport;

public class MainMenu implements Screen {
    private SpriteBatch spriteBatch;
    private Texture background;
    private Teeport game;
    private Skin skin;
    private TextureAtlas atlas;
    private Stage stage;

    public MainMenu(Teeport g) {
        this.atlas = new TextureAtlas(Gdx.files.internal("skins/pack.atlas"));
        this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"), atlas);
        this.stage = new Stage(new ScreenViewport());
        this.stage.getViewport().update(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, false);

        TextButton playButton = new TextButton("Play", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton creditsButton = new TextButton("Credits", skin);


        Table table = new Table(this.skin);
        Table subTable = new Table(this.skin);
        stage.addActor(table);
        stage.addActor(subTable);
        table.setFillParent(true);

        table.setSize(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT);
        table.padRight(10).padTop(10);
        table.row();

        table.add(playButton).width(400).pad(10f);
        table.row();
        //table.add(optionsButton).width(400).pad(10f);
        //table.row();
        table.add(creditsButton).width(400).pad(10f);


        table.right();


        playButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.gameScreen);
            }
        });


        creditsButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.endScreen);
            }
        });

        this.game = g;
        table.debug();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.9137f, 0.949f, 0.9764f, 1);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0);
        spriteBatch.end();


        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        //Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(AssetsScreen.camera.combined);
        background = new Texture(Gdx.files.internal("ui/uiBackground.png"));
        AssetsScreen.musicManager.play("background3");

        Gdx.input.setCatchBackKey(false);
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        Gdx.input.setInputProcessor(null);
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