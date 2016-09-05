package com.xcorp.teeport;
/*
 * This class helps SVGParse to load the Map Entities

 */

import com.xcorp.teeport.utils.Utils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MapFigure extends Brain {
    BodyType type;
    String name;
    ShapeForm shapeForm;
    float density;
    float friction = 1.0f;
    float restitution;
    Vector2 pos;
    Vector2 size;
    EntityType entityType;
    Body body;

    Texture texture = null;
    Sprite sprite = null;

    Vector2[] vertices;

    public float width;

    public float height;

    public MapFigure() {
        // texture = new Texture(Gdx.files.internal("box.png"));
    }

    public void convertVerticesBox2d() {
        for (Vector2 polygonVertice : this.vertices) {
            Utils.vectorInBox2dCoordinates(polygonVertice);
        }
    }

    @Override
    public void draw() {
        Entity entity = (Entity) this.body.getUserData();
        if (entity.getEntityType() == EntityType.ET_BOX) {
            Vector2 position = this.body.getPosition().cpy();
            Utils.vectorInWorldCoordinates(position);

            this.sprite = new com.badlogic.gdx.graphics.g2d.Sprite(this.texture);
            this.sprite.setSize(this.width, this.width);
            this.sprite.setPosition(position.x - this.width / 2, position.y
                    - this.width / 2);
            this.sprite.setOrigin(this.width / 2, this.width / 2);
            this.sprite.setRotation(MathUtils.radiansToDegrees
                    * this.body.getAngle());

            this.sprite.draw(GameScreen.batch);
        }
    }

    /**
     * @return the density
     */
    public float getDensity() {
        return this.density;
    }

    /**
     * @return the friction
     */
    public float getFriction() {
        return this.friction;
    }

    /**
     * @return the pos
     */
    public Vector2 getPos() {
        return this.pos;
    }

    /**
     * @return the restitution
     */
    public float getRestitution() {
        return this.restitution;
    }

    /**
     * @return the shapeForm
     */
    public ShapeForm getShapeForm() {
        return this.shapeForm;
    }

    /**
     * @return the size
     */
    public Vector2 getSize() {
        return this.size.cpy();
    }

    /**
     * @return the type
     */
    public BodyType getType() {
        return this.type;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * @param density the density to set
     */
    public void setDensity(float density) {
        this.density = density;
    }

    /**
     * @param friction the friction to set
     */
    public void setFriction(float friction) {
        this.friction = friction;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Vector2 pos) {
        this.pos = pos.cpy(); // Dont just pass as reference or we are gonna
        // have problemoz
    }

    /**
     * @param restitution the restitution to set
     */
    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    /**
     * @param shapeForm the shapeForm to set
     */
    public void setShapeForm(ShapeForm shapeForm) {
        this.shapeForm = shapeForm;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Vector2 size) {
        this.size = size;
    }

    /**
     * @param type the type to set
     */
    public void setType(BodyType type) {
        this.type = type;
    }

    /**
     * @param vertices the vertices to set
     */
    public void setVertices(Vector2[] vertices) {
        this.vertices = vertices;
    }
}