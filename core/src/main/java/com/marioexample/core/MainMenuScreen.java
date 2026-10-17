package com.marioexample.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Professional Main Menu for John Wick's Adventure.
 */
public class MainMenuScreen implements Screen {

    private final MainGame game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private Texture backgroundTexture;

    private static final float BUTTON_WIDTH = 340f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_SPACING = 66f;

    private final Rectangle[] buttonBounds = new Rectangle[5];

    public MainMenuScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        layout = new GlyphLayout();

        for (int i = 0; i < buttonBounds.length; i++) {
            buttonBounds[i] = new Rectangle();
        }

        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        // 1. Draw Background with dark cinematic tint
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(0.7f, 0.75f, 0.85f, 1f);
        batch.draw(backgroundTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);
        batch.end();

        // 2. Draw Translucent Center Card
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float cardW = 540f;
        float cardH = 590f;
        float cardX = (screenW - cardW) / 2f;
        float cardY = (screenH - cardH) / 2f - 20f;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.06f, 0.07f, 0.10f, 0.85f);
        shapeRenderer.rect(cardX, cardY, cardW, cardH);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.95f, 0.75f, 0.22f, 0.75f));
        shapeRenderer.rect(cardX, cardY, cardW, cardH);
        shapeRenderer.end();

        // 3. Draw Title & Subtitle
        batch.begin();
        font.getData().setScale(2.2f);
        font.setColor(Color.BLACK);
        String title = "JOHN WICK'S ADVENTURE";
        layout.setText(font, title);
        float titleX = (screenW - layout.width) / 2f;
        float titleY = cardY + cardH - 38f;
        font.draw(batch, title, titleX + 2, titleY - 2);
        font.setColor(new Color(1f, 0.85f, 0.35f, 1f));
        font.draw(batch, title, titleX, titleY);

        font.getData().setScale(1.05f);
        font.setColor(new Color(0.85f, 0.9f, 1f, 0.9f));
        String sub = "AN ACTION-PACKED 2D PLATFORMER";
        layout.setText(font, sub);
        font.draw(batch, sub, (screenW - layout.width) / 2f, titleY - 44f);
        font.getData().setScale(1.0f);
        batch.end();

        // 4. Render Menu Buttons
        float centerX = screenW / 2f;
        float startY = titleY - 120f;
        float mx = Gdx.input.getX();
        float my = screenH - Gdx.input.getY();

        String[] buttonLabels = new String[] {
            "START GAME (LEVEL 1)",
            "LEVEL 1: DESERT RUN",
            "LEVEL 2: ICE RUN",
            "LEVEL 3: FINAL BOSS",
            "EXIT GAME"
        };

        Color[] normalColors = new Color[] {
            new Color(0.18f, 0.55f, 0.25f, 0.9f),
            new Color(0.60f, 0.45f, 0.15f, 0.9f),
            new Color(0.18f, 0.42f, 0.65f, 0.9f),
            new Color(0.55f, 0.20f, 0.45f, 0.9f),
            new Color(0.55f, 0.16f, 0.16f, 0.9f)
        };

        Color[] hoverColors = new Color[] {
            new Color(0.24f, 0.75f, 0.35f, 0.98f),
            new Color(0.85f, 0.65f, 0.22f, 0.98f),
            new Color(0.25f, 0.62f, 0.92f, 0.98f),
            new Color(0.78f, 0.28f, 0.65f, 0.98f),
            new Color(0.82f, 0.22f, 0.22f, 0.98f)
        };

        float currentY = startY;
        for (int i = 0; i < buttonLabels.length; i++) {
            float bx = centerX - BUTTON_WIDTH / 2f;
            buttonBounds[i].set(bx, currentY, BUTTON_WIDTH, BUTTON_HEIGHT);
            boolean isHovered = buttonBounds[i].contains(mx, my);

            // Button Fill
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(isHovered ? hoverColors[i] : normalColors[i]);
            shapeRenderer.rect(bx, currentY, BUTTON_WIDTH, BUTTON_HEIGHT);
            shapeRenderer.end();

            // Button Border
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(isHovered ? Color.WHITE : new Color(0.4f, 0.45f, 0.55f, 0.8f));
            shapeRenderer.rect(bx, currentY, BUTTON_WIDTH, BUTTON_HEIGHT);
            shapeRenderer.end();

            // Button Text
            batch.begin();
            font.getData().setScale(1.1f);
            layout.setText(font, buttonLabels[i]);
            float tx = centerX - layout.width / 2f;
            float ty = currentY + BUTTON_HEIGHT / 2f + layout.height / 2f;

            font.setColor(Color.BLACK);
            font.draw(batch, buttonLabels[i], tx + 1, ty - 1);
            font.setColor(Color.WHITE);
            font.draw(batch, buttonLabels[i], tx, ty);
            font.getData().setScale(1.0f);
            batch.end();

            currentY -= BUTTON_SPACING;
        }

        // 5. Bottom Hint Banner
        GameUI.renderControlsHint(batch, shapeRenderer, font, layout,
            "[ 1 / 2 / 3 ] Quick Select   |   [ ENTER ] Start Game   |   [ ESC ] Exit", screenW);

        // 6. Handle Input
        handleInput(mx, my);
    }

    private void handleInput(float mx, float my) {
        if (Gdx.input.justTouched()) {
            if (buttonBounds[0].contains(mx, my) || buttonBounds[1].contains(mx, my)) {
                game.setScreen(new LevelOneScreen(game));
                return;
            } else if (buttonBounds[2].contains(mx, my)) {
                game.setScreen(new LevelTwoScreen(game));
                return;
            } else if (buttonBounds[3].contains(mx, my)) {
                game.setScreen(new GameplayScreen(game));
                return;
            } else if (buttonBounds[4].contains(mx, my)) {
                Gdx.app.exit();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new LevelOneScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            game.setScreen(new LevelOneScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            game.setScreen(new LevelTwoScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            game.setScreen(new GameplayScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() { }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
