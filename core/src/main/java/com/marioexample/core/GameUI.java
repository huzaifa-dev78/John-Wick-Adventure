package com.marioexample.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Professional UI and HUD system for John Wick's Adventure.
 * Handles top HUD bar, boss health bar, control hints, and victory/defeat modals.
 */
public class GameUI {

    // Action codes for dialogs and modals
    public static final int ACTION_NONE = 0;
    public static final int ACTION_PROCEED = 1;
    public static final int ACTION_REPLAY = 2;
    public static final int ACTION_MENU = 3;

    // Top HUD Dimensions
    public static final float HUD_HEIGHT = 54f;
    public static final float EXIT_BTN_WIDTH = 90f;
    public static final float EXIT_BTN_HEIGHT = 36f;

    // Button bounds for modal clicks
    private static final Rectangle retryBtnBounds = new Rectangle();
    private static final Rectangle menuBtnBounds = new Rectangle();
    private static final Rectangle nextBtnBounds = new Rectangle();
    private static final Rectangle exitBtnBounds = new Rectangle();

    // Button bounds for permission dialog
    private static final Rectangle permProceedBtnBounds = new Rectangle();
    private static final Rectangle permReplayBtnBounds = new Rectangle();
    private static final Rectangle permMenuBtnBounds = new Rectangle();

    // Palette
    private static final Color BG_DARK = new Color(0.08f, 0.09f, 0.12f, 0.88f);
    private static final Color BORDER_GOLD = new Color(0.95f, 0.76f, 0.22f, 0.9f);
    private static final Color BORDER_SUBTLE = new Color(0.3f, 0.35f, 0.45f, 0.6f);
    private static final Color ACCENT_GREEN = new Color(0.18f, 0.75f, 0.35f, 0.95f);
    private static final Color ACCENT_RED = new Color(0.85f, 0.22f, 0.22f, 0.95f);
    private static final Color ACCENT_CYAN = new Color(0.2f, 0.7f, 0.9f, 0.95f);

    /**
     * Renders top HUD with dark glass bar, level badge, score, coins, lives, and exit button.
     */
    public static void renderTopHud(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font, GlyphLayout layout,
                                    String levelTitle, Color themeColor, int score, int coins, int lives,
                                    Texture coinTex, Texture heartTex, float screenW, float screenH) {
        float hudY = screenH - HUD_HEIGHT;
        float mx = Gdx.input.getX();
        float my = screenH - Gdx.input.getY();

        // 1. Draw top bar background & border
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        // Bar background
        shapes.setColor(0.04f, 0.05f, 0.08f, 0.94f);
        shapes.rect(0, hudY, screenW, HUD_HEIGHT);
        // Bottom accent lines (theme color + gold separator)
        shapes.setColor(themeColor.r, themeColor.g, themeColor.b, 0.85f);
        shapes.rect(0, hudY, screenW, 2.5f);
        shapes.setColor(BORDER_GOLD);
        shapes.rect(0, hudY + 2.5f, 220f, 1.5f);

        // Level Title Badge
        float badgeX = 14f;
        float badgeY = hudY + 8f;
        float badgeW = 210f;
        float badgeH = 38f;
        shapes.setColor(themeColor.r * 0.22f, themeColor.g * 0.22f, themeColor.b * 0.22f, 0.85f);
        shapes.rect(badgeX, badgeY, badgeW, badgeH);

        // Tactical Status indicator pip
        shapes.setColor(ACCENT_GREEN);
        shapes.circle(badgeX + 16f, badgeY + badgeH / 2f, 4.5f);

        // Stats card backgrounds
        // Score pill
        float scorePillX = badgeX + badgeW + 14f;
        float scorePillW = 160f;
        shapes.setColor(0.10f, 0.12f, 0.17f, 0.90f);
        shapes.rect(scorePillX, badgeY, scorePillW, badgeH);

        // Coins pill
        float coinPillX = scorePillX + scorePillW + 12f;
        float coinPillW = 125f;
        shapes.rect(coinPillX, badgeY, coinPillW, badgeH);

        // Lives pill
        float livesPillX = coinPillX + coinPillW + 12f;
        float livesPillW = 160f;
        shapes.rect(livesPillX, badgeY, livesPillW, badgeH);

        // Exit button background
        float exitX = screenW - EXIT_BTN_WIDTH - 14f;
        float exitY = hudY + (HUD_HEIGHT - EXIT_BTN_HEIGHT) / 2f;
        exitBtnBounds.set(exitX, exitY, EXIT_BTN_WIDTH, EXIT_BTN_HEIGHT);
        boolean exitHovered = exitBtnBounds.contains(mx, my);

        if (exitHovered) {
            shapes.setColor(0.85f, 0.18f, 0.18f, 1f);
        } else {
            shapes.setColor(0.38f, 0.10f, 0.10f, 0.85f);
        }
        shapes.rect(exitX, exitY, EXIT_BTN_WIDTH, EXIT_BTN_HEIGHT);

        // Draw Graphic Health Shields / Hearts
        float pipStartX = livesPillX + 78f;
        float pipCenterY = badgeY + badgeH / 2f;
        for (int i = 0; i < 3; i++) {
            float px = pipStartX + i * 24f;
            if (i < lives) {
                // Active Life: Glowing Crimson Crest
                shapes.setColor(0.92f, 0.18f, 0.24f, 1f);
            } else {
                // Depleted Life: Muted dark slate
                shapes.setColor(0.18f, 0.20f, 0.26f, 0.6f);
            }
            // Diamond crest
            shapes.triangle(px - 7f, pipCenterY, px, pipCenterY + 8f, px + 7f, pipCenterY);
            shapes.triangle(px - 7f, pipCenterY, px, pipCenterY - 9f, px + 7f, pipCenterY);
        }

        shapes.end();

        // 2. Outlines
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(themeColor.r, themeColor.g, themeColor.b, 0.95f);
        shapes.rect(badgeX, badgeY, badgeW, badgeH);
        shapes.setColor(BORDER_SUBTLE);
        shapes.rect(scorePillX, badgeY, scorePillW, badgeH);
        shapes.rect(coinPillX, badgeY, coinPillW, badgeH);
        shapes.rect(livesPillX, badgeY, livesPillW, badgeH);

        // Outlines for health crests
        for (int i = 0; i < 3; i++) {
            float px = pipStartX + i * 24f;
            if (i < lives) {
                shapes.setColor(BORDER_GOLD);
            } else {
                shapes.setColor(0.35f, 0.38f, 0.45f, 0.5f);
            }
            shapes.line(px - 7f, pipCenterY, px, pipCenterY + 8f);
            shapes.line(px, pipCenterY + 8f, px + 7f, pipCenterY);
            shapes.line(px + 7f, pipCenterY, px, pipCenterY - 9f);
            shapes.line(px, pipCenterY - 9f, px - 7f, pipCenterY);
        }

        if (exitHovered) {
            shapes.setColor(Color.WHITE);
        } else {
            shapes.setColor(ACCENT_RED);
        }
        shapes.rect(exitX, exitY, EXIT_BTN_WIDTH, EXIT_BTN_HEIGHT);
        shapes.end();

        // 3. Draw text and sprites
        batch.begin();

        // Level Title Text
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        layout.setText(font, levelTitle);
        font.draw(batch, levelTitle, badgeX + 28f, badgeY + badgeH / 2f + layout.height / 2f);

        // Score Text
        font.getData().setScale(0.92f);
        font.setColor(ACCENT_CYAN);
        String scoreStr = "SCORE: " + String.format("%04d", score);
        layout.setText(font, scoreStr);
        font.draw(batch, scoreStr, scorePillX + 16f, badgeY + badgeH / 2f + layout.height / 2f);

        // Coin Icon and Count
        if (coinTex != null) {
            batch.draw(coinTex, coinPillX + 10f, badgeY + 7f, 24f, 24f);
        }
        font.setColor(BORDER_GOLD);
        String coinStr = "x " + coins;
        font.draw(batch, coinStr, coinPillX + 44f, badgeY + badgeH / 2f + layout.height / 2f);

        // Lives text label
        font.setColor(Color.WHITE);
        font.draw(batch, "VITAL:", livesPillX + 14f, badgeY + badgeH / 2f + layout.height / 2f);

        // Exit button label
        font.getData().setScale(0.95f);
        font.setColor(Color.WHITE);
        String exitLabel = "ABORT";
        layout.setText(font, exitLabel);
        font.draw(batch, exitLabel, exitX + (EXIT_BTN_WIDTH - layout.width) / 2f, exitY + EXIT_BTN_HEIGHT / 2f + layout.height / 2f);

        font.getData().setScale(1.0f);
        batch.end();
    }

    /**
     * Checks if the exit button was clicked.
     */
    public static boolean isExitClicked(float screenH) {
        if (Gdx.input.justTouched()) {
            float mx = Gdx.input.getX();
            float my = screenH - Gdx.input.getY();
            return exitBtnBounds.contains(mx, my);
        }
        return false;
    }

    /**
     * Renders a sleek control hint banner at the bottom of the screen.
     */
    public static void renderControlsHint(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font,
                                          GlyphLayout layout, String hint, float screenW) {
        float pillW = 620f;
        float pillH = 28f;
        float pillX = (screenW - pillW) / 2f;
        float pillY = 8f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.08f, 0.09f, 0.12f, 0.70f);
        shapes.rect(pillX, pillY, pillW, pillH);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(BORDER_SUBTLE);
        shapes.rect(pillX, pillY, pillW, pillH);
        shapes.end();

        batch.begin();
        font.getData().setScale(0.85f);
        font.setColor(new Color(0.85f, 0.88f, 0.95f, 0.9f));
        layout.setText(font, hint);
        font.draw(batch, hint, pillX + (pillW - layout.width) / 2f, pillY + pillH / 2f + layout.height / 2f);
        font.getData().setScale(1.0f);
        batch.end();
    }

    /**
     * Renders the boss health bar with name and glowing health meter.
     */
    public static void renderBossHealthBar(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font,
                                          GlyphLayout layout, float healthRatio, int currentHp, int maxHp,
                                          float screenW, float screenH) {
        float barW = 440f;
        float barH = 22f;
        float barX = (screenW - barW) / 2f;
        float barY = screenH - HUD_HEIGHT - 44f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Backdrop panel (cyber-tactical carbon frame)
        shapes.setColor(0.04f, 0.05f, 0.08f, 0.92f);
        shapes.rect(barX - 20f, barY - 26f, barW + 40f, barH + 54f);

        // Warning indicator bar on top
        shapes.setColor(ACCENT_RED);
        shapes.rect(barX - 20f, barY + barH + 26f, barW + 40f, 2f);

        // Health channel (dark crimson slot)
        shapes.setColor(0.18f, 0.04f, 0.04f, 0.95f);
        shapes.rect(barX, barY, barW, barH);

        // Molten Health fill
        float fillW = Math.max(0f, barW * healthRatio);
        shapes.setColor(0.95f, 0.20f, 0.18f, 1f);
        shapes.rect(barX, barY, fillW, barH);

        // Specular highlight stripe
        shapes.setColor(1.0f, 0.65f, 0.20f, 0.65f);
        shapes.rect(barX, barY + barH - 4f, fillW, 3f);

        shapes.end();

        // Borders
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(BORDER_GOLD);
        shapes.rect(barX - 20f, barY - 26f, barW + 40f, barH + 54f);
        shapes.setColor(0.85f, 0.25f, 0.25f, 0.85f);
        shapes.rect(barX, barY, barW, barH);
        shapes.end();

        // Text labels
        batch.begin();
        font.getData().setScale(0.95f);
        font.setColor(Color.GOLD);
        String title = "TARGET: THE FIRE DRAGON [ ANCIENT TITAN ]";
        layout.setText(font, title);
        font.draw(batch, title, (screenW - layout.width) / 2f, barY + barH + 18f);

        font.getData().setScale(0.88f);
        font.setColor(Color.WHITE);
        String hpStr = "HEALTH: " + currentHp + " / " + maxHp + " HP (" + (int)(healthRatio * 100f) + "%)";
        layout.setText(font, hpStr);
        font.draw(batch, hpStr, (screenW - layout.width) / 2f, barY + barH / 2f + layout.height / 2f);

        font.getData().setScale(1.0f);
        batch.end();
    }

    /**
     * Renders professional Game Over modal with stats and interactive Retry / Menu buttons.
     * Returns true if Retry clicked, or sets menuClicked array.
     */
    public static boolean renderGameOverModal(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font,
                                              GlyphLayout layout, int score, int coins, float screenW, float screenH,
                                              boolean[] menuClicked) {
        float cardW = 540f;
        float cardH = 340f;
        float cardX = (screenW - cardW) / 2f;
        float cardY = (screenH - cardH) / 2f;

        float mx = Gdx.input.getX();
        float my = screenH - Gdx.input.getY();

        // Fullscreen darken
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.02f, 0.03f, 0.05f, 0.85f);
        shapes.rect(0, 0, screenW, screenH);

        // Card background
        shapes.setColor(0.08f, 0.09f, 0.13f, 0.98f);
        shapes.rect(cardX, cardY, cardW, cardH);

        // Top banner header (crimson)
        float headerH = 64f;
        shapes.setColor(0.50f, 0.10f, 0.10f, 0.98f);
        shapes.rect(cardX, cardY + cardH - headerH, cardW, headerH);
        shapes.setColor(ACCENT_RED);
        shapes.rect(cardX, cardY + cardH - headerH, cardW, 2.5f);

        // Middle Debrief Panel
        float panelX = cardX + 24f;
        float panelW = cardW - 48f;
        float panelY = cardY + 90f;
        float panelH = 165f;
        shapes.setColor(0.04f, 0.06f, 0.09f, 0.88f);
        shapes.rect(panelX, panelY, panelW, panelH);

        // Stat Badges inside panel
        float statPillW = (panelW - 24f) / 2f;
        float statPillH = 36f;
        float statPillY = panelY + 54f;
        shapes.setColor(0.12f, 0.15f, 0.20f, 0.90f);
        shapes.rect(panelX + 8f, statPillY, statPillW, statPillH);
        shapes.rect(panelX + panelW - statPillW - 8f, statPillY, statPillW, statPillH);

        // Retry button
        float btnW = (panelW - 16f) / 2f;
        float btnH = 46f;
        float retryX = panelX;
        float retryY = cardY + 24f;
        retryBtnBounds.set(retryX, retryY, btnW, btnH);
        boolean retryHover = retryBtnBounds.contains(mx, my);

        if (retryHover) {
            shapes.setColor(0.22f, 0.78f, 0.38f, 1f);
        } else {
            shapes.setColor(0.16f, 0.55f, 0.28f, 0.95f);
        }
        shapes.rect(retryX, retryY, btnW, btnH);

        // Menu button
        float menuX = panelX + btnW + 16f;
        float menuY = cardY + 24f;
        menuBtnBounds.set(menuX, menuY, btnW, btnH);
        boolean menuHover = menuBtnBounds.contains(mx, my);

        if (menuHover) {
            shapes.setColor(0.60f, 0.18f, 0.18f, 1f);
        } else {
            shapes.setColor(0.35f, 0.13f, 0.13f, 0.9f);
        }
        shapes.rect(menuX, menuY, btnW, btnH);

        shapes.end();

        // Borders
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(BORDER_GOLD);
        shapes.rect(cardX, cardY, cardW, cardH);
        shapes.setColor(0.35f, 0.40f, 0.55f, 0.7f);
        shapes.rect(panelX, panelY, panelW, panelH);
        shapes.rect(panelX + 8f, statPillY, statPillW, statPillH);
        shapes.rect(panelX + panelW - statPillW - 8f, statPillY, statPillW, statPillH);

        shapes.setColor(retryHover ? Color.WHITE : ACCENT_GREEN);
        shapes.rect(retryX, retryY, btnW, btnH);
        shapes.setColor(menuHover ? Color.WHITE : ACCENT_RED);
        shapes.rect(menuX, menuY, btnW, btnH);
        shapes.end();

        // Text
        batch.begin();
        // Title
        font.getData().setScale(1.30f);
        font.setColor(Color.WHITE);
        String title = "CONTRACT TERMINATED // MISSION FAILED";
        layout.setText(font, title);
        font.draw(batch, title, cardX + (cardW - layout.width) / 2f, cardY + cardH - 16f);

        // Subtitle
        font.getData().setScale(0.85f);
        font.setColor(BORDER_GOLD);
        String sub = "[ HIGH TABLE CASUALTY REPORT - AGENT NEUTRALIZED ]";
        layout.setText(font, sub);
        font.draw(batch, sub, cardX + (cardW - layout.width) / 2f, cardY + cardH - 42f);

        // Debrief text
        font.getData().setScale(0.95f);
        font.setColor(Color.LIGHT_GRAY);
        String debrief = "John Wick has fallen in combat. High Table contract paused.";
        layout.setText(font, debrief);
        font.draw(batch, debrief, panelX + (panelW - layout.width) / 2f, panelY + panelH - 20f);

        // Stats text
        font.getData().setScale(0.92f);
        font.setColor(Color.GOLD);
        String scoreStr = "FINAL SCORE: " + score;
        layout.setText(font, scoreStr);
        font.draw(batch, scoreStr, panelX + 8f + (statPillW - layout.width) / 2f, statPillY + statPillH / 2f + layout.height / 2f);

        font.setColor(BORDER_GOLD);
        String coinStr = "BOUNTY COINS: " + coins;
        layout.setText(font, coinStr);
        font.draw(batch, coinStr, panelX + panelW - statPillW - 8f + (statPillW - layout.width) / 2f, statPillY + statPillH / 2f + layout.height / 2f);

        font.getData().setScale(0.88f);
        font.setColor(Color.YELLOW);
        String tip = "Authorize redeployment to resume from sector start.";
        layout.setText(font, tip);
        font.draw(batch, tip, panelX + (panelW - layout.width) / 2f, panelY + 28f);

        // Buttons text
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        String retryText = "REDEPLOY [ R ]";
        layout.setText(font, retryText);
        font.draw(batch, retryText, retryX + (btnW - layout.width) / 2f, retryY + btnH / 2f + layout.height / 2f);

        String menuText = "ABORT TO MENU [ ESC ]";
        layout.setText(font, menuText);
        font.draw(batch, menuText, menuX + (btnW - layout.width) / 2f, menuY + btnH / 2f + layout.height / 2f);

        font.getData().setScale(1.0f);
        batch.end();

        if (Gdx.input.justTouched()) {
            if (retryHover) return true;
            if (menuHover && menuClicked != null && menuClicked.length > 0) {
                menuClicked[0] = true;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            return true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && menuClicked != null && menuClicked.length > 0) {
            menuClicked[0] = true;
        }
        return false;
    }

    /**
     * Renders a tactical High-Table style "Permission Required / Sector Clearance" modal.
     * Asks player for confirmation before deploying into the next level/sector.
     * 
     * @return ACTION_NONE (0), ACTION_PROCEED (1), ACTION_REPLAY (2), ACTION_MENU (3).
     */
    public static int renderLevelPermissionDialog(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font,
                                                 GlyphLayout layout, String currentSectorName, String nextSectorName,
                                                 String clearanceDetails, int score, int coins,
                                                 float screenW, float screenH) {
        float cardW = 620f;
        float cardH = 410f;
        float cardX = (screenW - cardW) / 2f;
        float cardY = (screenH - cardH) / 2f;

        float mx = Gdx.input.getX();
        float my = screenH - Gdx.input.getY();

        // 1. Dark backdrop veil
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.02f, 0.03f, 0.05f, 0.85f);
        shapes.rect(0, 0, screenW, screenH);

        // 2. Main Dialog Frame
        shapes.setColor(0.08f, 0.10f, 0.14f, 0.98f);
        shapes.rect(cardX, cardY, cardW, cardH);

        // Header banner
        float headerH = 68f;
        shapes.setColor(0.10f, 0.32f, 0.20f, 0.98f);
        shapes.rect(cardX, cardY + cardH - headerH, cardW, headerH);

        // Header accent line
        shapes.setColor(ACCENT_GREEN);
        shapes.rect(cardX, cardY + cardH - headerH, cardW, 2f);

        // Intel / Debriefing Panel
        float panelX = cardX + 24f;
        float panelW = cardW - 48f;
        float panelY = cardY + 124f;
        float panelH = 202f;
        shapes.setColor(0.04f, 0.06f, 0.09f, 0.9f);
        shapes.rect(panelX, panelY, panelW, panelH);

        // Metrics pills inside panel
        float pillW = 260f;
        float pillH = 36f;
        float pillY = panelY + 68f;
        // Score pill
        shapes.setColor(0.12f, 0.15f, 0.20f, 0.9f);
        shapes.rect(panelX + 10f, pillY, pillW, pillH);
        // Coins pill
        shapes.rect(panelX + panelW - pillW - 10f, pillY, pillW, pillH);

        // Authorization Prompt bar inside panel
        shapes.setColor(0.20f, 0.18f, 0.08f, 0.85f);
        shapes.rect(panelX + 10f, panelY + 14f, panelW - 20f, 40f);

        // Buttons
        // Primary Button: Grant Permission & Advance
        float btnW = panelW;
        float btnH = 46f;
        float proceedX = panelX;
        float proceedY = cardY + 66f;
        permProceedBtnBounds.set(proceedX, proceedY, btnW, btnH);
        boolean proceedHover = permProceedBtnBounds.contains(mx, my);

        if (proceedHover) {
            shapes.setColor(0.22f, 0.78f, 0.38f, 1f);
        } else {
            shapes.setColor(0.16f, 0.55f, 0.28f, 0.95f);
        }
        shapes.rect(proceedX, proceedY, btnW, btnH);

        // Secondary & Tertiary Buttons Row
        float subBtnW = (panelW - 16f) / 2f;
        float subBtnH = 38f;

        // Replay Button
        float replayX = panelX;
        float replayY = cardY + 18f;
        permReplayBtnBounds.set(replayX, replayY, subBtnW, subBtnH);
        boolean replayHover = permReplayBtnBounds.contains(mx, my);

        if (replayHover) {
            shapes.setColor(0.26f, 0.36f, 0.50f, 1f);
        } else {
            shapes.setColor(0.16f, 0.22f, 0.32f, 0.9f);
        }
        shapes.rect(replayX, replayY, subBtnW, subBtnH);

        // Abort / Menu Button
        float menuX = panelX + subBtnW + 16f;
        float menuY = cardY + 18f;
        permMenuBtnBounds.set(menuX, menuY, subBtnW, subBtnH);
        boolean menuHover = permMenuBtnBounds.contains(mx, my);

        if (menuHover) {
            shapes.setColor(0.55f, 0.18f, 0.18f, 1f);
        } else {
            shapes.setColor(0.35f, 0.13f, 0.13f, 0.9f);
        }
        shapes.rect(menuX, menuY, subBtnW, subBtnH);

        shapes.end();

        // 3. Crisp Outlines & Borders
        shapes.begin(ShapeRenderer.ShapeType.Line);
        // Outer card gold border
        shapes.setColor(BORDER_GOLD);
        shapes.rect(cardX, cardY, cardW, cardH);
        // Intel panel cyan border
        shapes.setColor(0.25f, 0.40f, 0.55f, 0.7f);
        shapes.rect(panelX, panelY, panelW, panelH);
        // Metrics pills subtle borders
        shapes.setColor(BORDER_SUBTLE);
        shapes.rect(panelX + 10f, pillY, pillW, pillH);
        shapes.rect(panelX + panelW - pillW - 10f, pillY, pillW, pillH);
        // Authorization prompt border (amber/gold)
        shapes.setColor(0.85f, 0.65f, 0.20f, 0.8f);
        shapes.rect(panelX + 10f, panelY + 14f, panelW - 20f, 40f);

        // Button borders
        shapes.setColor(proceedHover ? Color.WHITE : ACCENT_GREEN);
        shapes.rect(proceedX, proceedY, btnW, btnH);
        shapes.setColor(replayHover ? Color.WHITE : ACCENT_CYAN);
        shapes.rect(replayX, replayY, subBtnW, subBtnH);
        shapes.setColor(menuHover ? Color.WHITE : ACCENT_RED);
        shapes.rect(menuX, menuY, subBtnW, subBtnH);
        shapes.end();

        // 4. Professional Typography & Badges
        batch.begin();

        // Header Title
        font.getData().setScale(1.35f);
        font.setColor(Color.WHITE);
        String headerTitle = "SECTOR SECURED  //  CLEARANCE GRANTED";
        layout.setText(font, headerTitle);
        font.draw(batch, headerTitle, cardX + (cardW - layout.width) / 2f, cardY + cardH - 18f);

        // Header Subtitle / Tag
        font.getData().setScale(0.85f);
        font.setColor(BORDER_GOLD);
        String subBadge = "[ HIGH TABLE TACTICAL DISPATCH - AUTHORIZATION PROTOCOL ]";
        layout.setText(font, subBadge);
        font.draw(batch, subBadge, cardX + (cardW - layout.width) / 2f, cardY + cardH - 45f);

        // Sector Transit route
        font.getData().setScale(1.10f);
        font.setColor(ACCENT_CYAN);
        String routeText = currentSectorName + "   >>>   " + nextSectorName;
        layout.setText(font, routeText);
        font.draw(batch, routeText, panelX + (panelW - layout.width) / 2f, panelY + panelH - 20f);

        // Mission debrief / clearance description
        font.getData().setScale(0.92f);
        font.setColor(Color.LIGHT_GRAY);
        layout.setText(font, clearanceDetails);
        font.draw(batch, clearanceDetails, panelX + (panelW - layout.width) / 2f, panelY + panelH - 52f);

        // Metrics text
        font.getData().setScale(0.95f);
        font.setColor(Color.GOLD);
        String scoreText = "MISSION SCORE: " + score;
        layout.setText(font, scoreText);
        font.draw(batch, scoreText, panelX + 10f + (pillW - layout.width) / 2f, pillY + pillH / 2f + layout.height / 2f);

        font.setColor(BORDER_GOLD);
        String coinsText = "CONTINENTAL COINS: " + coins;
        layout.setText(font, coinsText);
        font.draw(batch, coinsText, panelX + panelW - pillW - 10f + (pillW - layout.width) / 2f, pillY + pillH / 2f + layout.height / 2f);

        // Permission Inquiry Prompt
        font.getData().setScale(0.95f);
        font.setColor(Color.YELLOW);
        String permPrompt = "PERMISSION REQUIRED: Do you authorize deployment to the next sector?";
        layout.setText(font, permPrompt);
        font.draw(batch, permPrompt, panelX + (panelW - layout.width) / 2f, panelY + 39f);

        // Primary Proceed Button Text
        font.getData().setScale(1.05f);
        font.setColor(Color.WHITE);
        String proceedText = "GRANT PERMISSION & ADVANCE [ ENTER / CLICK ]";
        layout.setText(font, proceedText);
        font.draw(batch, proceedText, proceedX + (btnW - layout.width) / 2f, proceedY + btnH / 2f + layout.height / 2f);

        // Replay Button Text
        font.getData().setScale(0.95f);
        font.setColor(Color.WHITE);
        String replayText = "REPLAY SECTOR [ R ]";
        layout.setText(font, replayText);
        font.draw(batch, replayText, replayX + (subBtnW - layout.width) / 2f, replayY + subBtnH / 2f + layout.height / 2f);

        // Menu Button Text
        String abortText = "ABORT TO MENU [ ESC ]";
        layout.setText(font, abortText);
        font.draw(batch, abortText, menuX + (subBtnW - layout.width) / 2f, menuY + subBtnH / 2f + layout.height / 2f);

        font.getData().setScale(1.0f);
        batch.end();

        // 5. Input Check: Mouse clicks & Keyboard keys
        if (Gdx.input.justTouched()) {
            if (proceedHover) return ACTION_PROCEED;
            if (replayHover) return ACTION_REPLAY;
            if (menuHover) return ACTION_MENU;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            return ACTION_PROCEED;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            return ACTION_REPLAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return ACTION_MENU;
        }

        return ACTION_NONE;
    }

    /**
     * Renders professional Level Complete modal.
     * Returns true if Next Level clicked, or sets menuClicked array.
     */
    public static boolean renderLevelCompleteModal(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font,
                                                   GlyphLayout layout, String levelCompletedTitle, String nextLevelTitle,
                                                   int score, int coins, float screenW, float screenH,
                                                   boolean[] menuClicked) {
        float cardW = 500f;
        float cardH = 320f;
        float cardX = (screenW - cardW) / 2f;
        float cardY = (screenH - cardH) / 2f;

        float mx = Gdx.input.getX();
        float my = screenH - Gdx.input.getY();

        // Darken
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0f, 0f, 0f, 0.75f);
        shapes.rect(0, 0, screenW, screenH);

        // Card
        shapes.setColor(0.10f, 0.11f, 0.15f, 0.95f);
        shapes.rect(cardX, cardY, cardW, cardH);

        // Header banner
        shapes.setColor(0.14f, 0.45f, 0.25f, 0.95f);
        shapes.rect(cardX, cardY + cardH - 65f, cardW, 65f);

        // Next button
        float btnW = 200f;
        float btnH = 46f;
        float nextX = cardX + 35f;
        float nextY = cardY + 30f;
        nextBtnBounds.set(nextX, nextY, btnW, btnH);
        boolean nextHover = nextBtnBounds.contains(mx, my);

        if (nextHover) {
            shapes.setColor(ACCENT_GREEN);
        } else {
            shapes.setColor(0.18f, 0.60f, 0.30f, 0.9f);
        }
        shapes.rect(nextX, nextY, btnW, btnH);

        // Menu button
        float menuX = cardX + cardW - btnW - 35f;
        float menuY = cardY + 30f;
        menuBtnBounds.set(menuX, menuY, btnW, btnH);
        boolean menuHover = menuBtnBounds.contains(mx, my);

        if (menuHover) {
            shapes.setColor(ACCENT_CYAN);
        } else {
            shapes.setColor(0.22f, 0.28f, 0.38f, 0.9f);
        }
        shapes.rect(menuX, menuY, btnW, btnH);

        shapes.end();

        // Borders
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(BORDER_GOLD);
        shapes.rect(cardX, cardY, cardW, cardH);
        shapes.setColor(nextHover ? Color.WHITE : ACCENT_GREEN);
        shapes.rect(nextX, nextY, btnW, btnH);
        shapes.setColor(menuHover ? Color.WHITE : ACCENT_CYAN);
        shapes.rect(menuX, menuY, btnW, btnH);
        shapes.end();

        // Text
        batch.begin();
        // Title
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        layout.setText(font, levelCompletedTitle);
        font.draw(batch, levelCompletedTitle, cardX + (cardW - layout.width) / 2f, cardY + cardH - 22f);

        // Subtitle
        font.getData().setScale(1.0f);
        font.setColor(Color.LIGHT_GRAY);
        layout.setText(font, nextLevelTitle);
        font.draw(batch, nextLevelTitle, cardX + (cardW - layout.width) / 2f, cardY + cardH - 90f);

        // Stats
        font.setColor(Color.GOLD);
        String stats = "Current Score: " + score + "     Coins Collected: " + coins;
        layout.setText(font, stats);
        font.draw(batch, stats, cardX + (cardW - layout.width) / 2f, cardY + cardH - 135f);

        // Buttons text
        font.setColor(Color.WHITE);
        String nextText = "CONTINUE [ ENTER ]";
        layout.setText(font, nextText);
        font.draw(batch, nextText, nextX + (btnW - layout.width) / 2f, nextY + btnH / 2f + layout.height / 2f);

        String menuText = "MENU [ ESC ]";
        layout.setText(font, menuText);
        font.draw(batch, menuText, menuX + (btnW - layout.width) / 2f, menuY + btnH / 2f + layout.height / 2f);

        font.getData().setScale(1.0f);
        batch.end();

        if (Gdx.input.justTouched()) {
            if (nextHover) return true;
            if (menuHover && menuClicked != null && menuClicked.length > 0) {
                menuClicked[0] = true;
            }
        }
        return false;
    }
}
