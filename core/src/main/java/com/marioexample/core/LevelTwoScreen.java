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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Simple Level Two screen.
 *
 * - Background: iceB.jpg
 * - Simple ground with a few thorns
 * - Every thorn has a coin above it
 * - Exactly 4 enemies
 * - Portal at the end
 * - Player can ONLY shoot ice bullets in this level (freeze enemies, don't kill them)
 * - When the player enters the portal, we show "Level 2 Completed"
 *   and softly transition to {@link GameplayScreen} (final boss level).
 */
public class LevelTwoScreen implements Screen {

    // World config – match final level aspect/zoom
    private static final float WORLD_WIDTH = 24f;
    private static final float WORLD_HEIGHT = 13.5f;
    // Total horizontal length of this level (can be wider than the camera width)
    private static final float LEVEL_LENGTH = 50f;

    // Player config (kept same as GameplayScreen so physics feel identical)
    private static final float PLAYER_HEIGHT = 2.5f;
    private static final float PLAYER_WIDTH = PLAYER_HEIGHT * 904f / 1277f;
    private static final float PLAYER_SPEED = 10f;
    private static final float PLAYER_ACCELERATION = 60f;
    private static final float PLAYER_DECELERATION = 80f;
    private static final float GRAVITY = -40f;
    private static final float JUMP_VELOCITY = 16f;

    // Bullet config (only ice bullets here)
    private static final int MAX_BULLETS = 6;
    private static final float BULLET_SPEED = 20f;
    private static final float BULLET_COOLDOWN = 0.25f;

    // Enemy config
    private static final int ENEMY_COUNT = 3;
    // Slightly larger enemies in Level 2 (keep square ratio so sprite is not stretched)
    private static final float ENEMY_WIDTH = 2.5f;
    private static final float ENEMY_HEIGHT = 2.5f;

    // HUD / Exit button (match final level style)
    private static final float EXIT_BTN_WIDTH = 80f;
    private static final float EXIT_BTN_HEIGHT = 30f;

    // Core references
    private final MainGame game;
    private final SpriteBatch batch;
    private OrthographicCamera worldCamera;
    private OrthographicCamera hudCamera;

    // Rendering helpers
    private BitmapFont font;
    private GlyphLayout layout;
    private ShapeRenderer shapeRenderer;

    private final Rectangle tmpPlayerHit = new Rectangle();
    private final Rectangle tmpThornHit = new Rectangle();
    private final Rectangle tmpEnemyHit = new Rectangle();

    // Textures
    private Texture backgroundTexture;
    private Texture groundTexture;
    private Texture standingTexture;
    private Texture jumpTexture;
    private Texture walk1Texture;
    private Texture walk2Texture;
    private Texture walk3Texture;
    private Texture enemyTexture;
    private Texture frozenEnemyTexture;
    private Texture iceBulletTexture;
    private Texture coinTexture;
    private Texture thornTexture;
    private Texture portalTexture;

    // Sounds (similar to final level but simplified for this intro level)
    private Sound jumpSound;
    private Sound iceBulletSound;
    private Sound coinSound;
    private Sound enemyDeathSound;
    private Sound levelCompleteSound;
    private Music backgroundMusic;

    // Player state
    private Rectangle playerRect;
    private float playerVX;
    private float playerVY;
    private boolean onGround;
    private boolean facingRight = true;
    private float walkAnimTime = 0f;

    // Simple animation state
    private enum PlayerAnimState { IDLE, WALKING, JUMPING }
    private PlayerAnimState playerAnimState = PlayerAnimState.IDLE;
    private static final float WALK_ANIM_SPEED = 8f;

    // Level geometry
    private Array<Rectangle> platforms = new Array<Rectangle>();
    private Array<Rectangle> hazards = new Array<Rectangle>();
    private Rectangle goalPortal;
    private float levelWidth;
    
    // Trenches (gaps in ground that kill player if fallen into)
    private Array<Rectangle> trenches = new Array<Rectangle>();

    // Enemies
    private float[] enemyX = new float[ENEMY_COUNT];
    private float[] enemyY = new float[ENEMY_COUNT];
    private float[] enemyVX = new float[ENEMY_COUNT];
    private boolean[] enemyAlive = new boolean[ENEMY_COUNT];
    private float[] enemyLeftPatrol = new float[ENEMY_COUNT];
    private float[] enemyRightPatrol = new float[ENEMY_COUNT];
    private boolean[] enemyFrozen = new boolean[ENEMY_COUNT];  // Whether enemy is frozen by ice bullet
    private float[] enemyFrozenTimer = new float[ENEMY_COUNT];  // Timer for frozen enemy fade-out

    // Coins (one above each thorn)
    private float[] coinX;
    private float[] coinY;
    private boolean[] coinActive;
    private int coinsCollected = 0;

    // Player bullets (ice only)
    private float[] bulletX = new float[MAX_BULLETS];
    private float[] bulletY = new float[MAX_BULLETS];
    private float[] bulletVX = new float[MAX_BULLETS];
    private boolean[] bulletActive = new boolean[MAX_BULLETS];
    private float bulletCooldownTimer = 0f;

    // Virtual Touch Controls (Android / Touchscreens)
    private TouchControls touchControls = new TouchControls();

    // Game state
    private int lives = 3;
    private int score = 0;
    private boolean levelComplete = false;
    private boolean playerDead = false;
    private float levelCompleteTimer = 0f;
    private float playerAlpha = 1f;

    public LevelTwoScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;

        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        worldCamera.zoom = 1.1f; // same zoom as final level
        worldCamera.position.y = WORLD_HEIGHT / 2f + 0.7f; // same vertical offset as final level

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = new BitmapFont();
        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        loadTextures();
        loadSounds();
        initLevelLayout();
        resetPlayer();
    }

    private void loadTextures() {
        backgroundTexture = new Texture(Gdx.files.internal("iceB.jpg"));
        groundTexture = new Texture(Gdx.files.internal("ground.png"));
        standingTexture = new Texture(Gdx.files.internal("standing.png"));
        jumpTexture = new Texture(Gdx.files.internal("jump.png"));
        walk1Texture = new Texture(Gdx.files.internal("walk1.png"));
        walk2Texture = new Texture(Gdx.files.internal("walk2.png"));
        walk3Texture = new Texture(Gdx.files.internal("walk3.png"));
        // Level 2 uses the same enemy sprite as final level, with freezing mechanism
        enemyTexture = new Texture(Gdx.files.internal("enemy.png"));
        frozenEnemyTexture = new Texture(Gdx.files.internal("frozenenemy.png"));
        iceBulletTexture = new Texture(Gdx.files.internal("icebullet.png"));
        coinTexture = new Texture(Gdx.files.internal("coin.png"));
        thornTexture = new Texture(Gdx.files.internal("thorn.png"));
        portalTexture = new Texture(Gdx.files.internal("door.png"));
    }

    private void loadSounds() {
        try {
            jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
        } catch (Exception e) {
            Gdx.app.log("Sound", "jump.wav not found for Level 2");
        }
        try {
            iceBulletSound = Gdx.audio.newSound(Gdx.files.internal("icebullet.mp3"));
        } catch (Exception e) {
            Gdx.app.log("Sound", "icebullet.mp3 not found for Level 2");
        }
        try {
            coinSound = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
        } catch (Exception e) {
            Gdx.app.log("Sound", "coin.wav not found for Level 2");
        }
        try {
            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("enemy_death.wav"));
        } catch (Exception e) {
            Gdx.app.log("Sound", "enemy_death.wav not found for Level 2");
        }

        try {
            levelCompleteSound = Gdx.audio.newSound(Gdx.files.internal("level_complete.wav"));
        } catch (Exception e) {
            Gdx.app.log("Sound", "level_complete.wav not found for Level 2");
        }
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
            if (backgroundMusic != null) {
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.25f);
                // Stop any existing music before playing to prevent duplicates
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.stop();
                }
                backgroundMusic.play();
            }
        } catch (Exception e) {
            Gdx.app.log("Sound", "background_music.mp3 not found for Level 2");
        }
    }

    private void initLevelLayout() {
        platforms.clear();
        hazards.clear();
        trenches.clear();

        // Ground platforms - segmented to create 3 trenches
        float groundHeight = 1.2f;
        float groundY = 0f;
        
        // Segment 1: Start area to trench 1
        platforms.add(new Rectangle(0f, groundY, 9f, groundHeight));
        trenches.add(new Rectangle(9f, -10f, 3.5f, 15f));
        
        // Segment 2: Between trench 1 and 2
        platforms.add(new Rectangle(12.5f, groundY, 9.5f, groundHeight));
        trenches.add(new Rectangle(22f, -10f, 3.5f, 15f));
        
        // Segment 3: Between trench 2 and 3
        platforms.add(new Rectangle(25.5f, groundY, 9.5f, groundHeight));
        trenches.add(new Rectangle(35f, -10f, 3.5f, 15f));
        
        // Segment 4: After trench 3 to portal
        platforms.add(new Rectangle(38.5f, groundY, LEVEL_LENGTH - 38.5f, groundHeight));

        // Trench Coins centered high in the jump arc over each gap
        float coinAboveTrenchY = groundY + groundHeight + 2.5f;
        float[] trenchCoinXs = new float[] {
            10.75f,  // Above trench 1
            23.75f,  // Above trench 2
            36.75f   // Above trench 3
        };
        
        // Thorns placed firmly on solid ground platforms away from trenches and landing zones
        float hazardSize = 1.1f;
        float hazardY = groundY + groundHeight;
        float[] thornXs = new float[] { 
            5.0f,   // On segment 1
            20.0f,  // On segment 2 (after enemy 0)
            33.0f,  // On segment 3 (after enemy 1)
            46.0f   // On segment 4 (after enemy 2)
        };
        
        int thornCount = thornXs.length;
        int totalCoins = thornCount + trenchCoinXs.length;
        coinX = new float[totalCoins];
        coinY = new float[totalCoins];
        coinActive = new boolean[totalCoins];
        
        int coinIndex = 0;
        
        // Add coins above thorns
        for (int i = 0; i < thornCount; i++) {
            float x = thornXs[i];
            Rectangle h = new Rectangle(x, hazardY, hazardSize, hazardSize);
            hazards.add(h);
            coinX[coinIndex] = x + hazardSize / 2f;
            coinY[coinIndex] = hazardY + hazardSize + 1.2f;
            coinActive[coinIndex] = true;
            coinIndex++;
        }
        
        // Add coins above trenches
        for (int i = 0; i < trenchCoinXs.length; i++) {
            coinX[coinIndex] = trenchCoinXs[i];
            coinY[coinIndex] = coinAboveTrenchY;
            coinActive[coinIndex] = true;
            coinIndex++;
        }

        // Portal at the far right
        float portalWidth = 3.0f;
        float portalX = LEVEL_LENGTH - portalWidth - 1f;
        goalPortal = new Rectangle(portalX, 0f, portalWidth, 1f);
        if (portalTexture != null) {
            float aspect = (float) portalTexture.getHeight() / (float) portalTexture.getWidth();
            goalPortal.height = goalPortal.width * aspect;
        } else {
            goalPortal.height = 5f;
        }

        levelWidth = LEVEL_LENGTH;

        // Enemies: patrol cleanly on each segment with safe landing buffers and zero thorn overlap!
        setEnemy(0, 15.0f, groundY + groundHeight, 13.5f, 18.0f, 1.5f); // On segment 2
        setEnemy(1, 28.0f, groundY + groundHeight, 26.5f, 31.0f, 1.6f); // On segment 3
        setEnemy(2, 41.0f, groundY + groundHeight, 39.5f, 44.0f, 1.6f); // On segment 4
        
        // Initialize frozen state arrays
        for (int i = 0; i < ENEMY_COUNT; i++) {
            enemyFrozen[i] = false;
            enemyFrozenTimer[i] = 0f;
        }
    }

    private void setEnemy(int index, float x, float y, float left, float right, float speed) {
        enemyX[index] = x;
        enemyY[index] = y;
        enemyVX[index] = speed;
        enemyAlive[index] = true;
        enemyLeftPatrol[index] = left;
        enemyRightPatrol[index] = right;
        enemyFrozen[index] = false;
        enemyFrozenTimer[index] = 0f;
    }

    private void resetPlayer() {
        playerRect = new Rectangle(2f, 1.2f, PLAYER_WIDTH, PLAYER_HEIGHT);
        playerVX = 0f;
        playerVY = 0f;
        onGround = false;
        facingRight = true;
        levelComplete = false;
        playerDead = false;
        levelCompleteTimer = 0f;
        playerAlpha = 1f;

        // Reset bullets
        for (int i = 0; i < MAX_BULLETS; i++) {
            bulletActive[i] = false;
        }
        bulletCooldownTimer = 0f;
        
        // Reset enemies to initial state (unfreeze them and reset positions)
        float groundLevel = 1.2f;
        setEnemy(0, 13.5f, groundLevel, 12f, 19f, 1.5f);
        setEnemy(1, 25.5f, groundLevel, 24f, 31f, 1.7f);
        setEnemy(2, 38f, groundLevel, 36f, 43f, 1.6f);
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background stretched to screen
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        hudCamera.setToOrtho(false, screenW, screenH);
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, screenW, screenH);
        batch.end();

        // Draw world
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();

        // Platforms
        for (Rectangle p : platforms) {
            float tileSize = 1f;
            int tiles = MathUtils.ceil(p.width / tileSize);
            for (int i = 0; i < tiles; i++) {
                float gx = p.x + i * tileSize;
                batch.draw(groundTexture, gx, p.y, tileSize, p.height);
            }
        }

        // Portal
        batch.draw(portalTexture, goalPortal.x, goalPortal.y, goalPortal.width, goalPortal.height);

        // Hazards (thorns)
        for (Rectangle h : hazards) {
            batch.draw(thornTexture, h.x, h.y, h.width, h.height);
        }

        // Coins
        for (int i = 0; i < coinX.length; i++) {
            if (!coinActive[i]) continue;
            float size = 1.0f;
            batch.draw(coinTexture, coinX[i] - size / 2f, coinY[i] - size / 2f, size, size);
        }

        // Enemies (texture, flip horizontally based on movement direction, handle frozen state)
        for (int i = 0; i < ENEMY_COUNT; i++) {
            if (!enemyAlive[i]) continue;

            // Choose texture and alpha based on frozen state
            Texture tex = enemyFrozen[i] ? frozenEnemyTexture : enemyTexture;
            float alpha = 1f;
            if (enemyFrozen[i]) {
                // Fade out over 5 seconds
                float fadeProgress = Math.min(enemyFrozenTimer[i] / 5f, 1f);
                alpha = 1f - fadeProgress;
            }
            batch.setColor(1f, 1f, 1f, alpha);

            if (enemyVX[i] > 0 && !enemyFrozen[i]) {
                // Moving right: flip horizontally (original faces left)
                batch.draw(tex, enemyX[i] + ENEMY_WIDTH, enemyY[i], -ENEMY_WIDTH, ENEMY_HEIGHT);
            } else {
                // Moving left, idle, or frozen: normal orientation
                batch.draw(tex, enemyX[i], enemyY[i], ENEMY_WIDTH, ENEMY_HEIGHT);
            }
            batch.setColor(Color.WHITE);
        }

        // Bullets (ice only)
        float bw = 1.2f;
        float bh = 0.4f;
        for (int i = 0; i < MAX_BULLETS; i++) {
            if (!bulletActive[i]) continue;
            batch.draw(iceBulletTexture, bulletX[i], bulletY[i] - bh / 2f, bw, bh);
        }

        // Player
        batch.setColor(1f, 1f, 1f, playerAlpha);
        Texture currentTex = standingTexture;
        if (playerAnimState == PlayerAnimState.JUMPING) {
            currentTex = jumpTexture;
        } else if (playerAnimState == PlayerAnimState.WALKING) {
            int frameIndex = (int)(walkAnimTime * WALK_ANIM_SPEED) % 3;
            currentTex = (frameIndex == 0) ? walk1Texture : (frameIndex == 1 ? walk2Texture : walk3Texture);
        }
        if (facingRight) {
            batch.draw(currentTex, playerRect.x, playerRect.y, playerRect.width, playerRect.height);
        } else {
            batch.draw(currentTex, playerRect.x + playerRect.width, playerRect.y, -playerRect.width, playerRect.height);
        }
        batch.setColor(Color.WHITE);

        batch.end();

        // HUD / texts
        renderHud();
    }

    private void handleInput(float delta) {
        if (playerDead || levelComplete) {
            return;
        }

        touchControls.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || touchControls.isLeftPressed();
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || touchControls.isRightPressed();

        if (left) {
            facingRight = false;
            playerVX -= PLAYER_ACCELERATION * delta;
            if (playerVX < -PLAYER_SPEED) playerVX = -PLAYER_SPEED;
        } else if (right) {
            facingRight = true;
            playerVX += PLAYER_ACCELERATION * delta;
            if (playerVX > PLAYER_SPEED) playerVX = PLAYER_SPEED;
        } else {
            if (playerVX > 0) {
                playerVX -= PLAYER_DECELERATION * delta;
                if (playerVX < 0) playerVX = 0;
            } else if (playerVX < 0) {
                playerVX += PLAYER_DECELERATION * delta;
                if (playerVX > 0) playerVX = 0;
            }
        }

        // Animate
        if (!onGround || playerVY > 0) {
            playerAnimState = PlayerAnimState.JUMPING;
        } else if (Math.abs(playerVX) > 0.1f) {
            playerAnimState = PlayerAnimState.WALKING;
            walkAnimTime += delta;
        } else {
            playerAnimState = PlayerAnimState.IDLE;
            walkAnimTime = 0f;
        }

        // Jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.W)
                || Gdx.input.isKeyJustPressed(Input.Keys.UP)
                || touchControls.isJumpJustPressed()) {
            if (onGround) {
                playerVY = JUMP_VELOCITY;
                onGround = false;
                if (jumpSound != null) {
                    jumpSound.play(0.6f);
                }
            }
        }

        // Shooting (K) – ONLY ice bullets in this level
        bulletCooldownTimer -= delta;
        if (bulletCooldownTimer < 0) bulletCooldownTimer = 0;
        if ((Gdx.input.isKeyPressed(Input.Keys.K) || touchControls.isFirePressed()) && bulletCooldownTimer <= 0) {
            shootIceBullet();
            bulletCooldownTimer = BULLET_COOLDOWN;
        }
    }

    private void shootIceBullet() {
        for (int i = 0; i < MAX_BULLETS; i++) {
            if (!bulletActive[i]) {
                bulletActive[i] = true;
                // Bullet spawns from gun nozzle: near front edge (95% of width), at gun height (60% of height)
                float muzzleOffsetX = playerRect.width * 0.95f;
                float muzzleOffsetY = playerRect.height * 0.6f;
                if (facingRight) {
                    bulletX[i] = playerRect.x + muzzleOffsetX;
                    bulletVX[i] = BULLET_SPEED;
                } else {
                    // When facing left, nozzle is at 5% from left edge (same as final level)
                    bulletX[i] = playerRect.x + playerRect.width * (1f - 0.95f);
                    bulletVX[i] = -BULLET_SPEED;
                }
                bulletY[i] = playerRect.y + muzzleOffsetY;
                if (iceBulletSound != null) {
                    iceBulletSound.play(0.7f);
                }
                break;
            }
        }
    }

    private void update(float delta) {
        if (levelComplete) {
            levelCompleteTimer += delta;
            float fadeDuration = 1.3f;
            playerAlpha = Math.max(0.2f, 1f - levelCompleteTimer / fadeDuration);

            // Halt physics while waiting for player's permission to advance
            playerVX = 0;
            playerVY = 0;
            return;
        }

        if (playerDead) {
            return;
        }

        // Physics
        playerVY += GRAVITY * delta;
        float newX = playerRect.x + playerVX * delta;
        float newY = playerRect.y + playerVY * delta;

        // Horizontal collisions
        Rectangle newRectX = new Rectangle(newX, playerRect.y, playerRect.width, playerRect.height);
        for (Rectangle p : platforms) {
            if (newRectX.overlaps(p)) {
                if (playerVX > 0) {
                    newX = p.x - playerRect.width;
                } else if (playerVX < 0) {
                    newX = p.x + p.width;
                }
                playerVX = 0;
                newRectX.x = newX;
            }
        }

        // Vertical collisions
        Rectangle newRectY = new Rectangle(newX, newY, playerRect.width, playerRect.height);
        onGround = false;
        for (Rectangle p : platforms) {
            if (newRectY.overlaps(p)) {
                if (playerVY < 0) {
                    newY = p.y + p.height;
                    onGround = true;
                } else if (playerVY > 0) {
                    newY = p.y - playerRect.height;
                }
                playerVY = 0;
                newRectY.y = newY;
            }
        }

        playerRect.x = newX;
        playerRect.y = newY;

        // Clamp to world
        if (playerRect.x < 0) playerRect.x = 0;
        if (playerRect.x + playerRect.width > levelWidth)
            playerRect.x = levelWidth - playerRect.width;

        // Check if player falls off world or into a trench
        if (playerRect.y < -5) {
            killPlayer();
        } else {
            // Check if player is in a trench and below ground level
            float groundLevel = 1.2f; // Ground platform height
            for (Rectangle trench : trenches) {
                // Check if player's X position overlaps with trench
                if (playerRect.x + playerRect.width > trench.x && 
                    playerRect.x < trench.x + trench.width) {
                    // Player is in trench area, check if below ground
                    if (playerRect.y < groundLevel) {
                        killPlayer();
                        break;
                    }
                }
            }
        }

        // Enemies patrol
        for (int i = 0; i < ENEMY_COUNT; i++) {
            if (!enemyAlive[i]) continue;

            if (enemyFrozen[i]) {
                // While frozen, don't move or interact, just count down fade timer
                enemyFrozenTimer[i] += delta;
                if (enemyFrozenTimer[i] >= 5f) {
                    // After 5 seconds, remove enemy completely
                    enemyAlive[i] = false;
                    enemyFrozen[i] = false;
                }
                continue;
            }

            enemyX[i] += enemyVX[i] * delta;
            if (enemyX[i] < enemyLeftPatrol[i]) {
                enemyX[i] = enemyLeftPatrol[i];
                enemyVX[i] = Math.abs(enemyVX[i]);
            } else if (enemyX[i] + ENEMY_WIDTH > enemyRightPatrol[i]) {
                enemyX[i] = enemyRightPatrol[i] - ENEMY_WIDTH;
                enemyVX[i] = -Math.abs(enemyVX[i]);
            }
        }

        // Bullets
        for (int i = 0; i < MAX_BULLETS; i++) {
            if (!bulletActive[i]) continue;
            bulletX[i] += bulletVX[i] * delta;
            if (bulletX[i] < -5 || bulletX[i] > levelWidth + 5) {
                bulletActive[i] = false;
            }
        }

        // Bullet-enemy collisions
        Rectangle bulletRect = new Rectangle();
        Rectangle enemyRect = new Rectangle();
        for (int i = 0; i < MAX_BULLETS; i++) {
            if (!bulletActive[i]) continue;
            bulletRect.set(bulletX[i], bulletY[i] - 0.2f, 1.2f, 0.4f);
            for (int j = 0; j < ENEMY_COUNT; j++) {
                if (!enemyAlive[j]) continue;
                enemyRect.set(enemyX[j], enemyY[j], ENEMY_WIDTH, ENEMY_HEIGHT);
                if (bulletRect.overlaps(enemyRect)) {
                    bulletActive[i] = false;
                    // Ice bullet: freeze enemy instead of killing
                    enemyFrozen[j] = true;
                    enemyFrozenTimer[j] = 0f;
                    enemyVX[j] = 0f;
                    break;
                }
            }
        }

        // Player-enemy collisions with tight hitboxes
        Rectangle playerHit = getPlayerHitRect();
        for (int i = 0; i < ENEMY_COUNT; i++) {
            if (!enemyAlive[i] || enemyFrozen[i]) continue;  // Skip frozen enemies
            tmpEnemyHit.set(enemyX[i] + ENEMY_WIDTH * 0.15f, enemyY[i], ENEMY_WIDTH * 0.70f, ENEMY_HEIGHT * 0.85f);
            if (playerHit.overlaps(tmpEnemyHit)) {
                killPlayer();
                break;
            }
        }

        // Player-thorn collisions with tight hitboxes
        for (Rectangle h : hazards) {
            if (playerHit.overlaps(getThornHitRect(h))) {
                killPlayer();
                break;
            }
        }

        // Coins
        for (int i = 0; i < coinX.length; i++) {
            if (!coinActive[i]) continue;
            Rectangle coinRect = new Rectangle(coinX[i] - 0.5f, coinY[i] - 0.5f, 1f, 1f);
            if (playerHit.overlaps(coinRect)) {
                coinActive[i] = false;
                coinsCollected++;
                score += 10;
                if (coinSound != null) {
                    coinSound.play(0.7f);
                }
            }
        }

        // Portal / level complete
        if (playerHit.overlaps(goalPortal)) {
            if (!levelComplete) {
                levelComplete = true;
                if (levelCompleteSound != null) {
                    levelCompleteSound.play(0.8f);
                }
                if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                    backgroundMusic.stop();
                }
            }
        }

        // Camera follow
        float halfViewportWidth = worldCamera.viewportWidth / 2f;
        float camX = playerRect.x + playerRect.width / 2f;
        camX = MathUtils.clamp(camX, halfViewportWidth, levelWidth - halfViewportWidth);
        worldCamera.position.x = camX;
        worldCamera.position.y = WORLD_HEIGHT / 2f + 0.7f;
    }

    private Rectangle getPlayerHitRect() {
        float insetX = playerRect.width * 0.22f;
        float insetY = playerRect.height * 0.12f;
        tmpPlayerHit.set(playerRect.x + insetX, playerRect.y + insetY, playerRect.width - insetX * 2f, playerRect.height - insetY * 2f);
        return tmpPlayerHit;
    }

    private Rectangle getThornHitRect(Rectangle h) {
        float insetX = h.width * 0.20f;
        float insetY = h.height * 0.15f;
        tmpThornHit.set(h.x + insetX, h.y, h.width - insetX * 2f, h.height - insetY);
        return tmpThornHit;
    }

    private void killPlayer() {
        lives--;
        if (lives > 0) {
            resetPlayer();
        } else {
            playerDead = true;
        }
    }

    private void renderHud() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        hudCamera.setToOrtho(false, screenW, screenH);
        hudCamera.update();

        batch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        // 1. Top HUD Bar (Ice Cyan theme)
        GameUI.renderTopHud(batch, shapeRenderer, font, layout,
            "SEC-02 // FROZEN CAVERNS", new Color(0.25f, 0.78f, 0.95f, 1f),
            score, coinsCollected, lives, coinTexture, null, screenW, screenH);

        // 2. Bottom Controls Banner
        GameUI.renderControlsHint(batch, shapeRenderer, font, layout,
            "[ ◄ / ► / A / D ] Move   •   [ SPACE / W ] Jump   •   [ K ] Shoot Ice   •   [ ESC ] Menu", screenW);

        // Virtual Touch Controls (Android / Touchscreens)
        if (!playerDead && !levelComplete) {
            touchControls.render(batch, shapeRenderer, font, layout, false);
        }

        // 3. Victory or Defeat Modals
        boolean[] menuClicked = new boolean[1];
        if (playerDead) {
            boolean retry = GameUI.renderGameOverModal(batch, shapeRenderer, font, layout,
                score, coinsCollected, screenW, screenH, menuClicked);
            if (retry || Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                if (backgroundMusic != null && backgroundMusic.isPlaying()) backgroundMusic.stop();
                game.setScreen(new LevelTwoScreen(game));
                return;
            }
        } else if (levelComplete) {
            int action = GameUI.renderLevelPermissionDialog(batch, shapeRenderer, font, layout,
                "SECTOR 2: FROZEN CAVERNS", "SECTOR 3: CITADEL & DRAGON'S LAIR",
                "Ice Cavern neutralized. High-value boss detected in the Citadel ahead.",
                score, coinsCollected, screenW, screenH);

            if (action == GameUI.ACTION_PROCEED) {
                if (backgroundMusic != null && backgroundMusic.isPlaying()) backgroundMusic.stop();
                game.setScreen(new GameplayScreen(game));
                return;
            } else if (action == GameUI.ACTION_REPLAY) {
                if (backgroundMusic != null && backgroundMusic.isPlaying()) backgroundMusic.stop();
                game.setScreen(new LevelTwoScreen(game));
                return;
            } else if (action == GameUI.ACTION_MENU) {
                if (backgroundMusic != null && backgroundMusic.isPlaying()) backgroundMusic.stop();
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        if (menuClicked[0] || GameUI.isExitClicked(screenH) || (!levelComplete && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))) {
            if (backgroundMusic != null && backgroundMusic.isPlaying()) backgroundMusic.stop();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void drawShadowedText(String text, float x, float y, Color color) {
        font.setColor(Color.BLACK);
        font.draw(batch, text, x + 2, y - 2);
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    @Override
    public void resize(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
    }

    @Override
    public void show() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    @Override
    public void hide() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (groundTexture != null) groundTexture.dispose();
        if (standingTexture != null) standingTexture.dispose();
        if (jumpTexture != null) jumpTexture.dispose();
        if (walk1Texture != null) walk1Texture.dispose();
        if (walk2Texture != null) walk2Texture.dispose();
        if (walk3Texture != null) walk3Texture.dispose();
        if (enemyTexture != null) enemyTexture.dispose();
        if (iceBulletTexture != null) iceBulletTexture.dispose();
        if (frozenEnemyTexture != null) frozenEnemyTexture.dispose();
        if (coinTexture != null) coinTexture.dispose();
        if (thornTexture != null) thornTexture.dispose();
        if (portalTexture != null) portalTexture.dispose();
        if (font != null) font.dispose();
        if (jumpSound != null) jumpSound.dispose();
        if (iceBulletSound != null) iceBulletSound.dispose();
        if (coinSound != null) coinSound.dispose();
        if (enemyDeathSound != null) enemyDeathSound.dispose();

        if (levelCompleteSound != null) levelCompleteSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
    }
}


