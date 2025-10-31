package com.space.ship.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.util.Log;

public class GameEngine extends SurfaceView implements Runnable {
    private static final String TAG = "GameEngine";
    private Thread gameThread;
    private volatile boolean playing;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private int screenWidth, screenHeight;
    
    // سیستم‌های بازی
    private SpaceShip spaceShip;
    private VirtualJoystick joystick;
    private List<Planet> planets;
    private List<Enemy> enemies;
    private List<Star> stars;
    private List<BlackHole> blackHoles;
    private List<Nebula> nebulas;
    private GameState gameState;
    private ParticleSystem particleSystem;
    private CameraSystem cameraSystem;
    private AudioSystem audioSystem;
    private Random random;
    
    // زمان‌سنج‌ها
    private long lastTime;
    private int fps;
    private long gameTime;
    private boolean isInitialized = false;

    public GameEngine(Context context, int screenX, int screenY) {
        super(context);
        this.screenWidth = screenX;
        this.screenHeight = screenY;
        
        initializeEngine();
    }

    private void initializeEngine() {
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        random = new Random();
        
        // ایجاد سیستم‌های اصلی
        cameraSystem = new CameraSystem(screenWidth, screenHeight);
        spaceShip = new SpaceShip(screenWidth / 2, screenHeight / 2, screenWidth, screenHeight, cameraSystem);
        joystick = new VirtualJoystick(screenWidth / 2, screenHeight - 200, 120);
        planets = new ArrayList<>();
        enemies = new ArrayList<>();
        stars = new ArrayList<>();
        blackHoles = new ArrayList<>();
        nebulas = new ArrayList<>();
        gameState = new GameState();
        particleSystem = new ParticleSystem();
        audioSystem = new AudioSystem(getContext());
        
        // ایجاد محیط بازی
        createGalaxyEnvironment();
        startNewLevel();
        
        isInitialized = true;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void createGalaxyEnvironment() {
        // ایجاد ستاره‌ها با تراکم بالا
        for (int i = 0; i < 500; i++) {
            stars.add(new Star(
                random.nextInt(screenWidth * 2) - screenWidth / 2,
                random.nextInt(screenHeight * 2) - screenHeight / 2,
                random.nextFloat() * 4 + 1,
                random.nextFloat() * 0.8f + 0.2f,
                random.nextFloat() * 0.7f + 0.3f
            ));
        }
        
        // ایجاد سحابی‌ها
        for (int i = 0; i < 8; i++) {
            nebulas.add(new Nebula(
                random.nextInt(screenWidth * 3) - screenWidth,
                random.nextInt(screenHeight * 3) - screenHeight,
                random.nextFloat() * 400 + 200,
                random.nextInt(5)
            ));
        }
        
        // ایجاد سیاه‌چاله‌ها
        for (int i = 0; i < 3; i++) {
            blackHoles.add(new BlackHole(
                random.nextInt(screenWidth * 2) - screenWidth / 2,
                random.nextInt(screenHeight * 2) - screenHeight / 2,
                random.nextFloat() * 80 + 40
            ));
        }
    }

    private void startNewLevel() {
        planets.clear();
        enemies.clear();
        
        int currentLevel = gameState.getCurrentLevel();
        
        // ایجاد سیارات - تعداد بر اساس سطح
        int planetCount = 20 + (currentLevel - 1) * 2;
        for (int i = 0; i < planetCount; i++) {
            float x = random.nextFloat() * (screenWidth * 2) - screenWidth / 2;
            float y = random.nextFloat() * (screenHeight * 2) - screenHeight / 2;
            int health = currentLevel * 15 + 50;
            int type = random.nextInt(5);
            planets.add(new Planet(x, y, health, screenWidth, screenHeight, type, currentLevel));
        }
        
        // ایجاد دشمنان - تعداد بر اساس سطح
        int enemyCount = 10 + (currentLevel - 1) * 3;
        for (int i = 0; i < enemyCount; i++) {
            enemies.add(new Enemy(screenWidth, screenHeight, currentLevel, random.nextInt(4)));
        }
        
        audioSystem.playLevelStart();
    }

    @Override
    public void run() {
        while (playing) {
            if (!isInitialized) continue;
            
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastTime) / 1000.0f;
            lastTime = currentTime;
            gameTime += (currentTime - lastTime);
            
            update(deltaTime);
            draw();
            controlFPS();
        }
    }

    private void update(float deltaTime) {
        // بروزرسانی سیستم دوربین
        cameraSystem.update(spaceShip, deltaTime);
        
        // بروزرسانی سفینه
        spaceShip.update(joystick, deltaTime);
        
        // بروزرسانی دشمنان
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(spaceShip, deltaTime);
            
            // بررسی برخورد با سفینه
            if (spaceShip.checkCollision(enemy)) {
                particleSystem.createSupernova(spaceShip.getX(), spaceShip.getY(), 80, 
                    new int[]{Color.RED, Color.ORANGE, Color.YELLOW});
                audioSystem.playExplosion();
                gameState.shipDestroyed();
                if (gameState.getLives() <= 0) {
                    gameOver();
                    return;
                } else {
                    resetShip();
                }
            }
            
            // بررسی برخورد با سیاه‌چاله
            for (BlackHole blackHole : blackHoles) {
                if (enemy.checkBlackHoleCollision(blackHole)) {
                    particleSystem.createBlackHoleEffect(blackHole.getX(), blackHole.getY(), 30);
                    enemies.remove(i);
                    audioSystem.playBlackHole();
                    break;
                }
            }
        }
        
        // بررسی برخورد با سیارات
        for (int i = planets.size() - 1; i >= 0; i--) {
            Planet planet = planets.get(i);
            if (spaceShip.checkCollision(planet)) {
                planet.takeDamage(25);
                particleSystem.createPlanetImpact(planet.getX(), planet.getY(), 25, planet.getType());
                audioSystem.playImpact();
                
                if (planet.isDestroyed()) {
                    planets.remove(i);
                    gameState.planetDestroyed(planet.getType());
                    particleSystem.createPlanetExplosion(planet.getX(), planet.getY(), 100, planet.getType());
                    audioSystem.playPlanetExplosion();
                    
                    // شانس افتادن پاداش
                    if (random.nextFloat() < 0.3f) {
                        // ایجاد پاداش
                    }
                }
            }
        }
        
        // بروزرسانی ذرات
        particleSystem.update(deltaTime);
        
        // بروزرسانی ستاره‌ها (افکت پارالاکس پیشرفته)
        for (Star star : stars) {
            star.update(spaceShip.getVelocityX(), spaceShip.getVelocityY(), deltaTime);
        }
        
        // بروزرسانی سیاه‌چاله‌ها
        for (BlackHole blackHole : blackHoles) {
            blackHole.update(deltaTime);
        }
        
        // بررسی پایان مرحله
        if (planets.isEmpty()) {
            gameState.nextLevel();
            audioSystem.playLevelComplete();
            startNewLevel();
        }
        
        // مدیریت دشمنان
        manageEnemies();
        
        // بروزرسانی صدا
        audioSystem.update(spaceShip, deltaTime);
    }

    private void manageEnemies() {
        // حذف دشمنان خارج از صفحه
        enemies.removeIf(enemy -> enemy.isOutOfScreen(screenWidth, screenHeight));
        
        // اضافه کردن دشمنان جدید
        int currentLevel = gameState.getCurrentLevel();
        int maxEnemies = 10 + (currentLevel - 1) * 3;
        if (enemies.size() < maxEnemies && random.nextInt(100) < (5 + currentLevel * 2)) {
            enemies.add(new Enemy(screenWidth, screenHeight, currentLevel, random.nextInt(4)));
        }
    }

    private void draw() {
        if (!surfaceHolder.getSurface().isValid() || !isInitialized) {
            return;
        }
        
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) return;
        
        try {
            // اعمال تبدیل‌های دوربین
            cameraSystem.applyTransform(canvas);
            
            // رسم محیط کهکشانی
            drawGalaxyBackground(canvas);
            
            // رسم سحابی‌ها
            for (Nebula nebula : nebulas) {
                nebula.draw(canvas, paint);
            }
            
            // رسم سیاه‌چاله‌ها
            for (BlackHole blackHole : blackHoles) {
                blackHole.draw(canvas, paint);
            }
            
            // رسم ذرات
            particleSystem.draw(canvas, paint);
            
            // رسم سیارات
            for (Planet planet : planets) {
                planet.draw(canvas, paint);
            }
            
            // رسم دشمنان
            for (Enemy enemy : enemies) {
                enemy.draw(canvas, paint);
            }
            
            // رسم سفینه
            spaceShip.draw(canvas, paint);
            
            // بازگرداندن تبدیل‌های دوربین
            cameraSystem.restoreTransform(canvas);
            
            // رسم رابط کاربری (بدون تأثیر از دوربین)
            drawHUD(canvas);
            drawJoystick(canvas);
            
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawGalaxyBackground(Canvas canvas) {
        // گرادیانت عمق فضا
        RadialGradient gradient = new RadialGradient(
            screenWidth / 2, screenHeight / 2, Math.max(screenWidth, screenHeight),
            new int[]{
                Color.argb(255, 5, 5, 35),
                Color.argb(255, 2, 2, 20),
                Color.argb(255, 0, 0, 10)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setShader(null);
        
        // ستاره‌های درخشان
        for (Star star : stars) {
            star.draw(canvas, paint);
        }
    }

    private void drawHUD(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(42);
        paint.setShadowLayer(5, 3, 3, Color.BLACK);
        
        // اطلاعات سطح
        canvas.drawText("LEVEL " + gameState.getCurrentLevel(), 50, 80, paint);
        canvas.drawText("PLANETS: " + planets.size(), 50, 140, paint);
        
        // اطلاعات امتیاز
        String scoreText = "SCORE: " + String.format("%,d", gameState.getScore());
        canvas.drawText(scoreText, screenWidth - 400, 80, paint);
        
        String coinsText = "COINS: " + formatCoins(gameState.getCoins());
        canvas.drawText(coinsText, screenWidth - 400, 140, paint);
        
        // سلامت و جان‌ها
        drawHealthBar(canvas);
        drawLives(canvas);
        
        paint.setShadowLayer(0, 0, 0, 0);
    }

    private void drawHealthBar(Canvas canvas) {
        float healthPercent = spaceShip.getHealth() / 100.0f;
        float barWidth = 300;
        float barHeight = 25;
        float x = screenWidth - barWidth - 50;
        float y = screenHeight - 100;
        
        // پس‌زمینه سلامت
        paint.setColor(Color.argb(180, 100, 100, 100));
        canvas.drawRoundRect(x, y, x + barWidth, y + barHeight, 12, 12, paint);
        
        // سلامت فعلی
        int healthColor;
        if (healthPercent > 0.7f) {
            healthColor = Color.argb(220, 0, 255, 100);
        } else if (healthPercent > 0.3f) {
            healthColor = Color.argb(220, 255, 255, 0);
        } else {
            healthColor = Color.argb(220, 255, 50, 50);
        }
        
        paint.setColor(healthColor);
        canvas.drawRoundRect(x, y, x + (barWidth * healthPercent), y + barHeight, 12, 12, paint);
        
        // کادر سلامت
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(x, y, x + barWidth, y + barHeight, 12, 12, paint);
        paint.setStyle(Paint.Style.FILL);
        
        // متن سلامت
        paint.setTextSize(20);
        paint.setColor(Color.WHITE);
        String healthText = "SHIELD: " + (int)spaceShip.getHealth() + "%";
        canvas.drawText(healthText, x + 10, y + 18, paint);
    }

    private void drawLives(Canvas canvas) {
        int lives = gameState.getLives();
        float x = 50;
        float y = screenHeight - 80;
        float size = 30;
        float spacing = 40;
        
        paint.setColor(Color.argb(255, 0, 200, 255));
        for (int i = 0; i < lives; i++) {
            canvas.drawCircle(x + i * spacing, y, size, paint);
            paint.setColor(Color.argb(150, 0, 150, 255));
            canvas.drawCircle(x + i * spacing, y, size * 0.6f, paint);
            paint.setColor(Color.argb(255, 0, 200, 255));
        }
        
        paint.setTextSize(24);
        canvas.drawText("LIVES", x, y - 15, paint);
    }

    private void drawJoystick(Canvas canvas) {
        joystick.draw(canvas, paint);
    }

    private String formatCoins(long coins) {
        if (coins >= 1000000000) {
            return String.format("%.1fB", coins / 1000000000.0);
        } else if (coins >= 1000000) {
            return String.format("%.1fM", coins / 1000000.0);
        } else if (coins >= 1000) {
            return String.format("%.1fK", coins / 1000.0);
        }
        return String.valueOf(coins);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInitialized) return true;
        
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (y > screenHeight - 500) { // منطقه جویستیک
                    joystick.setActive(true, x, y);
                } else {
                    // کنترل‌های لمسی اضافی
                    handleTouchInput(x, y, true);
                }
                break;
            case MotionEvent.ACTION_UP:
                joystick.setActive(false, x, y);
                handleTouchInput(x, y, false);
                break;
        }
        return true;
    }

    private void handleTouchInput(float x, float y, boolean isPressed) {
        // سیستم کنترل لمسی پیشرفته
        if (isPressed) {
            // شلیک یا قابلیت‌های خاص
            if (x < screenWidth / 2) {
                spaceShip.activateShield();
            }
        }
    }

    private void controlFPS() {
        try {
            long currentTime = System.currentTimeMillis();
            long sleepTime = 16 - (currentTime - lastTime); // ~60 FPS
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
            fps = (int)(1000 / (System.currentTimeMillis() - lastTime + 1));
            lastTime = currentTime;
        } catch (InterruptedException e) {
            Log.e(TAG, "Game thread interrupted", e);
        }
    }

    private void resetShip() {
        spaceShip.reset(screenWidth / 2, screenHeight / 2);
        particleSystem.createRespawnEffect(screenWidth / 2, screenHeight / 2, 60);
        audioSystem.playRespawn();
    }

    private void gameOver() {
        playing = false;
        audioSystem.playGameOver();
        // نمایش صفحه Game Over
    }

    public void pauseGame() {
        playing = false;
        if (audioSystem != null) {
            audioSystem.pauseAll();
        }
    }

    public void resumeGame() {
        if (!playing && isInitialized) {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
            if (audioSystem != null) {
                audioSystem.resumeAll();
            }
        }
    }

    public void destroyGame() {
        playing = false;
        isInitialized = false;
        if (audioSystem != null) {
            audioSystem.release();
        }
        if (gameState != null) {
            gameState.saveGame(getContext());
        }
    }
              }
