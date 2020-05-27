package com.xcorp.teeport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;

public class Princess extends Brain {
    private Texture texture;
    private Texture eyesTexture;
    private BodyDef bodyDef = new BodyDef();
    private FixtureDef fixtureDef;
    private CircleShape circleShape;
    private Body body;
    private float lastTouch;
    Entity self;

    public Princess(Vector2 position) {
        texture = new Texture(Gdx.files.internal("characterFemale.png"));
        eyesTexture = new Texture(Gdx.files.internal("eyesFemale.png"));

        bodyDef.type = BodyType.DynamicBody;
        fixtureDef = new FixtureDef();

        circleShape = new CircleShape();
        circleShape.setRadius(Settings.PLAYER_RADIUS * Settings.WORLD_TO_BOX);

        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.5f;

        Utils.vectorInBox2dCoordinates(position);
        bodyDef.position.set(position);
        body = GameScreen.world.createBody(bodyDef);
        this.body.createFixture(fixtureDef);

        circleShape.setRadius((Settings.PLAYER_RADIUS + 5) * Settings.WORLD_TO_BOX);

        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef);

        circleShape.dispose();

        // Entity
        Entity entity = new Entity();
        entity.setID(-1);
        entity.setEntityType(EntityType.ET_PRINCESS);
        entity.brain = this;
        entity.touch = this;
        entity.self = body;
        self = entity;

        this.body.setUserData(entity);
        this.body.setFixedRotation(true);
    }

    public void draw() {

        Vector2 playerPos = this.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPos);

        Sprite playerSprite = new Sprite(texture);
        playerSprite.setPosition(playerPos.x - 32, playerPos.y - 32);
        playerSprite.draw(GameScreen.batch);
        this.drawEyes();
        if (this.lastTouch > (TimeUtils.nanoTime() - 5000000000f) && !this.self.inLove) {
            this.self.inLove = true;
            AssetsScreen.getSound("complete").play(0.4f);
        }
        if (this.self.inLove) {
            GameScreen.effects.corazones(this.body.getPosition().cpy().scl(Settings.BOX_TO_WORLD).add(0, 35));
        } else {
            GameScreen.currentTime = TimeUtils.millis();
        }
    }

    private void drawEyes() {
        Vector2 mouse = GameScreen.player.body.getPosition();
        Vector2 playerPos = this.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPos);
        Utils.vectorInWorldCoordinates(mouse);

        Sprite eyesSprite = new Sprite(eyesTexture);

        float angle = (float) (Math.atan2(mouse.y - playerPos.y, mouse.x - playerPos.x) * 180 / Math.PI);
        Vector2 direction = Utils.polar2Rectangular(angle, 10);

        eyesSprite.setPosition(playerPos.x - 16 + direction.x, playerPos.y - 8 + direction.y);

        eyesSprite.draw(GameScreen.batch);

    }

    public void touch(Entity entityA, Entity entityB) {
        this.lastTouch = TimeUtils.nanoTime();
    }
}