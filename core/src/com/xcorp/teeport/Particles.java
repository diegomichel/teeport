package com.xcorp.teeport;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.xcorp.teeport.ui.AssetsScreen;

public class Particles {
    private ParticleEffect effect = new ParticleEffect();
    private ParticleEffect muzzleEffect = new ParticleEffect();

    public static ParticleEffect noPortalEffect = new ParticleEffect();
    public static Array<ParticleEmitter> noPortalEmitters;


    private Array<ParticleEmitter> emitters;
    private SpriteBatch spriteBatch;
    private float fpsCounter;
    private int emitterIndex;


    public Particles(String file) {
        this.spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(AssetsScreen.camera.combined);
        this.effect.load(Gdx.files.internal("data/test.p.txt"), Gdx.files.internal("data/"));
        this.effect.setPosition(AssetsScreen.camera.viewportWidth / 2, AssetsScreen.camera.viewportHeight / 2);
        this.emitters = new Array<>(this.effect.getEmitters());
        this.effect.getEmitters().clear();
        this.effect.getEmitters().add(this.emitters.get(1));


        noPortalEffect.load(Gdx.files.internal("data/noportal.p"), Gdx.files.internal("data/"));
        noPortalEffect.setPosition(AssetsScreen.camera.viewportWidth / 2, AssetsScreen.camera.viewportHeight / 2);
        noPortalEmitters = new Array<>(noPortalEffect.getEmitters());
        noPortalEffect.getEmitters().clear();
        //  this.noPortalEffect.getEmitters().add(this.noPortalEmitters.get(0));


        this.muzzleEffect.getEmitters().clear();
        this.muzzleEffect.getEmitters().add(this.emitters.get(0));

    }

    public void render() {
        this.spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float delta = Gdx.graphics.getDeltaTime();
        this.spriteBatch.begin();
        this.effect.draw(this.spriteBatch, delta);
        this.spriteBatch.end();
        this.fpsCounter += delta;
        if (this.fpsCounter > 3) {
            this.fpsCounter = 0;
            int activeCount = this.emitters.get(this.emitterIndex).getActiveCount();
            int particleCount = 10;
            System.out.println(activeCount + "/" + particleCount + " particles, FPS: " + Gdx.graphics.getFramesPerSecond());
        }
    }

    public void muzzleEffectDraw(Vector2 position) {
        this.muzzleEffect.getEmitters().clear();
        if (Portal.isNextPortalBlue) {
            this.muzzleEffect.getEmitters().add(this.emitters.get(0));
        } else {
            this.muzzleEffect.getEmitters().add(this.emitters.get(1));
        }
        this.muzzleEffect.setPosition(position.x, position.y);
        float delta = Gdx.graphics.getDeltaTime();
        this.muzzleEffect.draw(GameScreen.batch, delta);
    }

    public void corazones(Vector2 position) {
        this.muzzleEffect.getEmitters().clear();
        this.muzzleEffect.getEmitters().add(this.emitters.get(2));
        this.muzzleEffect.setPosition(position.x, position.y);
        float delta = Gdx.graphics.getDeltaTime();

        this.muzzleEffect.draw(GameScreen.batch, delta);
    }

    public void corazonesA(Vector2 position) {
        AssetsScreen.batch.begin();
        this.muzzleEffect.getEmitters().clear();

        this.muzzleEffect.getEmitters().add(this.emitters.get(2));

        this.muzzleEffect.setPosition(position.x, position.y);
        float delta = Gdx.graphics.getDeltaTime();

        this.muzzleEffect.draw(AssetsScreen.batch, delta);
        AssetsScreen.batch.end();
    }

    public void noPortal(Vector2 position) {
        noPortalEffect.setPosition(position.x, position.y);
        float delta = Gdx.graphics.getDeltaTime();
        noPortalEffect.draw(GameScreen.batch, delta);
    }

}