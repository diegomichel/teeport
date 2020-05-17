package com.xcorp.teeport.Effects;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
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
import com.xcorp.teeport.Particles;
import com.xcorp.teeport.Settings;
import com.xcorp.teeport.ui.AssetsScreen;


public class NoPortal extends Brain {
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

    public NoPortal(Vector2 position) {
        touchSound = AssetsScreen.getSound("shitSound");
        texture = AssetsScreen.getTexture("shitTexture");

        radiusFactor = (float) Math.random();

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
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
        //entity.touch = this;
        entity.self = body;

        float neg = (Math.random() > 0.5) ? -1 : 1;
        lifeTime = 1000;
        spawnTime = TimeUtils.millis();

        body.setLinearVelocity(new Vector2((float) Math.random() * 5 * neg, (float) Math.random() * 7));
        this.body.setUserData(entity);

        ent = entity;
        Particles.noPortalEffect.getEmitters().add(Particles.noPortalEmitters.get(0));
        AssetsScreen.getSound("error").play();

    }

    public void draw() {
        GameScreen.effects.noPortal(this.body.getPosition().cpy().scl(Settings.BOX_TO_WORLD)); //.mul(Settings.BOX_TO_WORLD));
    }

    public void think() {
        if (this.lifeTime + this.spawnTime < TimeUtils.millis()) {
            Particles.noPortalEffect.getEmitters().clear();
            ent.setDie(true);

        }
    }
}