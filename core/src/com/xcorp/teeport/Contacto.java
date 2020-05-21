package com.xcorp.teeport;


import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.xcorp.teeport.Effects.Debris;


public class Contacto implements ContactListener {

    Entity entityA = null;
    Entity entityB = null;

    @Override
    public void beginContact(Contact contact) {

        entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (entityA == null || entityB == null)
            return;

        Portal.saveHighSpeed(entityA);
        Portal.saveHighSpeed(entityB);

        if (entityA.getEntityType() == EntityType.ET_BOX) {
            ((Box) entityA.touch).touch(entityA, entityB);
        }

        if (entityA.getEntityType() == EntityType.ET_SHIT && entityA.touch != null) {
            ((Debris) entityA.touch).touch(entityA, entityB);
        }

        if (entityB.getEntityType() == EntityType.ET_SHIT && entityB.touch != null) {
            ((Debris) entityB.touch).touch(entityB, entityA);
        }

        if (entityA.getEntityType() == EntityType.ET_PLAYER) {
            ((Player) entityA.touch).touch(entityA, entityB);
        }
        if (entityB.getEntityType() == EntityType.ET_PLAYER) {
            ((Player) entityB.touch).touch(entityB, entityA);
        }

        if (entityA.getEntityType() == EntityType.ET_PLAYER && entityB.getEntityType() == EntityType.ET_PRINCESS)
            ((Princess) entityB.touch).touch(entityA, entityB);

        if (entityB.getEntityType() == EntityType.ET_PLAYER && entityA.getEntityType() == EntityType.ET_PRINCESS)
            ((Princess) entityA.touch).touch(entityB, entityA);

        if (contact.getFixtureA().isSensor()
                && contact.getFixtureB().isSensor())
            return;

        Player.footStartContact(contact);

    }

    @Override
    public void endContact(Contact contact) {

        entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (contact.getFixtureA().isSensor()
                && contact.getFixtureB().isSensor())
            return;
        if (entityA == null || entityB == null)
            return;


        Player.footEndContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // TODO Auto-generated method stub

    }

}