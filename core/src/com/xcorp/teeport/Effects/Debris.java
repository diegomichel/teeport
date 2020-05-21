package com.xcorp.teeport.Effects;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.Brain;
import com.xcorp.teeport.Entity;
import com.xcorp.teeport.EntityType;
import com.xcorp.teeport.GameScreen;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;


public class Debris extends Brain {
    Texture texture;
    Sound touchSound;
    BodyDef bodyDef;
    FixtureDef fixtureDef;
    CircleShape circleShape;
    Body body;
    float radius = 16;
    float radiusFactor;
    long lifeTime;
    long spawnTime;
    Entity ent;

    public Debris(Vector2 position) {
        touchSound = AssetsScreen.getSound("shitSound");
        texture = AssetsScreen.getTexture("shitTexture");

        radiusFactor = (float) Math.random();

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        fixtureDef = new FixtureDef();


        circleShape = new CircleShape();
        circleShape.setRadius(radius * radiusFactor * Settings.WORLD_TO_BOX);

        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = (float) Math.random() - 0.2f;
        fixtureDef.friction = (float) Math.random() + 0.1f;

        bodyDef.position.set(position);
        body = GameScreen.world.createBody(bodyDef);
        this.body.createFixture(fixtureDef);

        circleShape.dispose();

        // Entity
        Entity entity = new Entity();
        entity.setID(-1);
        entity.setEntityType(EntityType.ET_SHIT);
        entity.brain = this;
        entity.draw = this;
        entity.touch = this;
        entity.self = body;

        float neg = (Math.random() > 0.5) ? -1 : 1;
        lifeTime = (long) (Math.random() * 10000);
        spawnTime = TimeUtils.millis();

        body.setLinearVelocity(new Vector2((float) Math.random() * 5 * neg, (float) Math.random() * 7));
        this.body.setUserData(entity);

        ent = entity;
    }


    public void draw() {
        Vector2 drawPosition = this.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(drawPosition);

        Sprite shitSprite = new Sprite(texture);
        shitSprite.setSize((radius * 2 * radiusFactor), (radius * 2 * radiusFactor));
        shitSprite.setOrigin((radius * radiusFactor), (radius * radiusFactor));
        shitSprite.setPosition(drawPosition.x - (radius * radiusFactor), drawPosition.y - (radius * radiusFactor));
        shitSprite.rotate(this.body.getAngle() * MathUtils.radiansToDegrees);
        shitSprite.draw(GameScreen.batch);
    }

    public void think() {
        if (this.lifeTime + this.spawnTime < TimeUtils.millis()) {
            ent.setDie(true);
        }
    }

    public void touch(Entity self, Entity other) {
        touchSound.stop();
        touchSound.play((this.body.getLinearVelocity().len()) / 50);
    }
}