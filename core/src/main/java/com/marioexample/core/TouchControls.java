package com.marioexample.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

/**
 * Tactical on-screen virtual touch controls for John Wick's Adventure.
 * Enables seamless playability on Android, iPhone/iPad, tablets, and touch-enabled devices
 * directly in the browser on itch.io without downloading!
 */
public class TouchControls {

    // Virtual button touch areas (circles for easy thumb targeting)
    private final Circle leftBtn = new Circle();
    private final Circle rightBtn = new Circle();
    private final Circle jumpBtn = new Circle();
    private final Circle fireBtn = new Circle();
    private final Circle iceBtn = new Circle();

    // Button states
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean jumpJustPressed = false;
    private boolean prevJump = false;
    private boolean firePressed = false;
    private boolean icePressed = false;

    // Palette matching John Wick tactical HUD
    private static final Color COLOR_BG_IDLE = new Color(0.06f, 0.08f, 0.12f, 0.45f);
    private static final Color COLOR_BG_ACTIVE = new Color(0.95f, 0.75f, 0.22f, 0.75f);
    private static final Color COLOR_BORDER_IDLE = new Color(0.85f, 0.70f, 0.25f, 0.65f);
    private static final Color COLOR_BORDER_ACTIVE = new Color(1.0f, 0.95f, 0.5f, 0.95f);

    private static final Color COLOR_FIRE_IDLE = new Color(0.85f, 0.30f, 0.15f, 0.55f);
    private static final Color COLOR_FIRE_ACTIVE = new Color(1.0f, 0.45f, 0.20f, 0.85f);

    private static final Color COLOR_ICE_IDLE = new Color(0.20f, 0.65f, 0.90f, 0.55f);
    private static final Color COLOR_ICE_ACTIVE = new Color(0.40f, 0.85f, 1.0f, 0.85f);

    private static final Color COLOR_JUMP_IDLE = new Color(0.18f, 0.65f, 0.35f, 0.55f);
    private static final Color COLOR_JUMP_ACTIVE = new Color(0.30f, 0.85f, 0.45f, 0.85f);

    public TouchControls() {
    }

    /**
     * Updates button positions according to current viewport dimensions and reads multi-touch input.
     */
    public void update(float screenW, float screenH, boolean isBossLevel) {
        // Layout positions based on screen dimensions
        float btnRadius = Math.min(screenW * 0.065f, 44f);
        if (btnRadius < 34f) btnRadius = 34f;

        float bottomMargin = btnRadius + 18f;
        float leftMargin = btnRadius + 22f;
        float spacing = btnRadius * 2.25f;

        leftBtn.set(leftMargin, bottomMargin, btnRadius);
        rightBtn.set(leftMargin + spacing, bottomMargin, btnRadius);

        float rightMargin = screenW - btnRadius - 22f;
        jumpBtn.set(rightMargin, bottomMargin + 10f, btnRadius * 1.08f);
        fireBtn.set(rightMargin - spacing, bottomMargin, btnRadius);

        if (isBossLevel) {
            iceBtn.set(rightMargin - spacing * 1.9f, bottomMargin, btnRadius);
        } else {
            iceBtn.set(-500f, -500f, 1f);
        }

        // Poll multi-touch pointers (up to 5 fingers supported)
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        firePressed = false;
        icePressed = false;

        for (int p = 0; p < 5; p++) {
            if (Gdx.input.isTouched(p)) {
                float tx = Gdx.input.getX(p);
                float ty = screenH - Gdx.input.getY(p);

                // Add 15% touch tolerance for responsive thumb gameplay
                float hitRadiusSq = btnRadius * 1.15f * (btnRadius * 1.15f);

                if (leftBtn.contains(tx, ty) || distSq(tx, ty, leftBtn.x, leftBtn.y) <= hitRadiusSq) {
                    leftPressed = true;
                }
                if (rightBtn.contains(tx, ty) || distSq(tx, ty, rightBtn.x, rightBtn.y) <= hitRadiusSq) {
                    rightPressed = true;
                }
                if (jumpBtn.contains(tx, ty) || distSq(tx, ty, jumpBtn.x, jumpBtn.y) <= hitRadiusSq * 1.15f) {
                    jumpPressed = true;
                }
                if (fireBtn.contains(tx, ty) || distSq(tx, ty, fireBtn.x, fireBtn.y) <= hitRadiusSq) {
                    firePressed = true;
                }
                if (isBossLevel && (iceBtn.contains(tx, ty) || distSq(tx, ty, iceBtn.x, iceBtn.y) <= hitRadiusSq)) {
                    icePressed = true;
                }
            }
        }

        jumpJustPressed = jumpPressed && !prevJump;
        prevJump = jumpPressed;
    }

    private float distSq(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    /**
     * Renders sleek semi-transparent tactical buttons with icons and labels.
     */
    public void render(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font, GlyphLayout layout, boolean isBossLevel) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // 1. Render Filled Backdrops
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        drawButtonFill(shapes, leftBtn, leftPressed ? COLOR_BG_ACTIVE : COLOR_BG_IDLE);
        drawButtonFill(shapes, rightBtn, rightPressed ? COLOR_BG_ACTIVE : COLOR_BG_IDLE);
        drawButtonFill(shapes, jumpBtn, jumpPressed ? COLOR_JUMP_ACTIVE : COLOR_JUMP_IDLE);
        drawButtonFill(shapes, fireBtn, firePressed ? COLOR_FIRE_ACTIVE : COLOR_FIRE_IDLE);

        if (isBossLevel) {
            drawButtonFill(shapes, iceBtn, icePressed ? COLOR_ICE_ACTIVE : COLOR_ICE_IDLE);
        }

        shapes.end();

        // 2. Render Tactical Outline Rings
        shapes.begin(ShapeRenderer.ShapeType.Line);

        drawButtonRing(shapes, leftBtn, leftPressed ? COLOR_BORDER_ACTIVE : COLOR_BORDER_IDLE);
        drawButtonRing(shapes, rightBtn, rightPressed ? COLOR_BORDER_ACTIVE : COLOR_BORDER_IDLE);
        drawButtonRing(shapes, jumpBtn, jumpPressed ? Color.WHITE : COLOR_BORDER_IDLE);
        drawButtonRing(shapes, fireBtn, firePressed ? Color.WHITE : COLOR_BORDER_IDLE);

        if (isBossLevel) {
            drawButtonRing(shapes, iceBtn, icePressed ? Color.WHITE : COLOR_BORDER_IDLE);
        }

        shapes.end();

        // 3. Render Button Labels & Icons
        batch.begin();
        font.getData().setScale(1.05f);

        drawCenteredText(batch, font, layout, "<", leftBtn.x, leftBtn.y);
        drawCenteredText(batch, font, layout, ">", rightBtn.x, rightBtn.y);

        font.getData().setScale(0.95f);
        drawCenteredText(batch, font, layout, "JUMP", jumpBtn.x, jumpBtn.y);
        drawCenteredText(batch, font, layout, "FIRE", fireBtn.x, fireBtn.y);

        if (isBossLevel) {
            drawCenteredText(batch, font, layout, "ICE", iceBtn.x, iceBtn.y);
        }

        font.getData().setScale(1.0f);
        batch.end();
    }

    private void drawButtonFill(ShapeRenderer shapes, Circle c, Color color) {
        shapes.setColor(color);
        shapes.circle(c.x, c.y, c.radius, 28);
    }

    private void drawButtonRing(ShapeRenderer shapes, Circle c, Color color) {
        shapes.setColor(color);
        shapes.circle(c.x, c.y, c.radius, 28);
        shapes.circle(c.x, c.y, c.radius - 2.5f, 28);
    }

    private void drawCenteredText(SpriteBatch batch, BitmapFont font, GlyphLayout layout, String text, float cx, float cy) {
        layout.setText(font, text);
        float tx = cx - layout.width / 2f;
        float ty = cy + layout.height / 2f;

        font.setColor(Color.BLACK);
        font.draw(batch, text, tx + 1.5f, ty - 1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, text, tx, ty);
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    public boolean isJumpJustPressed() {
        return jumpJustPressed;
    }

    public boolean isFirePressed() {
        return firePressed;
    }

    public boolean isIcePressed() {
        return icePressed;
    }
}
