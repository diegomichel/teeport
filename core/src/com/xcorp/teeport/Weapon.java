package com.xcorp.teeport;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.TimeUtils;
import com.xcorp.teeport.Effects.NoPortal;
import com.xcorp.teeport.controllers.Controls;
import com.xcorp.teeport.ui.AssetsScreen;
import com.xcorp.teeport.utils.Utils;

public class Weapon implements RayCastCallback {
    public static Entity target;
    public static int shots;
    public Vector2 aimTarget;
    private Texture weaponTexture;
    private Texture portalBluePrint;
    private Texture portalRedPrint;
    private Texture trajectoryPoint;
    private Texture trajectoryPointBlue;
    private Texture trajectoryPointOrange;
    private Vector2 weaponOffset = new Vector2(32, 32);
    private Player player;
    private boolean canFire = true;
    private RC_Check rayCastOption;
    private Vector2 trEndPos = null;
    private Vector2 trNormal = null;
    private long lastShotTime;
    private float fireRate;
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

        Sprite weaponSprite = new Sprite(
                this.weaponTexture);

        float angle = GameScreen.player.controller.joystickA.getAngle();
        if (Controls.target != null) {
            angle = Utils.anglePP(this.player.body.getPosition(),
                    Controls.target);
        }

        if (angle > 90 && angle <= 180 || angle < -90 && angle >= -180) {
            weaponSprite.setScale(1, -1);
        }

        weaponSprite.setOrigin(this.weaponOffset.x, this.weaponOffset.y);
        weaponSprite.rotate(angle);
        weaponSprite.setPosition(playerPos.x - this.weaponOffset.x,
                playerPos.y - this.weaponOffset.y);

        weaponSprite.draw(GameScreen.batch);
    }

    private void drawGhostPortal() {
        Sprite ghostPortalSprite = new Sprite(
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
            ghostPortalSprite = new Sprite(
                    this.portalBluePrint);
        }

        if (this.player.portal[0] != null && this.player.portal[1] != null) {
            if (this.player.portal[0].birthDate > this.player.portal[1].birthDate) {
                if (Portal.hitPortal(this.player.portal[0],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            } else {
                if (Portal.hitPortal(this.player.portal[1],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            }
        } else {
            if (this.player.portal[0] != null) {

                if (Portal.hitPortal(this.player.portal[0],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            }
            if (this.player.portal[1] != null) {
                if (Portal.hitPortal(this.player.portal[1],
                        this.trEndPos.cpy(), this.trNormal.cpy())) {
                    ghostPortalSprite = new Sprite(
                            this.portalRedPrint);
                }
            }
        }

        Utils.vectorInWorldCoordinates(this.trEndPos);
        this.drawTrajectoryFromPlayerToGhost(this.trEndPos);
        this.trEndPos.y -= ghostPortalSprite.getHeight() / 2;
        this.trEndPos.x -= ghostPortalSprite.getWidth() / 2;
        ghostPortalSprite.setPosition(this.trEndPos.x, this.trEndPos.y);
        ghostPortalSprite.rotate(this.trNormal.angle());
        ghostPortalSprite.draw(GameScreen.batch);
    }

    private void drawTrajectoryFromPlayerToGhost(Vector2 target) {
        int trajectoryPoints = 100;
        Vector2 playerPosition = GameScreen.player.body.getPosition().cpy();
        Utils.vectorInWorldCoordinates(playerPosition);

        float distance = Utils.distancePP(playerPosition, target);
        float step = 16;
        float angle = Utils.anglePP(playerPosition, target);

        Vector2 trajectoryPointPosition;
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
        this.canFire = true;

        Vector2 p1 = this.player.body.getPosition();
        Vector2 p2;
        Vector2 vectorPath;
        Vector2 normal;

        p2 = Utils.getMouseInFixedBox2dCoordenates();

        float r;
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

    }

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

    public enum RC_Check {
        RC_MUZZLE, RC_WALL, RC_GHOST
    }
}