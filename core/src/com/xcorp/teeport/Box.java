package com.xcorp.teeport;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;


public class Box extends Brain {
    private Texture texture;
    private Body body;
    private float radius = 32;

    public Box(Vector2 position) {
        texture = AssetsScreen.getTexture("boxTexture");


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape polygonShape = new PolygonShape();
        this.radius = MathUtils.random(0.5f, 1f) * this.radius;
        polygonShape.setAsBox(radius * Settings.WORLD_TO_BOX, radius * Settings.WORLD_TO_BOX);

        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.3f;
        fixtureDef.friction = 0.05f;

        bodyDef.position.set(position);
        body = GameScreen.world.createBody(bodyDef);
        this.body.createFixture(fixtureDef);

        polygonShape.dispose();

        // Entity
        Entity entity = new Entity();
        entity.setID(-1);
        entity.setEntityType(EntityType.ET_BOX);
        entity.brain = this;
        entity.draw = this;
        entity.touch = this;
        entity.self = body;

        this.body.setUserData(entity);

    }


    public void draw() {
        Vector2 drawPosition = this.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(drawPosition);

        Sprite shitSprite = new Sprite(texture);
        shitSprite.setSize((radius * 2), (radius * 2));
        shitSprite.setOrigin((radius), (radius));
        shitSprite.setPosition(drawPosition.x - (radius), drawPosition.y - (radius));
        shitSprite.rotate(this.body.getAngle() * MathUtils.radiansToDegrees);
        shitSprite.draw(GameScreen.batch);
    }

    public void think() {
    }
}