package com.xcorp.teeport;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;

public class TileMap {
    private SpriteBatch spriteBatch;

    OrthographicCamera cam;
    Vector3 camDirection = new Vector3(1, 1, 0);
    Vector2 maxCamPosition = new Vector2(0, 0);

    private TiledMapRenderer tileMapRenderer;
    public static TiledMap map;
    AtlasTmxMapLoader atlas;

    long startTime = System.nanoTime();
    Vector3 tmp = new Vector3();

    private Array<MapFigure> figures;

    public TileMap(int mapname) {
        long startTime, endTime;
        BitmapFont font = new BitmapFont();
        this.figures = new Array<>();

        font.setColor(Color.RED);

        this.spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(AssetsScreen.camera.combined);


        final String path = "data/maps/";

        FileHandle mapHandle = Gdx.files.internal(path + "map" + mapname + ".tmx");
        FileHandle baseDir = Gdx.files.internal(path);

        if (!mapHandle.exists()) {
            //Utils.printObject("This is the end of the road bro, load the end Screen for good.");
        }

        startTime = System.currentTimeMillis();
        // TileMap.map = TiledLoader.createMap(mapHandle);
        TmxMapLoader.Parameters par = new TmxMapLoader.Parameters();

        TileMap.map = new TmxMapLoader().load(mapHandle.toString(), par);
        endTime = System.currentTimeMillis();
        System.out.println("Loaded map in " + (endTime - startTime) + "mS");

        // this.atlas = new TileAtlas(TileMap.map, baseDir);

        int blockWidth = 64;
        int blockHeight = 64;

        startTime = System.currentTimeMillis();

        this.tileMapRenderer = new OrthogonalTiledMapRenderer(TileMap.map);
        endTime = System.currentTimeMillis();
        System.out.println("Created cache in " + (endTime - startTime) + "mS");


        for (MapLayer group : TileMap.map.getLayers()) {
            for (MapObject object : group.getObjects()) {
                this.createFigureFromObject(object);

            }
        }
        this.spawnEntities();
    }

    // Well this method looks superficial, maybe i should create the bodies
    // here, instead of the other place.
    private void createFigureFromObject(MapObject object) {
        MapFigure figure = new MapFigure();
        Vector2 objectPosition;

        PolygonShape polygonShape;
        Body body;
        Entity entity = new Entity();
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        if (object.getName() == null) {
            object.setName("ET_WALL");
        }

        switch (object.getName()) {
            case "ET_PRINCESS": {
                float sideSize = Float.parseFloat(object.getProperties().get("width").toString());
                objectPosition = new Vector2(Float.parseFloat(object.getProperties().get("x").toString()), Float.parseFloat(object.getProperties().get("y").toString()) + sideSize);
                objectPosition.add(sideSize / 2, -sideSize / 2);
                GameScreen.princess = new Princess(objectPosition);
                return;
            }
            case "ET_PLAYER": {
                float sideSize = Float.parseFloat(object.getProperties().get("width").toString());
                objectPosition = new Vector2(Float.parseFloat(object.getProperties().get("x").toString()), Float.parseFloat(object.getProperties().get("y").toString()) + sideSize);
                objectPosition.add(sideSize / 2, -sideSize / 2);
                GameScreen.player = new Player(objectPosition);
                return;
            }
            case "ET_BOX": {
                float sideSize = Float.parseFloat(object.getProperties().get("width").toString());
                objectPosition = new Vector2(Float.parseFloat(object.getProperties().get("x").toString()), Float.parseFloat(object.getProperties().get("y").toString()) + sideSize);
                objectPosition.add(sideSize / 2, -sideSize / 2);
                Utils.vectorInBox2dCoordinates(objectPosition);
                new Box(objectPosition);
                return;
            }
            case "ET_WALL":
            case "ET_WALL_NOPORTAL":
                figure.entityType = EntityType.ET_WALL;
                if (object.getName().equals("ET_WALL_NOPORTAL")) {
                    figure.entityType = EntityType.ET_WALL_NOPORTAL;
                }

                figure.setType(BodyType.StaticBody);
                figure.setShapeForm(ShapeForm.BOX);
                figure.setSize(new Vector2(Float.parseFloat(object.getProperties().get("width").toString()), Float.parseFloat(object.getProperties().get("height").toString())));
                objectPosition = new Vector2(Float.parseFloat(object.getProperties().get("x").toString()), Float.parseFloat(object.getProperties().get("y").toString()));
                figure.setPos(objectPosition.cpy());
                break;
            case "ET_SPIKES":
                Vector2 objectSize = new Vector2(Float.parseFloat(object.getProperties().get("width").toString()), Float.parseFloat(object.getProperties().get("height").toString()));
                objectPosition = new Vector2(Float.parseFloat(object.getProperties().get("x").toString()), Float.parseFloat(object.getProperties().get("y").toString()) + Float.parseFloat(object.getProperties().get("height").toString()));
                objectPosition.add(Float.parseFloat(object.getProperties().get("width").toString()) / 2, Float.parseFloat(object.getProperties().get("height").toString()) / 2);
                Utils.vectorInBox2dCoordinates(objectPosition);

                entity.setEntityType(EntityType.ET_SPIKES);

                bodyDef.type = BodyType.StaticBody;
                bodyDef.position.set(objectPosition);

                fixtureDef.density = 1;
                fixtureDef.friction = 1;
                fixtureDef.restitution = 0;
                fixtureDef.isSensor = true;

                polygonShape = new PolygonShape();
                Utils.vectorInBox2dCoordinates(objectSize);
                polygonShape.setAsBox(objectSize.x / 2, objectSize.y / 2);

                fixtureDef.shape = polygonShape;
                body = GameScreen.world.createBody(bodyDef);
                body.createFixture(fixtureDef);
                entity.draw = figure;
                figure.setBody(body);
                body.setUserData(entity);
                if (polygonShape != null) {
                    polygonShape.dispose();
                }
                return;
            default:
                Messages.warning(this, "Object name: " + object.getName() + " unrecognized, check your map objects");
                return;
        }

        this.figures.add(figure);
    }

    public void render() {
        this.tileMapRenderer.setView(AssetsScreen.camera);
        this.tileMapRenderer.render();

        this.spriteBatch.begin();

        if (GameScreen.princess.self.inLove) {
            if (GameScreen.currentTime + Settings.INTERMISSION_TIME > TimeUtils.millis()) {
                AssetsScreen.font64.draw(this.spriteBatch, (int) (GameScreen.currentTime + Settings.INTERMISSION_TIME - TimeUtils.millis()) / 1000 + "", AssetsScreen.camera.viewportWidth
                        - AssetsScreen.camera.viewportWidth / 2, AssetsScreen.camera.viewportHeight - AssetsScreen.camera.viewportHeight / 2);
            } else {
                GameScreen.player.loadNextLevel();
            }
        }

        AssetsScreen.font16.draw(this.spriteBatch, "Time: " + (int) (GameScreen.currentTime - GameScreen.startTime) / 1000 + " s",
                AssetsScreen.camera.viewportWidth - 120, AssetsScreen.camera.viewportHeight - 20);

        AssetsScreen.font16.draw(this.spriteBatch, "Tele: " + Player.teleportations, AssetsScreen.camera.viewportWidth - 120, AssetsScreen.camera.viewportHeight - 40);

        AssetsScreen.font16.draw(this.spriteBatch, "Hits: " + Weapon.shots, AssetsScreen.camera.viewportWidth - 120, AssetsScreen.camera.viewportHeight - 60);

        GameScreen.player.control();

        AssetsScreen.font16.draw(this.spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, -20 + Settings.SCREEN_HEIGHT);

        this.spriteBatch.end();
    }

    private void spawnEntities() {
        PolygonShape polygonShape;
        ChainShape chainShape;
        Body body;

        for (MapFigure figure : this.figures) {
            polygonShape = null;
            chainShape = null;

            Entity entity = new Entity();
            BodyDef bodyDef = new BodyDef();
            FixtureDef fixtureDef = new FixtureDef();

            Vector2 bodyPosition = figure.getPos().cpy();
            bodyPosition.x += figure.getSize().x / 2;
            bodyPosition.y += figure.getSize().y / 2;
            Utils.vectorInBox2dCoordinates(bodyPosition);

            entity.setEntityType(figure.entityType == null ? EntityType.ET_WALL : figure.entityType);

            bodyDef.type = figure.getType();
            bodyDef.position.set(bodyPosition);

            fixtureDef.density = figure.getDensity();
            fixtureDef.friction = figure.getFriction();

            fixtureDef.restitution = figure.getRestitution();

            switch (figure.getShapeForm()) {
                case BOX:
                    polygonShape = new PolygonShape();
                    Vector2 size = figure.getSize();
                    Utils.vectorInBox2dCoordinates(size);
                    polygonShape.setAsBox(size.x / 2, size.y / 2);
                    fixtureDef.shape = polygonShape;
                    break;
                case POLYGON:
                    polygonShape = new PolygonShape();
                    for (Vector2 polygonVertice : figure.vertices) {
                        Utils.vectorInBox2dCoordinates(polygonVertice);
                    }
                    polygonShape.set(figure.vertices);
                    fixtureDef.shape = polygonShape;
                    break;
                case CIRCLE:
                    break;
                case CHAINSHAPE:
                    chainShape = new ChainShape();
                    for (Vector2 polygonVertice : figure.vertices) {
                        Utils.vectorInBox2dCoordinates(polygonVertice);
                    }
                    chainShape.createChain(figure.vertices);
                    fixtureDef.shape = chainShape;
                    break;
                case CHAINSHAPELOOP:
                    chainShape = new ChainShape();
                    for (Vector2 polygonVertice : figure.vertices) {
                        Utils.vectorInBox2dCoordinates(polygonVertice);
                    }
                    chainShape.createLoop(figure.vertices);
                    fixtureDef.shape = chainShape;
                    break;
            }
            body = GameScreen.world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            if (figure.entityType == EntityType.ET_MAP) {
                entity.width = figure.width;
                entity.height = figure.height;
                GameScreen.map = body;
            }

            if (figure.entityType == EntityType.ET_BOX) {
                entity.self = body;
                entity.touch = new Box(bodyPosition);
            }

            entity.draw = figure;
            figure.setBody(body);
            body.setUserData(entity);

            if (polygonShape != null) {
                polygonShape.dispose();
            }
            if (chainShape != null) {
                chainShape.dispose();
            }
        }
    }

    public Vector2 tileToWorld(Vector2 pos) {
        pos.y = -pos.y + Float.parseFloat(TileMap.map.getProperties().get("height").toString()) * Float.parseFloat(TileMap.map.getProperties().get("tileheight").toString());
        return pos;
    }
}