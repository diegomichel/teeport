package com.xcorp.teeport;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;

import java.util.Iterator;

public class Portal extends Brain {
    boolean blue = false;
    boolean orange = false;
    static boolean isNextPortalBlue = true;
    float birthDate = 0;

    Vector2 spritePosition = null;
    Vector2 positionBox2d = null;
    Vector2 normal = null;

    Sprite portalSprite = null;

    Body body = null;
    Fixture bodyFixture = null;
    float angleOffset = 0;

    float halfX;
    float halfY;
    Vector2 center = null;
    float boxAngle;

    static boolean blocked = false;

    Texture texture;
    Portal mate = null;

    Sound teleportSound;
    Sound portalSpawnSound;

    public Portal(Vector2 position, Vector2 normal) {
        this.birthDate = TimeUtils.nanoTime();
        positionBox2d = position.cpy();
        Utils.vectorInWorldCoordinates(position);
        teleportSound = AssetsScreen.getSound("teleportSound");
        portalSpawnSound = AssetsScreen.getSound("portalSpawnSound");

        this.normal = normal;

        if (Portal.isNextPortalBlue) {
            this.texture = new Texture(Gdx.files.internal("bluePortal.png"));
            this.blue = true;
            GameScreen.player.portal[0] = this;
            if (GameScreen.player.portal[1] != null) {
                this.mate = GameScreen.player.portal[1];
                GameScreen.player.portal[1].mate = GameScreen.player.portal[0];
            }
        } else {
            this.texture = new Texture(Gdx.files.internal("orangePortal.png"));
            this.orange = true;
            GameScreen.player.portal[1] = this;
            if (GameScreen.player.portal[0] != null) {
                this.mate = GameScreen.player.portal[0];
                GameScreen.player.portal[0].mate = this;
            }
        }
        portalSprite = new Sprite(
                this.texture);
        position.y -= this.portalSprite.getHeight() / 2;
        position.x -= this.portalSprite.getWidth() / 2;

        this.spritePosition = position;

        Portal.isNextPortalBlue = !Portal.isNextPortalBlue;

        BodyDef bodyDef = new BodyDef();

        bodyDef.fixedRotation = true;
        bodyDef.position.set(positionBox2d);
        bodyDef.type = BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();

        float angle = this.normal.angle();
        this.angleOffset = (float) (Math.PI * angle / 180);

        center = this.normal.cpy();
        Utils.vectorInBox2dCoordinates(center);
        /*
         * Move the center of the portal, aka move the portal towards the normal
         */
        center.x *= Settings.PORTAL_WIDTH / 8 + 5;
        center.y *= Settings.PORTAL_WIDTH / 8 + 5;

        Vector2 half = new Vector2(Settings.PORTAL_WIDTH / 8,
                Settings.PORTAL_HEIGHT / 2);
        Utils.vectorInBox2dCoordinates(half);

        boxAngle = (float) (Math.PI * angle / 180);
        shape.setAsBox(half.x, half.y, center, boxAngle);

        this.body = GameScreen.world.createBody(bodyDef);
        // this.bodyFixture = this.body.createFixture(fixtureDef);

        Entity entity = new Entity();
        entity.setEntityType(EntityType.ET_PORTAL);
        entity.setOwner(this.body);
        entity.setPortal(this);

        this.body.setUserData(entity);
        this.body.setLinearVelocity(new Vector2(0.0f, 0.0f));

        shape.dispose();
        portalSpawnSound.stop();
        portalSpawnSound.play(1.0f);
    }

    public void portalThink() {
        this.draw();
        this.body.setGravityScale(0);


        Vector2 pointA = getPortalRayCastPointA();
        Vector2 pointB = getPortalRayCastPointB();

        Utils.vectorInBox2dCoordinates(pointA);
        Utils.vectorInBox2dCoordinates(pointB);


        if (this.mate == null) return;

        GameScreen.world.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point,
                                          Vector2 normal, float fraction) {
                Entity entity;
                entity = (Entity) fixture.getBody().getUserData();
                if (entity.getEntityType() == EntityType.ET_PORTAL)
                    return -1;
                if (entity.getEntityType() != EntityType.ET_BOX
                        && entity.getEntityType() != EntityType.ET_PLAYER
                        && entity.getEntityType() != EntityType.ET_PRINCESS)
                    return -1;
                if (entity.getEntityType() == EntityType.ET_PRINCESS && !entity.inLove)
                    return -1;
                entity.teleportTime = TimeUtils.nanoTime();
                GameScreen.teleportBuffer.add(entity);
                return 0;
            }
        }, pointA, pointB);

        Iterator<Entity> iterator = GameScreen.teleportBuffer.iterator();

        while (iterator.hasNext()) {
            teleportSound.stop();
            Entity ent = iterator.next();
            Vector2 bodyNextPosition = mate.body.getPosition().cpy();
            Vector2 bodyOffsetFromPortal = mate.normal.cpy();
            bodyOffsetFromPortal.scl(Settings.MAX_OBJECT_SIZE_TO_TELEPORT
                    * Settings.WORLD_TO_BOX);
            bodyNextPosition.add(bodyOffsetFromPortal);
            float speed = Utils.distancePP(new Vector2(0, 0), ent.self
                    .getLinearVelocity().cpy());

            if (ent.highSpeedTime > (TimeUtils.nanoTime() - 100000000)) {
                if (ent.highSpeed > speed) {
                    speed = ent.highSpeed;
                }
            }
            Vector2 bodyVelocity = new Vector2(mate.normal.cpy().x * speed,
                    mate.normal.cpy().y * speed);
            ent.self.setTransform(bodyNextPosition, 0);
            ent.self.setLinearVelocity(bodyVelocity);
            ent.self.setAwake(true);
            ent.highSpeed = 0;
            ent.highSpeedTime = 0;
            teleportSound.play(1 / bodyVelocity.len() * 5);
            Player.teleportations += 1;
        }

        GameScreen.teleportBuffer.clear();
    }

    public static boolean portalFit(Vector2 position, Vector2 normal) {
        position.scl(Settings.BOX_TO_WORLD);

        Portal.blocked = false;

        if (Weapon.target.entityType == EntityType.ET_WALL_NOPORTAL) return Portal.blocked;

        Vector2 center = new Vector2();
        Vector2 pointA = new Vector2();
        Vector2 pointB = new Vector2();
        Vector2 pointC = new Vector2();
        Vector2 pointD = new Vector2();
        Vector2 center2 = new Vector2();

        center = normal.cpy();
        center.scl(3);
        center.add(position);


        center2 = normal.cpy();
        center2.scl(Settings.PORTAL_WIDTH);
        center2.add(position);

        float angleDown = normal.angle();
        angleDown -= 90;
        pointA = Utils.polar2Rectangular(angleDown, Settings.PORTAL_HEIGHT / 2 - 10);
        pointA.add(center2);


        angleDown = normal.angle();
        angleDown += 90;
        pointC = Utils.polar2Rectangular(angleDown, Settings.PORTAL_HEIGHT / 2 - 10);
        pointC.add(center2);


        // We get the angle, we rotate it 90, then we project 64 towards it
        float angleUp = normal.angle();
        angleUp += 90;
        pointB = Utils.polar2Rectangular(angleUp, Settings.PORTAL_HEIGHT / 2 - 10);
        pointB.add(center2);

        angleUp = normal.angle();
        angleUp -= 90;
        pointD = Utils.polar2Rectangular(angleUp, Settings.PORTAL_HEIGHT / 2 - 10);
        pointD.add(center2);

        Utils.vectorInBox2dCoordinates(pointA);
        Utils.vectorInBox2dCoordinates(pointB);

        GameScreen.world.rayCast(new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point,
                                          Vector2 normal, float fraction) {
                Entity entity = (Entity) fixture.getBody().getUserData();
                if (entity.getEntityType() == EntityType.ET_WALL
                        || entity.getEntityType() == EntityType.ET_PORTAL
                        || entity.getEntityType() == EntityType.ET_WALL_NOPORTAL) {
                    blocked = true;
                }
                return -1;
            }
        }, pointA, pointB);

        Utils.vectorInBox2dCoordinates(pointC);
        Utils.vectorInBox2dCoordinates(pointD);
        GameScreen.world.rayCast(new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point,
                                          Vector2 normal, float fraction) {
                Entity entity = (Entity) fixture.getBody().getUserData();
                if (entity.getEntityType() == EntityType.ET_WALL
                        || entity.getEntityType() == EntityType.ET_PORTAL
                        || entity.getEntityType() == EntityType.ET_WALL_NOPORTAL) {
                    blocked = true;
                }
                return -1;
            }
        }, pointC, pointD);

        Utils.vectorInBox2dCoordinates(center);
        Utils.vectorInBox2dCoordinates(center2);
        GameScreen.world.rayCast(new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point,
                                          Vector2 normal, float fraction) {
                Entity entity = (Entity) fixture.getBody().getUserData();
                if (entity.getEntityType() == EntityType.ET_WALL
                        || entity.getEntityType() == EntityType.ET_PORTAL
                        || entity.getEntityType() == EntityType.ET_WALL_NOPORTAL) {
                    blocked = true;
                }
                return -1;
            }
        }, center, center2);


        return !blocked;
    }

    public void movePortal(Vector2 position, Vector2 normal) {
        this.birthDate = TimeUtils.nanoTime();
        positionBox2d = position.cpy();
        Utils.vectorInWorldCoordinates(position);
        position.y -= this.portalSprite.getHeight() / 2;
        position.x -= this.portalSprite.getWidth() / 2;
        this.spritePosition = position;
        this.normal = normal;

        float angle = this.normal.angle();

        this.body.setTransform(positionBox2d, (float) (Math.PI * angle / 180)
                - this.angleOffset);

        Portal.isNextPortalBlue = !Portal.isNextPortalBlue;
        portalSpawnSound.play();
    }

    public Vector2 getPortalRayCastCenter() {
        Vector2 rayCastCenter = this.body.getPosition().cpy();
        Vector2 normal = this.normal.cpy();
        normal.scl(10);

        Utils.vectorInBox2dCoordinates(normal);
        rayCastCenter.add(normal);
        return rayCastCenter;

    }

    public Vector2 getPortalRayCastPointA() {
        return getPortalRayCastPointFromCenter(-90);
    }

    public Vector2 getPortalRayCastPointB() {
        return getPortalRayCastPointFromCenter(90);
    }

    public Vector2 getPortalRayCastPointFromCenter(float degrees) {
        float angleDown = this.normal.angle()
                + degrees;
        Vector2 point = Utils.polar2Rectangular(angleDown,
                Settings.PORTAL_HEIGHT / 2);
        Vector2 rayCastCenter = getPortalRayCastCenter();
        Utils.vectorInWorldCoordinates(rayCastCenter);
        point.add(rayCastCenter);
        return point;
    }

    public void draw() {
        GameScreen.batch.begin();

        if (this.blue) {
            portalSprite = new Sprite(
                    this.texture);
        } else {
            portalSprite = new Sprite(
                    this.texture);
        }

        portalSprite.rotate(this.normal.angle());
        portalSprite.setPosition(this.spritePosition.x, this.spritePosition.y);
        //portalSprite.setSize(portalSprite.getWidth(), Settings.PORTAL_HEIGHT);

        portalSprite.draw(GameScreen.batch);
        GameScreen.batch.end();
    }

    public static void saveHighSpeed(Entity ent) {
        if (ent.getEntityType() != EntityType.ET_BOX
                && ent.getEntityType() != EntityType.ET_PLAYER)
            return;

        if (Utils.distancePP(new Vector2(0, 0), ent.self.getLinearVelocity()) > ent.highSpeed) {
            ent.highSpeed = Utils.distancePP(new Vector2(0, 0),
                    ent.self.getLinearVelocity());
            ent.highSpeedTime = TimeUtils.nanoTime();
        }

    }

    public static boolean hitPortal(Portal portal, Vector2 pos, Vector2 normal) {
        Line line = new Line(portal.getPortalRayCastPointA(), portal.getPortalRayCastPointB());
        line = Utils.rotateLineAroundFirstPoint(line, 10);


        Vector2 rayCastCenter = pos;
        Vector2 normalcpy = normal.cpy();
        normalcpy.scl(10);
        Utils.vectorInBox2dCoordinates(normalcpy);
        rayCastCenter.add(normalcpy);
        Utils.vectorInWorldCoordinates(rayCastCenter);

        float angleDown = normal.angle() + -90;
        Vector2 pointC = Utils.polar2Rectangular(angleDown,
                Settings.PORTAL_HEIGHT / 2);
        pointC.add(rayCastCenter);

        angleDown = normal.angle() + 90;
        Vector2 pointD = Utils.polar2Rectangular(angleDown,
                Settings.PORTAL_HEIGHT / 2);
        pointD.add(rayCastCenter);

        Line line2 = new Line(pointD, pointC);
        line2 = Utils.rotateLineAroundFirstPoint(line2, -10);


        return Utils.intersectionPoint(line.pointA.x, line.pointA.y, line.pointB.x, line.pointB.y, line2.pointA.x, line2.pointA.y, line2.pointB.x, line2.pointB.y) != null;
    }
}