package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import java.util.Random;

// سیستم دشمنان پیشرفته
class Enemy extends GameObject {
    private float velocityX, velocityY;
    private int screenX, screenY;
    private int level;
    private int type;
    private Random random;
    private float rotation;
    private float pulse;
    private float attackTimer;
    private float health;
    private boolean isAttacking;
    
    // انواع دشمنان
    public static final int TYPE_SCOUT = 0;      // سریع و ضعیف
    public static final int TYPE_FIGHTER = 1;    // متعادل
    public static final int TYPE_BOMBER = 2;     // کند و قوی
    public static final int TYPE_ELITE = 3;      // سریع و قوی
    
    public Enemy(int screenX, int screenY, int level, int type) {
        super(0, 0, getEnemyRadius(type, level));
        this.screenX = screenX;
        this.screenY = screenY;
        this.level = level;
        this.type = type;
        this.random = new Random();
        this.health = getMaxHealth(type, level);
        initializePosition();
    }
    
    private static int getEnemyRadius(int type, int level) {
        switch (type) {
            case TYPE_SCOUT: return 25 + level * 2;
            case TYPE_FIGHTER: return 35 + level * 3;
            case TYPE_BOMBER: return 45 + level * 4;
            case TYPE_ELITE: return 40 + level * 3;
            default: return 30;
        }
    }
    
    private static float getMaxHealth(int type, int level) {
        switch (type) {
            case TYPE_SCOUT: return 50 + level * 10;
            case TYPE_FIGHTER: return 100 + level * 20;
            case TYPE_BOMBER: return 200 + level * 30;
            case TYPE_ELITE: return 150 + level * 25;
            default: return 100;
        }
    }
    
    private void initializePosition() {
        int side = random.nextInt(4);
        float baseSpeed = getBaseSpeed(type);
        
        switch (side) {
            case 0: // بالا
                x = random.nextFloat() * screenX;
                y = -radius;
                velocityX = (random.nextFloat() - 0.5f) * baseSpeed;
                velocityY = baseSpeed;
                break;
            case 1: // راست
                x = screenX + radius;
                y = random.nextFloat() * screenY;
                velocityX = -baseSpeed;
                velocityY = (random.nextFloat() - 0.5f) * baseSpeed;
                break;
            case 2: // پایین
                x = random.nextFloat() * screenX;
                y = screenY + radius;
                velocityX = (random.nextFloat() - 0.5f) * baseSpeed;
                velocityY = -baseSpeed;
                break;
            case 3: // چپ
                x = -radius;
                y = random.nextFloat() * screenY;
                velocityX = baseSpeed;
                velocityY = (random.nextFloat() - 0.5f) * baseSpeed;
                break;
        }
    }
    
    private float getBaseSpeed(int type) {
        switch (type) {
            case TYPE_SCOUT: return 4 + level * 0.3f;
            case TYPE_FIGHTER: return 3 + level * 0.2f;
            case TYPE_BOMBER: return 2 + level * 0.1f;
            case TYPE_ELITE: return 3.5f + level * 0.25f;
            default: return 3;
        }
    }
    
    public void update(SpaceShip ship, float deltaTime) {
        // هوش مصنوعی بر اساس نوع دشمن
        switch (type) {
            case TYPE_SCOUT:
                updateScoutBehavior(ship, deltaTime);
                break;
            case TYPE_FIGHTER:
                updateFighterBehavior(ship, deltaTime);
                break;
            case TYPE_BOMBER:
                updateBomberBehavior(ship, deltaTime);
                break;
            case TYPE_ELITE:
                updateEliteBehavior(ship, deltaTime);
                break;
        }
        
        x += velocityX * deltaTime * 60;
        y += velocityY * deltaTime * 60;
        rotation += getRotationSpeed(type) * deltaTime * 60;
        pulse = (float)Math.sin(System.currentTimeMillis() * 0.005) * 0.2f + 0.8f;
        attackTimer += deltaTime;
    }
    
    private void updateScoutBehavior(SpaceShip ship, float deltaTime) {
        // رفتار: حرکت سریع و غیرقابل پیش‌بینی
        float dx = ship.getX() - x;
        float dy = ship.getY() - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 200) {
            // تعقیب
            velocityX += (dx / distance) * 0.1f * deltaTime * 60;
            velocityY += (dy / distance) * 0.1f * deltaTime * 60;
        } else {
            // حرکت تصادفی برای فرار
            if (random.nextInt(100) < 5) {
                velocityX += (random.nextFloat() - 0.5f) * 2;
                velocityY += (random.nextFloat() - 0.5f) * 2;
            }
        }
        
        limitSpeed(5 + level * 0.4f);
    }
    
    private void updateFighterBehavior(SpaceShip ship, float deltaTime) {
        // رفتار: تعقیب مستقیم و حمله
        float dx = ship.getX() - x;
        float dy = ship.getY() - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        
        velocityX += (dx / distance) * 0.08f * deltaTime * 60;
        velocityY += (dy / distance) * 0.08f * deltaTime * 60;
        
        limitSpeed(4 + level * 0.3f);
        
        // حمله در فاصله نزدیک
        if (distance < 150 && attackTimer > 2.0f) {
            isAttacking = true;
            attackTimer = 0;
        }
    }
    
    private void updateBomberBehavior(SpaceShip ship, float deltaTime) {
        // رفتار: حرکت آهسته و حمله قوی
        float dx = ship.getX() - x;
        float dy = ship.getY() - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 300) {
            velocityX += (dx / distance) * 0.05f * deltaTime * 60;
            velocityY += (dy / distance) * 0.05f * deltaTime * 60;
        }
        
        limitSpeed(3 + level * 0.2f);
        
        // حمله با تأخیر
        if (distance < 250 && attackTimer > 3.0f) {
            isAttacking = true;
            attackTimer = 0;
        }
    }
    
    private void updateEliteBehavior(SpaceShip ship, float deltaTime) {
        // رفتار: هوشمند با حرکات پیچیده
        float dx = ship.getX() - x;
        float dy = ship.getY() - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        
        // پیش‌بینی حرکت سفینه
        float predictX = ship.getX() + ship.getVelocityX() * 0.5f;
        float predictY = ship.getY() + ship.getVelocityY() * 0.5f;
        float predictDx = predictX - x;
        float predictDy = predictY - y;
        float predictDistance = (float)Math.sqrt(predictDx * predictDx + predictDy * predictDy);
        
        velocityX += (predictDx / predictDistance) * 0.1f * deltaTime * 60;
        velocityY += (predictDy / predictDistance) * 0.1f * deltaTime * 60;
        
        limitSpeed(4.5f + level * 0.35f);
        
        // حمله سریع
        if (distance < 180 && attackTimer > 1.5f) {
            isAttacking = true;
            attackTimer = 0;
        }
    }
    
    private void limitSpeed(float maxSpeed) {
        float currentSpeed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (currentSpeed > maxSpeed) {
            velocityX = (velocityX / currentSpeed) * maxSpeed;
            velocityY = (velocityY / currentSpeed) * maxSpeed;
        }
    }
    
    private float getRotationSpeed(int type) {
        switch (type) {
            case TYPE_SCOUT: return 6;
            case TYPE_FIGHTER: return 4;
            case TYPE_BOMBER: return 2;
            case TYPE_ELITE: return 5;
            default: return 3;
        }
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        float healthRatio = health / getMaxHealth(type, level);
        
        switch (type) {
            case TYPE_SCOUT:
                drawScout(canvas, paint, healthRatio);
                break;
            case TYPE_FIGHTER:
                drawFighter(canvas, paint, healthRatio);
                break;
            case TYPE_BOMBER:
                drawBomber(canvas, paint, healthRatio);
                break;
            case TYPE_ELITE:
                drawElite(canvas, paint, healthRatio);
                break;
        }
        
        // نمایش سلامت
        drawHealthBar(canvas, paint, healthRatio);
    }
    
    private void drawScout(Canvas canvas, Paint paint, float healthRatio) {
        // بدنه اصلی
        RadialGradient bodyGradient = new RadialGradient(
            x, y, radius * pulse,
            new int[]{
                Color.argb(255, 255, 100, 100),
                Color.argb(255, 200, 50, 50),
                Color.argb(255, 150, 0, 0)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(bodyGradient);
        canvas.drawCircle(x, y, radius * pulse, paint);
        
        // جزئیات
        paint.setShader(null);
        paint.setColor(Color.argb(255, 255, 200, 200));
        canvas.drawCircle(x, y, radius * 0.6f * pulse, paint);
        
        // باله‌های سریع
        drawWings(canvas, paint, 4, 15);
    }
    
    private void drawFighter(Canvas canvas, Paint paint, float healthRatio) {
        // بدنه زرهی
        RadialGradient bodyGradient = new RadialGradient(
            x, y, radius,
            new int[]{
                Color.argb(255, 255, 150, 100),
                Color.argb(255, 220, 100, 50),
                Color.argb(255, 180, 50, 0)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(bodyGradient);
        canvas.drawCircle(x, y, radius, paint);
        
        // سلاح‌ها
        drawWeapons(canvas, paint, 2);
    }
    
    private void drawBomber(Canvas canvas, Paint paint, float healthRatio) {
        // بدنه سنگین
        RadialGradient bodyGradient = new RadialGradient(
            x, y, radius,
            new int[]{
                Color.argb(255, 150, 150, 255),
                Color.argb(255, 100, 100, 220),
                Color.argb(255, 50, 50, 180)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(bodyGradient);
        canvas.drawCircle(x, y, radius, paint);
        
        // سلاح‌های سنگین
        drawHeavyWeapons(canvas, paint);
    }
    
    private void drawElite(Canvas canvas, Paint paint, float healthRatio) {
        // بدنه پیشرفته
        RadialGradient bodyGradient = new RadialGradient(
            x, y, radius * pulse,
            new int[]{
                Color.argb(255, 255, 50, 255),
                Color.argb(255, 200, 0, 200),
                Color.argb(255, 150, 0, 150)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(bodyGradient);
        canvas.drawCircle(x, y, radius * pulse, paint);
        
        // جزئیات پیشرفته
        drawAdvancedDetails(canvas, paint);
    }
    
    private void drawWings(Canvas canvas, Paint paint, int count, float length) {
        paint.setColor(Color.argb(255, 200, 0, 0));
        for (int i = 0; i < count; i++) {
            float angle = rotation + i * (360f / count);
            float wingX = x + (float)Math.cos(Math.toRadians(angle)) * length;
            float wingY = y + (float)Math.sin(Math.toRadians(angle)) * length;
            canvas.drawCircle(wingX, wingY, 6, paint);
        }
    }
    
    private void drawWeapons(Canvas canvas, Paint paint, int count) {
        paint.setColor(Color.argb(255, 255, 255, 0));
        for (int i = 0; i < count; i++) {
            float angle = 90 + i * 180;
            float weaponX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.8f;
            float weaponY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.8f;
            canvas.drawCircle(weaponX, weaponY, 8, paint);
        }
    }
    
    private void drawHeavyWeapons(Canvas canvas, Paint paint) {
        paint.setColor(Color.argb(255, 255, 100, 0));
        for (int i = 0; i < 4; i++) {
            float angle = rotation + i * 90;
            float weaponX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.7f;
            float weaponY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.7f;
            canvas.drawCircle(weaponX, weaponY, 10, paint);
        }
    }
    
    private void drawAdvancedDetails(Canvas canvas, Paint paint) {
        // حلقه انرژی
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(150, 255, 100, 255));
        canvas.drawCircle(x, y, radius * 1.3f, paint);
        paint.setStyle(Paint.Style.FILL);
        
        // نقاط انرژی
        paint.setColor(Color.argb(255, 255, 255, 100));
        for (int i = 0; i < 8; i++) {
            float angle = rotation * 2 + i * 45;
            float pointX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 1.1f;
            float pointY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 1.1f;
            canvas.drawCircle(pointX, pointY, 4, paint);
        }
    }
    
    private void drawHealthBar(Canvas canvas, Paint paint, float healthRatio) {
        float barWidth = radius * 2;
        float barHeight = 6;
        float barX = x - barWidth / 2;
        float barY = y - radius - 15;
        
        // پس‌زمینه
        paint.setColor(Color.argb(180, 100, 100, 100));
        canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paint);
        
        // سلامت
        int healthColor;
        if (healthRatio > 0.7f) {
            healthColor = Color.argb(220, 0, 255, 0);
        } else if (healthRatio > 0.3f) {
            healthColor = Color.argb(220, 255, 255, 0);
        } else {
            healthColor = Color.argb(220, 255, 0, 0);
        }
        
        paint.setColor(healthColor);
        canvas.drawRect(barX, barY, barX + (barWidth * healthRatio), barY + barHeight, paint);
    }
    
    public boolean checkBlackHoleCollision(BlackHole blackHole) {
        float dx = x - blackHole.getX();
        float dy = y - blackHole.getY();
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        return distance < (radius + blackHole.getSize());
    }
    
    public boolean isOutOfScreen(int screenX, int screenY) {
        return x < -200 || x > screenX + 200 || y < -200 || y > screenY + 200;
    }
    
    public void takeDamage(float damage) {
        health -= damage;
    }
    
    public boolean isDestroyed() {
        return health <= 0;
    }
    
    public int getType() { return type; }
    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) { isAttacking = attacking; }
                           }
