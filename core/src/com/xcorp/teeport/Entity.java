package com.xcorp.teeport;

import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
    public int ID;
    public int numFootContacts = 0;
    public boolean noclip = false;
    public EntityType entityType = EntityType.ET_NONE;
    public boolean die = false;
    public Body owner = null;
    public Body self = null;
    public boolean inLove = false;

    Portal portal;
    float teleportTime;

    float highSpeed;
    float highSpeedTime;

    //Components
    public Object draw;
    public Object brain;
    public Object physics;
    public Object control;

    //This one is called on the contact listener.
    public Object touch;

    public float width;
    public float height;

    /**
     * @param portal the portal to set
     */
    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    /**
     * @return the owner
     */
    public Body getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Body owner) {
        this.owner = owner;
    }

    /**
     * @return the numFootContacts
     */
    public int getNumFootContacts() {
        return numFootContacts;
    }

    /**
     * @param numFootContacts the numFootContacts to set
     */
    public void setNumFootContacts(int numFootContacts) {
        this.numFootContacts = numFootContacts;
    }

    public void setID(int i) {
        // TODO Auto-generated method stub
        this.ID = i;
    }

    public int getID() {
        // TODO Auto-generated method stub
        return this.ID;
    }

    public void setEntityType(EntityType type) {
        this.entityType = type;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public boolean getDie() {
        return this.die;
    }

    public void setDie(boolean mustDie) {
        this.die = mustDie;
    }
}
