package com.xcorp.teeport;


import com.xcorp.teeport.ui.AssetsScreen;

import com.xcorp.teeport.Effects.NoPortal;
import com.xcorp.teeport.Effects.Shit;

import com.xcorp.teeport.utils.Utils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.TimeUtils;

import com.xcorp.teeport.controllers.Controls;

public class Weapon implements RayCastCallback {
    public enum RC_Check {
        RC_MUZZLE, RC_WALL, RC_GHOST
    }

    private long lastShotTime;
    private float fireRate;
    Texture weaponTexture = null;
    Texture portalBluePrint = null;

    Texture portalRedPrint = null;
    Texture trajectoryPoint = null;
    Texture trajectoryPointBlue = null;

    Texture trajectoryPointOrange = null;
    Sprite weaponSprite = null;

    Sprite ghostPortalSprite = null;
    Vector2 weaponOffset = new Vector2(32, 32);
    Player player = null;

    boolean canFire = true;
    boolean rayCastCheckMuzzle = false;
    boolean rayCastGetWall = false;

    public Vector2 aimTarget;
    ;

    static Portal bluePortal = null;
    static Portal orangePortal = null;

    RC_Check rayCastOption;

    Vector2 trEndPos = null;
    Vector2 trNormal = null;

    public static Entity target;
    public static int shots;

    public Weapon(float fireRate, Player player) {
        this.weaponTexture = AssetsScreen.getTexture("gunTexture");

        this.portalRedPrint = AssetsScreen.getTexture("portalRedPrint");
        this.portalBluePrint = AssetsScreen.getTexture("portalBluePrint");
        this.trajectoryPoint = AssetsScreen.getTexture("trajectoryPoint");

        this.trajectoryPointBlue = AssetsScreen.getTexture("trajectoryPointBlue");
        this.trajectoryPointOrange = AssetsScreen.getTexture("trajectoryPointOrange");

        this.fireRate = fireRate;
        this.player = player;
    }

    public void dispose() {
        this.weaponTexture.dispose();
    }

    public void draw(Vector2 playerPosition) {
        this.drawGhostPortal();

        Vector2 playerPos = this.player.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPos);

        this.weaponSprite = new Sprite(
                this.weaponTexture);

        float angle = GameScreen.player.controller.joystickA.getAngle();
        if (Controls.target != null) {
            angle = Utils.anglePP(this.player.body.getPosition(),
                    Controls.target);
        }

        if (angle > 90 && angle <= 180 || angle < -90 && angle >= -180) {
            this.weaponSprite.setScale(1, -1);
        }

        this.weaponSprite.setOrigin(this.weaponOffset.x, this.weaponOffset.y);
        this.weaponSprite.rotate(angle);
        this.weaponSprite.setPosition(playerPos.x - this.weaponOffset.x,
                playerPos.y - this.weaponOffset.y);

        this.weaponSprite.draw(GameScreen.batch);
    }

    private void drawGhostPortal() {
        this.ghostPortalSprite = new Sprite(
                this.portalRedPrint);

        Vector2 p1 = this.player.body.getPosition();
        Vector2 vectorPath;
        Vector2 normal;

        float angle = GameScreen.player.controller.joystickA.getAngle();

        if (Controls.target != null) {
            angle = Utils.anglePP(GameScreen.player.body.getPosition().cpy(),
                    Controls.target);

        }

        float r = 1000;

        vectorPath = Utils.polar2Rectangular(angle, r);

        normal = vectorPath.cpy();
        normal.nor();

        Vector2 muzzle = normal.cpy();
        muzzle.scl(Settings.PORTAL_HEIGHT / 2);
        Utils.vectorInBox2dCoordinates(muzzle);
        muzzle.x += this.player.body.getPosition().x;
        muzzle.y += this.player.body.getPosition().y;

        Vector2 end = normal.cpy();
        end.scl(1000);
        this.findWallToAttachPortal(p1, end);

        if (this.trEndPos == null || this.trNormal == null)
            return;

        Vector2 draw = this.trEndPos.cpy();
        draw.scl(Settings.BOX_TO_WORLD);

        if (Portal.portalFit(this.trEndPos.cpy(), this.trNormal.cpy())) {
            this.ghostPortalSprite = new Sprite(
                    this.portalBluePrint);
        }

        if (this.player.portal[0] != null && this.player.portal[1] != null) {
            if (this.player.portal[0].birthDate > this.player.portal[1].birthDate) {
                if (Portal.hitPortal(this.player.portal[0],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    this.ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            } else {
                if (Portal.hitPortal(this.player.portal[1],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    this.ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            }
        } else {
            if (this.player.portal[0] != null) {

                if (Portal.hitPortal(this.player.portal[0],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    this.ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            }
            if (this.player.portal[1] != null) {
                if (Portal.hitPortal(this.player.portal[1],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    this.ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            }
        }

        Utils.vectorInWorldCoordinates(this.trEndPos);
        this.drawTrajectoryFromPlayerToGhost(this.trEndPos);
        this.trEndPos.y -= this.ghostPortalSprite.getHeight() / 2;
        this.trEndPos.x -= this.ghostPortalSprite.getWidth() / 2;
        this.ghostPortalSprite.setPosition(this.trEndPos.x, this.trEndPos.y);
        // ghostPortalSprite.setSize(ghostPortalSprite.getWidth(),
        // Settings.PORTAL_HEIGHT);
        this.ghostPortalSprite.rotate(this.trNormal.angle());
        this.ghostPortalSprite.draw(GameScreen.batch);

        // this.drawTrajectoryFromPlayerToGhost(trEndPos);

    }

    public void drawTrajectoryFromPlayerToGhost(Vector2 target) {
        int trajectoryPoints = 100;
        Vector2 playerPosition = GameScreen.player.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPosition);

        float distance = Utils.distancePP(playerPosition, target);
        float step = 16;
        float angle = Utils.anglePP(playerPosition, target);

        Vector2 trajectoryPointPosition = new Vector2();
        trajectoryPointPosition = playerPosition.cpy();

        this.trajectoryPoint = this.trajectoryPointOrange;
        if (Portal.isNextPortalBlue) {
            this.trajectoryPoint = this.trajectoryPointBlue;
        }

        for (int i = 0; i < trajectoryPoints; i++) {
            trajectoryPointPosition.add(Utils.polar2Rectangular(angle, step));
            if (Utils.distancePP(playerPosition, trajectoryPointPosition) < +16 + 1) {
                continue;
            }
            GameScreen.batch.draw(this.trajectoryPoint,
                    trajectoryPointPosition.x + 2,
                    trajectoryPointPosition.y + 2, 8, 8);

            if (distance < Utils.distancePP(playerPosition,
                    trajectoryPointPosition) + 16 + 1) {
                break;
            }
        }
    }

    private void findWallToAttachPortal(Vector2 p1, Vector2 muzzle) {
        this.trEndPos = null;
        this.trNormal = null;
        this.rayCastOption = RC_Check.RC_WALL;
        GameScreen.world.rayCast(this, p1, muzzle);
        if (this.trEndPos != null)
            this.aimTarget = this.trEndPos.cpy();
    }

    public void fire() {
        if (TimeUtils.nanoTime() - this.lastShotTime < this.fireRate * 1000000)
            return;
        this.lastShotTime = TimeUtils.nanoTime();
        // reset canFire flag
        this.canFire = true;

        Vector2 p1 = this.player.body.getPosition();
        Vector2 p2 = null;
        Vector2 vectorPath;
        Vector2 normal;

        p2 = Utils.getMouseInFixedBox2dCoordenates();

        float r = Utils.distancePP(p1, p2);
        float angle = Utils.anglePP(p1, p2);

        if (Controls.useJoystickToAim) {
            angle = GameScreen.player.controller.joystickA.getAngle();
        }

        if (Controls.target != null) {
            angle = Utils.anglePP(GameScreen.player.body.getPosition().cpy(),
                    Controls.target);
        }


        r = 1000;

        vectorPath = Utils.polar2Rectangular(angle, r);

        normal = vectorPath.cpy();
        normal.nor();

        Vector2 muzzle = normal.cpy();
        muzzle.scl(64);
        Utils.vectorInBox2dCoordinates(muzzle);
        muzzle.add(this.player.body.getPosition());

        Vector2 end = normal.cpy();
        end.scl(1000);
        this.findWallToAttachPortal(p1, end);

        if (this.trEndPos == null || this.trNormal == null)
            return;

        if (!Portal.portalFit(this.trEndPos.cpy(), this.trNormal.cpy())) {
            new NoPortal(this.trEndPos.cpy());
            return;
        }
        /*
		 * What if there is a portal where i want to put my new portal?
		 */
        if (this.player.portal[0] != null && this.player.portal[1] != null) {
            if (this.player.portal[0].birthDate > this.player.portal[1].birthDate) {
                if (Portal.hitPortal(this.player.portal[0],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    new NoPortal(this.trEndPos.cpy());
                    return;
                }
            } else {
                if (Portal.hitPortal(this.player.portal[1],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    new NoPortal(this.trEndPos.cpy());
                    return;
                }
            }
        } else {
            if (this.player.portal[0] != null) {

                if (Portal.hitPortal(this.player.portal[0],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    new NoPortal(this.trEndPos.cpy());
                    return;
                }
            }
            if (this.player.portal[1] != null) {
                if (Portal.hitPortal(this.player.portal[1],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    new NoPortal(this.trEndPos.cpy());
                    return;
                }
            }
        }

        if (this.player.portal[0] != null && this.player.portal[1] != null) {
            if (this.player.portal[0].birthDate > this.player.portal[1].birthDate) {
                this.player.portal[1].movePortal(this.trEndPos, this.trNormal);
            } else {
                this.player.portal[0].movePortal(this.trEndPos, this.trNormal);
            }
        } else {
            new Portal(this.trEndPos, this.trNormal);
        }

        Weapon.shots += 1;

        return;

    }

    //
    // private boolean canFire(Vector2 p1, Vector2 muzzle) {
    // this.rayCastOption = RC_Check.RC_MUZZLE;
    // Teeport.world.rayCast(this, p1, muzzle);
    // return this.canFire;
    // }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point,
                                  Vector2 normal, float fraction) {
        Entity entity;
        switch (this.rayCastOption) {
            case RC_MUZZLE:
                if (fixture.isSensor())
                    return -1;
                this.canFire = false;
                return fraction;
            case RC_WALL:
                entity = (Entity) fixture.getBody().getUserData();
                if (fixture.isSensor())
                    return -1;
                if (entity == null || entity.entityType != EntityType.ET_WALL
                        && entity.entityType != EntityType.ET_WALL_NOPORTAL
                        && entity.entityType != EntityType.ET_MAP)
                    return -1;
                Weapon.target = entity;
                this.trEndPos = point.cpy();
                this.trNormal = normal.cpy();
                return fraction;
            case RC_GHOST:
                entity = (Entity) fixture.getBody().getUserData();
                if (fixture.isSensor())
                    return -1;
                if (entity == null || entity.entityType != EntityType.ET_WALL
                        && entity.entityType != EntityType.ET_MAP)
                    return -1;
                this.trEndPos = point.cpy();
                this.trNormal = normal.cpy();
                return fraction;
        }

        return -1;
    }
}