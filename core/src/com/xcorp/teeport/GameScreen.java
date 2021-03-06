package com.xcorp.teeport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.CameraUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    public static World world;
    private static Array<Entity> entities;

    public static SpriteBatch batch;

    private Box2DDebugRenderer debugRenderer;
    static final float WORLD_TO_BOX = 0.01f;
    private static final float BOX_TO_WORLD = 100f;
    public static Player player;
    public static Body map;
    public static Particles effects;
    public static Princess princess;

    private ContactListener contacto = new Contacto();


    static Array<Entity> teleportBuffer;

    static {
        teleportBuffer = new Array<>();
    }

    private TileMap tileMap;

    public static Teeport game;

    public static int nextMapToLoad = Settings.INITIAL_MAP;

    public static long startTime;
    public static long currentTime;

    public GameScreen(Teeport g) {
        GameScreen.game = g;
    }

    private void create(int map) {
        if (map != 11) {
            AssetsScreen.musicManager.play("background1", 0.6f);
        } else {
            AssetsScreen.musicManager.play("background5", 1.0f);
        }


        GameScreen.world = new World(new Vector2(0, -10), true);
        GameScreen.world.setContactListener(this.contacto);

        GameScreen.entities = new Array<>();

        this.debugRenderer = new Box2DDebugRenderer();

        this.tileMap = new TileMap(map);


        GameScreen.effects = new Particles("");

        GameScreen.batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        GameScreen.player.dipose();
        GameScreen.batch.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if (!Player.dead)
            CameraUtils.followBody(AssetsScreen.camera, GameScreen.player.body);

        AssetsScreen.camera.update();
        this.tileMap.render();

        GameScreen.batch.setProjectionMatrix(AssetsScreen.camera.combined);

        GameScreen.batch.begin();
        Array<Body> bodies = new Array<>();
        GameScreen.world.getBodies(bodies);
        for (Body b : bodies) {
            if (b == null) {
                continue;
            }

            Entity e = (Entity) b.getUserData();

            if (e != null) {

                if (e.draw != null) {
                    Brain brain = (Brain) e.draw;
                    brain.draw();
                }

            }
        }

        GameScreen.player.draw();
        GameScreen.princess.draw();
        GameScreen.batch.end();
        if (GameScreen.player.portal[0] != null) {
            GameScreen.player.portal[0].portalThink();
        }
        if (GameScreen.player.portal[1] != null) {
            GameScreen.player.portal[1].portalThink();
        }

        if (Settings.DEBUG_DRAW_BBOX) {
            this.debugRenderer.render(GameScreen.world, AssetsScreen.camera.combined
                    .scale(GameScreen.BOX_TO_WORLD, GameScreen.BOX_TO_WORLD, 1.0f));
        }

        GameScreen.world.step(1 / 60f, 6, 2);

        RemoveDeadBodies(bodies);
    }

    private void RemoveDeadBodies(Array<Body> bodies) {
        Iterator<Body> bi;
        GameScreen.world.getBodies(bodies);
        bi = bodies.iterator();
        while (bi.hasNext()) {
            Body b = bi.next();
            if (b == null) {
                continue;
            }

            Entity e = (Entity) b.getUserData();

            if (e != null) {
                if (e.brain != null) {
                    Brain brain = (Brain) e.brain;
                    brain.think();
                }

                if (e.getDie()) {
                    b.getWorld().destroyBody(b);
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
        GameScreen.startTime = TimeUtils.millis();
        this.create(GameScreen.nextMapToLoad);
    }

    @Override
    public void hide() {
    }
}