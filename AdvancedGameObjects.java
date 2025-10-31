package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.Path;
import java.util.Random;

// سفینه فضایی فوق پیشرفته
class SpaceShip {
    private float x, y;
    private float velocityX, velocityY;
    private float maxSpeed = 15f;
    private float acceleration = 0.8f;
    private float friction = 0.92f;
    private float health = 100;
    private float shield = 100;
    private int screenX, screenY;
    private Random random = new Random();
    private float engineGlow = 0;
    private float shieldGlow = 0;
    private float rotation = 0;
    private CameraSystem cameraSystem;
    
    // سیستم‌های سفینه
    private boolean shieldActive = false;
    private long lastShieldTime = 0;
    private float[] engineParticles = new float[20];
    
    public SpaceShip(float startX, float startY, int screenX, int screenY, CameraSystem cameraSystem) {
        this.x = startX;
        this.y = startY;
        this.screenX = screenX;
        this.screenY = screenY;
        this.cameraSystem = cameraSystem;
        
        // مقداردهی اولیه ذرات موتور
        for (int i = 0; i < engineParticles.length; i++) {
            engineParticles[i] = random.nextFloat();
        }
    }
    
    public void update(VirtualJoystick joystick, float deltaTime) {
        // اعمال نیروی جویستیک
        if (joystick.isActive()) {
            float forceX = joystick.getForceX();
            float forceY = joystick.getForceY();
            
            velocityX += forceX * acceleration * deltaTime * 60;
            velocityY += forceY * acceleration * deltaTime * 60;
            engineGlow = Math.min(1.0f, engineGlow + deltaTime * 5);
            
            // چرخش سفینه بر اساس جهت حرکت
            rotation = (float)Math.toDegrees(Math.atan2(forceY, forceX));
            
            // محدود کردن سرعت
            float speed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            if (speed > maxSpeed) {
                velocityX = (velocityX / speed) * maxSpeed;
                velocityY = (velocityY / speed) * maxSpeed;
            }
        } else {
            engineGlow = Math.max(0, engineGlow - deltaTime * 3);
        }
        
        // اعمال اصطکاک
        velocityX *= friction;
        velocityY *= friction;
        
        // بروزرسانی موقعیت
        x += velocityX * deltaTime * 60;
        y += velocityY * deltaTime * 60;
        
        // بروزرسانی سیستم محافظ
        if (shieldActive) {
            shieldGlow = (float)Math.sin(System.currentTimeMillis() * 0.01) * 0.3f + 0.7f;
            shield -= deltaTime * 10;
            if (shield <= 0) {
                shieldActive = false;
                shield = 0;
            }
        } else {
            shieldGlow = 0;
            shield = Math.min(100, shield + deltaTime * 5);
        }
        
        // بروزرسانی ذرات موتور
        updateEngineParticles(deltaTime);
        
        // اطلاع به سیستم دوربین
        cameraSystem.follow(x, y, velocityX, velocityY);
    }
    
    private void updateEngineParticles(float deltaTime) {
        for (int i = 0; i < engineParticles.length; i++) {
            engineParticles[i] += deltaTime * (2 + random.nextFloat() * 3);
            if (engineParticles[i] > 1) {
                engineParticles[i] = 0;
            }
        }
    }
    
    public void draw(Canvas canvas, Paint paint) {
        float cameraX = cameraSystem.getX();
        float cameraY = cameraSystem.getY();
        float drawX = x - cameraX;
        float drawY = y - cameraY;
        
        // ذرات موتور
        drawEngineParticles(canvas, paint, drawX, drawY);
        
        // محافظ (اگر فعال باشد)
        if (shieldActive) {
            drawShield(canvas, paint, drawX, drawY);
        }
        
        // بدنه اصلی سفینه
        drawShipBody(canvas, paint, drawX, drawY);
        
        // موتورهای اصلی
        drawMainEngines(canvas, paint, drawX, drawY);
        
        // جزئیات و نورپردازی
        drawShipDetails(canvas, paint, drawX, drawY);
    }
    
    private void drawEngineParticles(Canvas canvas, Paint paint, float x, float y) {
        float enginePower = engineGlow;
        
        for (int i = 0; i < engineParticles.length; i++) {
            float progress = engineParticles[i];
            float particleX = x - (float)Math.cos(Math.toRadians(rotation)) * (40 + progress * 60);
            float particleY = y - (float)Math.sin(Math.toRadians(rotation)) * (40 + progress * 60);
            
            float size = (1 - progress) * (8 + enginePower * 12);
            float alpha = (1 - progress) * (150 + enginePower * 105);
            
            // ذرات رنگی
            int[] colors = {
                Color.argb((int)alpha, 255, 200, 0),
                Color.argb((int)(alpha * 0.7f), 255, 100, 0),
                Color.argb((int)(alpha * 0.3f), 255, 50, 0)
            };
            
            RadialGradient particleGradient = new RadialGradient(
                particleX, particleY, size,
                colors,
                null,
                Shader.TileMode.CLAMP
            );
            paint.setShader(particleGradient);
            canvas.drawCircle(particleX, particleY, size, paint);
        }
        paint.setShader(null);
    }
    
    private void drawShield(Canvas canvas, Paint paint, float x, float y) {
        float shieldSize = 70 + shieldGlow * 10;
        
        RadialGradient shieldGradient = new RadialGradient(
            x, y, shieldSize,
            new int[]{
                Color.argb(80, 0, 200, 255),
                Color.argb(40, 0, 150, 255),
                Color.argb(0, 0, 100, 200)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(shieldGradient);
        canvas.drawCircle(x, y, shieldSize, paint);
        
        // حلقه‌های انرژی
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(150, 0, 200, 255));
        
        for (int i = 0; i < 3; i++) {
            float ringSize = shieldSize * (0.7f + i * 0.15f);
            float ringAlpha = 100 - i * 30;
            paint.setAlpha((int)ringAlpha);
            canvas.drawCircle(x, y, ringSize, paint);
        }
        
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);
    }
    
    private void drawShipBody(Canvas canvas, Paint paint, float x, float y) {
        // بدنه اصلی با گرادیانت سه بعدی
        RadialGradient bodyGradient = new RadialGradient(
            x, y, 40,
            new int[]{
                Color.argb(255, 0, 220, 255),
                Color.argb(255, 0, 150, 220),
                Color.argb(255, 0, 100, 180)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(bodyGradient);
        canvas.drawCircle(x, y, 40, paint);
        
        // کابین خلبان
        paint.setShader(null);
        RadialGradient cockpitGradient = new RadialGradient(
            x, y, 25,
            new int[]{
                Color.argb(220, 200, 240, 255),
                Color.argb(180, 150, 200, 240),
                Color.argb(100, 100, 150, 200)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(cockpitGradient);
        canvas.drawCircle(x, y, 25, paint);
        
        // جزئیات کابین
        paint.setShader(null);
        paint.setColor(Color.argb(255, 100, 180, 255));
        canvas.drawCircle(x, y, 18, paint);
        
        // نور مرکزی
        paint.setColor(Color.argb(200, 255, 255, 255));
        canvas.drawCircle(x, y, 10, paint);
    }
    
    private void drawMainEngines(Canvas canvas, Paint paint, float x, float y) {
        float enginePower = engineGlow;
        
        // موتور چپ
        drawEngine(canvas, paint, x, y, -35, -15, enginePower, rotation);
        drawEngine(canvas, paint, x, y, -35, 15, enginePower, rotation);
        
        // موتور راست
        drawEngine(canvas, paint, x, y, 35, -15, enginePower, rotation);
        drawEngine(canvas, paint, x, y, 35, 15, enginePower, rotation);
    }
    
    private void drawEngine(Canvas canvas, Paint paint, float shipX, float shipY, 
                          float offsetX, float offsetY, float power, float rotation) {
        
        // محاسبه موقعیت موتور با چرخش
        float cos = (float)Math.cos(Math.toRadians(rotation));
        float sin = (float)Math.sin(Math.toRadians(rotation));
        float engineX = shipX + offsetX * cos - offsetY * sin;
        float engineY = shipY + offsetX * sin + offsetY * cos;
        
        // بدنه موتور
        paint.setColor(Color.argb(255, 80, 120, 160));
        canvas.drawCircle(engineX, engineY, 12, paint);
        
        // شعله موتور
        if (power > 0) {
            float flameLength = 15 + power * 25;
            float flameWidth = 8 + power * 8;
            
            Path flamePath = new Path();
            flamePath.moveTo(engineX - flameWidth, engineY);
            flamePath.lineTo(engineX + (float)Math.cos(Math.toRadians(rotation)) * flameLength, 
                           engineY + (float)Math.sin(Math.toRadians(rotation)) * flameLength);
            flamePath.lineTo(engineX + flameWidth, engineY);
            flamePath.close();
            
            int[] flameColors = {
                Color.argb(255, 255, 255, 100),
                Color.argb(200, 255, 150, 0),
                Color.argb(150, 255, 50, 0),
                Color.argb(0, 255, 0, 0)
            };
            
            LinearGradient flameGradient = new LinearGradient(
                engineX, engineY,
                engineX + (float)Math.cos(Math.toRadians(rotation)) * flameLength,
                engineY + (float)Math.sin(Math.toRadians(rotation)) * flameLength,
                flameColors,
                null,
                Shader.TileMode.CLAMP
            );
            paint.setShader(flameGradient);
            canvas.drawPath(flamePath, paint);
            paint.setShader(null);
        }
    }
    
    private void drawShipDetails(Canvas canvas, Paint paint, float x, float y) {
        // باله‌ها
        drawWing(canvas, paint, x, y, -50, -25, rotation);
        drawWing(canvas, paint, x, y, -50, 25, rotation);
        drawWing(canvas, paint, x, y, 50, -25, rotation);
        drawWing(canvas, paint, x, y, 50, 25, rotation);
        
        // نورپردازی محیطی
        RadialGradient glow = new RadialGradient(
            x, y, 60,
            Color.argb(60, 0, 150, 255),
            Color.argb(0, 0, 100, 200),
            Shader.TileMode.CLAMP
        );
        paint.setShader(glow);
        canvas.drawCircle(x, y, 60, paint);
        paint.setShader(null);
    }
    
    private void drawWing(Canvas canvas, Paint paint, float shipX, float shipY, 
                         float offsetX, float offsetY, float rotation) {
        
        float cos = (float)Math.cos(Math.toRadians(rotation));
        float sin = (float)Math.sin(Math.toRadians(rotation));
        float wingX = shipX + offsetX * cos - offsetY * sin;
        float wingY = shipY + offsetX * sin + offsetY * cos;
        
        paint.setColor(Color.argb(255, 0, 120, 200));
        canvas.drawCircle(wingX, wingY, 8, paint);
        
        paint.setColor(Color.argb(200, 0, 180, 255));
        canvas.drawCircle(wingX, wingY, 5, paint);
    }
    
    public boolean checkCollision(GameObject other) {
        if (shieldActive) return false;
        
        float dx = x - other.getX();
        float dy = y - other.getY();
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        return distance < (40 + other.getRadius());
    }
    
    public void takeDamage(float damage) {
        if (!shieldActive) {
            health = Math.max(0, health - damage);
        }
    }
    
    public void activateShield() {
        if (shield >= 30 && !shieldActive) {
            shieldActive = true;
            lastShieldTime = System.currentTimeMillis();
        }
    }
    
    public void reset(float newX, float newY) {
        x = newX;
        y = newY;
        velocityX = 0;
        velocityY = 0;
        health = 100;
        shield = 100;
        shieldActive = false;
        engineGlow = 0;
    }
    
    // متدهای دسترسی
    public float getX() { return x; }
    public float getY() { return y; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public float getHealth() { return health; }
    public float getShield() { return shield; }
    public boolean isShieldActive() { return shieldActive; }
                   }
