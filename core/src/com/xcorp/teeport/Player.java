package com.xcorp.teeport;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.Effects.DebrisParticles;
import com.xcorp.teeport.controllers.Controls;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;

import java.util.Iterator;

public class Player extends Brain {
    public float health = Settings.PLAYER_HEALTH;
    public float radius = Settings.PLAYER_RADIUS;
    public Body body;
    Texture playerTexture;
    Texture eyesTexture;

    BodyDef bodyDef = new BodyDef();
    FixtureDef fixtureDef;
    PolygonShape polygonShape;
    CircleShape circleShape;
    Fixture footSensorFixture;

    static boolean abortShot = false;

    public Weapon weapon;
    Vector2 spawnPoint;

    Portal[] portal = new Portal[2];

    Controls controller;

    Array<Entity> contacts;

    Sound jumpSound;
    Sound dieSound;
    static boolean dead;
    private long deadTime;
    public static int teleportations;

    public Player(Vector2 spawnPosition) {
        jumpSound = AssetsScreen.getSound("jumpSound");
        dieSound = AssetsScreen.getSound("dieSound");
        this.spawnPoint = spawnPosition.cpy();
        Utils.vectorInBox2dCoordinates(this.spawnPoint);
        bodyDef.type = BodyType.DynamicBody;
        fixtureDef = new FixtureDef();

        playerTexture = AssetsScreen.getTexture("characterTexture");
        eyesTexture = AssetsScreen.getTexture("characterEyesTexture");

        portal[0] = null;
        portal[1] = null;

        controller = new Controls();

        Gdx.input.setInputProcessor(controller);

        circleShape = new CircleShape();
        circleShape.setRadius(radius * Settings.WORLD_TO_BOX);

        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.0f;

        Utils.vectorInBox2dCoordinates(spawnPosition);
        bodyDef.position.set(spawnPosition);
        body = GameScreen.world.createBody(bodyDef);
        this.body.createFixture(fixtureDef);

        // Foot sensor
        polygonShape = new PolygonShape();
        polygonShape.setAsBox(radius / 2 * Settings.WORLD_TO_BOX, 0.05f, new Vector2(0, -radius * Settings.WORLD_TO_BOX), 0f);
        fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = polygonShape;
        footSensorFixture = this.body.createFixture(fixtureDef);
        polygonShape.dispose();

        circleShape.dispose();

        // Entity
        Entity entity = new Entity();
        entity.setID(-1);
        entity.setEntityType(EntityType.ET_PLAYER);
        entity.brain = this;
        entity.touch = this;
        entity.self = body;

        this.body.setUserData(entity);
        this.body.setFixedRotation(true);

        // Weapon
        this.weapon = new Weapon(Settings.PORTAL_GUN_RATE, this);

        contacts = new Array<>();
    }

    public void spawn() {
    }

    public void control() {
        if (dead)
            return;
        Vector2 velocity = this.body.getLinearVelocity();
        Vector2 impulse = new Vector2(0, 0);

        if(Settings.DEVELOPMENT_MODE) {
            if (Gdx.input.isKeyPressed(Keys.J)) {
                new Box(this.body.getPosition());
            }
            if (Gdx.input.isKeyPressed(Keys.P)) {
                this.loadNextLevel();
            }
        }

        if (Gdx.input.isKeyPressed(Keys.A) || (controller.joystickB.active && controller.joystickB.getDirectionVector().x < -10)) {
            if (velocity.x > -1)
                impulse.x = -0.7f;
        }

        if (Gdx.input.isKeyPressed(Keys.D) || (controller.joystickB.active && controller.joystickB.getDirectionVector().x > 10)) {
            if (velocity.x < 1)
                impulse.x = 0.7f;
        }

        if (Gdx.input.isKeyPressed(Keys.W) || (controller.joystickB.active && controller.joystickB.getDirectionVector().y > 15)) {
            if (playerTouchingGround()) {
                jumpSound.stop();
                impulse.y = 0.4f * Settings.PLAYER_RADIUS;
                jumpSound.play();
            }
        }
        this.body.applyForceToCenter(impulse, true);
    }

    public boolean playerTouchingGround() {
        Entity footData = (Entity) footSensorFixture.getBody().getUserData();

        if (footData != null) {
            return footData.getNumFootContacts() >= 1;
        }
        return true;
    }

    @Override
    public void draw() {
        if (dead)
            return;
        weapon.draw(body.getPosition());
        //if(Gdx.app.getType() == Application.ApplicationType.Android)
            controller.draw();

        Vector2 playerPos = GameScreen.player.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPos);

        Sprite playerSprite = new Sprite(playerTexture);
        playerSprite.setPosition(playerPos.x - playerTexture.getWidth() / 2, playerPos.y - playerTexture.getHeight() / 2);
        playerSprite.draw(GameScreen.batch);
        this.drawEyes();
    }

    public void drawEyes() {
        Vector2 playerPos = GameScreen.player.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPos);

        Sprite eyesSprite = new Sprite(eyesTexture);

        float angle = GameScreen.player.controller.joystickA.getAngle();
        if (Controls.target != null)
            angle = Utils.anglePP(body.getPosition(), Controls.target);

        Vector2 direction = Utils.polar2Rectangular(angle, 10);

        eyesSprite.setPosition(playerPos.x - (eyesTexture.getWidth() / 2) + direction.x, playerPos.y - (eyesTexture.getHeight() / 2) + direction.y);

        eyesSprite.draw(GameScreen.batch);

    }

    public void dipose() {
        weapon.dispose();
    }

    @Override
    public void think() {

        if (Player.dead) {
            if (this.deadTime + 3000 < TimeUtils.millis()) {
                this.body.setTransform(this.spawnPoint, 0);
                this.body.setLinearVelocity(0, 0);
                Player.dead = !Player.dead;
            }
            return;
        }

        // Reduces player speed
        if (this.body.getLinearVelocity().y == 0 || playerTouchingGround()) {
            this.body.setAngularVelocity(this.body.getAngularVelocity() * 0.91f);
            this.body.setLinearVelocity(this.body.getLinearVelocity().x * 0.91f, this.body.getLinearVelocity().y * 0.91f);
        }

        // Sets max speed for player without item
        Vector2 playerVelocity = body.getLinearVelocity();
        float speed = playerVelocity.len();
        if (speed > Settings.PLAYER_MAX_SPEED) {
            playerVelocity.nor();
            playerVelocity.scl(Settings.PLAYER_MAX_SPEED); //mul(Settings.PLAYER_MAX_SPEED);
            body.setLinearVelocity(playerVelocity);
        }

        /*
         * Contacts events that happened in the last frame
         */
        Iterator<Entity> ContactIterator = contacts.iterator();
        while (ContactIterator.hasNext()) {
            Entity other = ContactIterator.next();

            if (other.entityType == EntityType.ET_SPIKES) {
                this.die();
            }
            if (other.entityType == EntityType.ET_PRINCESS) {

                //this.loadNextLevel();
            }

            ContactIterator.remove();
        }
    }

    public void loadNextLevel() {
        if (dead)
            return;
        GameScreen.nextMapToLoad += 1;

        GameScreen.game.setScreen(GameScreen.game.loadingMap);
    }

    private void die() {
        Vector2 playerPosition = this.body.getPosition().cpy();
        this.portal[0] = null;
        this.portal[1] = null;
        this.deadTime = TimeUtils.millis();
        dead = true;
        this.body.setLinearVelocity(0, 0);
        this.body.setTransform(this.spawnPoint, 0);
        dieSound.play();
        new DebrisParticles(playerPosition);
    }

    public void touch(Entity self, Entity other) {
        if (dead)
            return;
        contacts.add(other);
    }

    public static void footStartContact(Contact contact) {

        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (entityA == null || entityB == null)
            return;

        if (entityA.getEntityType() == EntityType.ET_PLAYER && contact.getFixtureA().isSensor()) {
            entityA.setNumFootContacts(entityA.getNumFootContacts() + 1);
            contact.getFixtureA().getBody().setUserData(entityA);
        }

        if (entityB.getEntityType() == EntityType.ET_PLAYER && contact.getFixtureB().isSensor()) {
            entityB.setNumFootContacts(entityB.getNumFootContacts() + 1);
            contact.getFixtureB().getBody().setUserData(entityB);
        }
    }

    public static void footEndContact(Contact contact) {

        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (entityA == null || entityB == null)
            return;

        if (contact.getFixtureA().isSensor() && entityA.getEntityType() == EntityType.ET_PLAYER) {
            entityA.setNumFootContacts(entityA.getNumFootContacts() - 1);
            contact.getFixtureA().getBody().setUserData(entityA);
        }

        if (contact.getFixtureB().isSensor() && entityB.getEntityType() == EntityType.ET_PLAYER) {
            entityB.setNumFootContacts(entityB.getNumFootContacts() - 1);
            contact.getFixtureB().getBody().setUserData(entityB);
        }
    }
}