package com.benberi.cadesim.game.scene.impl.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.impl.battle.GameInformation;

public class ControlAreaScene implements GameScene {

    private GameContext context;

    private BattleControlComponent control;
    private ShapeRenderer shapeRenderer;

    public ControlAreaScene(GameContext context) {
        this.context = context;
    }

    @Override
    public void create()
    {
        shapeRenderer = new ShapeRenderer();
        this.control = new BattleControlComponent(context, this, true);
        control.create();
    }

    @Override
    public void update() {
        control.update();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderBackground();
        control.render();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean handleDrag(float sx, float sy, float x, float y) {
        return false;
    }

    @Override
    public boolean handleClick(float x, float y) {
        if (control.handleClick(x, y)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleClickRelease(float x, float y) {
        return false;
    }

    private void renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(65 / 255f, 101 / 255f, 139 / 255f, 1));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), 200);

        shapeRenderer.setColor(new Color(72 / 255f, 72 / 255f, 72 / 255f, 1));
        shapeRenderer.rect(0, 199, Gdx.graphics.getWidth(), 1);

        shapeRenderer.setColor(new Color(135 / 255f, 161 / 255f, 188 / 255f, 1));
        shapeRenderer.rect(0, 198, Gdx.graphics.getWidth(), 1);

        shapeRenderer.setColor(new Color(68 / 255f, 101 / 255f, 136 / 255f, 1));
        shapeRenderer.rect(0, 197, Gdx.graphics.getWidth(), 1);
        shapeRenderer.end();
    }

    public BattleControlComponent getBnavComponent() {
        return control;
    }
}
