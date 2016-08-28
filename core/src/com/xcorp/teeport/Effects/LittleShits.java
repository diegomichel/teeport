package com.xcorp.teeport.Effects;


import com.xcorp.teeport.Brain;
import com.xcorp.teeport.Entity;
import com.xcorp.teeport.EntityType;
import com.xcorp.teeport.GameScreen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;

public class LittleShits extends Brain {
    BodyDef bodyDef;
    Body body;
    Entity ent;

    int count = 0;
    int maxCount = 10;
    long time;

    public LittleShits(Vector2 position) {
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;


        bodyDef.position.set(position);
        body = GameScreen.world.createBody(bodyDef);

        // Entity
        Entity entity = new Entity();
        entity.setID(-1);
        entity.setEntityType(EntityType.ET_GHOST);
        entity.brain = this;
        entity.draw = this;
        entity.touch = this;
        entity.self = body;

        this.body.setUserData(entity);

        ent = entity;
    }

    public void think() {
        if (time < TimeUtils.millis()) {
            time = TimeUtils.millis() + 40;
            new Shit(this.body.getPosition());
            count++;
        }
        if (count > maxCount) {
            this.ent.setDie(true);
        }
    }
}