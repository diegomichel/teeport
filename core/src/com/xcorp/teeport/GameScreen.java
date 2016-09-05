package com.xcorp.teeport;

        import java.util.Iterator;

        import com.xcorp.teeport.ui.AssetsScreen;

        import com.xcorp.teeport.utils.CameraUtils;
        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.Screen;
        import com.badlogic.gdx.audio.Music;
        import com.badlogic.gdx.graphics.GL30;
        import com.badlogic.gdx.graphics.OrthographicCamera;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.math.Vector2;
        import com.badlogic.gdx.physics.box2d.Body;
        import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
        import com.badlogic.gdx.physics.box2d.ContactListener;
        import com.badlogic.gdx.physics.box2d.World;
        import com.badlogic.gdx.utils.Array;
        import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    public static World world;
    static Array<Entity> entities;

    public static SpriteBatch batch;

    Box2DDebugRenderer debugRenderer;
    static final float WORLD_TO_BOX = 0.01f;
    static final float BOX_TO_WORLD = 100f;
    public static Player player;
    public static Body map;
    public static Particles effects;
    public static Princess princess;

    ContactListener contacto = new Contacto();


    static Array<Entity> teleportBuffer = new Array<Entity>();

    Music music;

    TileMap tileMap;

    public static Teeport game;

    public static int nextMapToLoad = 1;

    public static long startTime;
    public static long	currentTime;

    public GameScreen(Teeport g){
        GameScreen.game = g;
    }

    public void create(int map) {
        if(map != 11)
        {
            AssetsScreen.musicManager.play("background1",0.6f);
        }
        else {
            AssetsScreen.musicManager.play("background5",1.0f);
        }


        GameScreen.world = new World(new Vector2(0, -10), true);
        GameScreen.world.setContactListener(this.contacto);

        GameScreen.entities = new Array<Entity>();

        this.debugRenderer = new Box2DDebugRenderer();

        this.tileMap = new TileMap(map);


        GameScreen.effects = new Particles("");

        GameScreen.batch = new SpriteBatch();
        // mapTexture = new Map();
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

        if(!Player.dead)
            CameraUtils.followBody(AssetsScreen.camera, GameScreen.player.body);

        AssetsScreen.camera.update();
        this.tileMap.render();

        GameScreen.batch.setProjectionMatrix(AssetsScreen.camera.combined);

        // Drawing
        GameScreen.batch.begin();

//		AssetsScreen.font.draw(GameScreen.batch,
//				"FPSa: " + Gdx.graphics.getFramesPerSecond(), 20, 20);



        // effects.muzzleEffectDraw(new Vector2(0,0));

        // Drawing bodies
        Array bodies = new Array();
        GameScreen.world.getBodies(bodies);
        Iterator<Body> bi = bodies.iterator();
        while (bi.hasNext()) {
            Body b = bi.next();
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

        // Removing dead bodies
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
        // TODO Auto-generated method stub
    }
}