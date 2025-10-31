package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import java.util.Random;

// سیستم قدرت‌آپگریدها و پاداش‌ها
class PowerUp extends GameObject {
    private int type;
    private float rotation;
    private float floatOffset;
    private Random random;
    private boolean collected;
    
    // انواع قدرت‌آپگریدها
    public static final int TYPE_HEALTH = 0;
    public static final int TYPE_SHIELD = 1;
    public static final int TYPE_SPEED = 2;
    public static final int TYPE_WEAPON = 3;
    public static final int TYPE_COIN = 4;
    public static final int TYPE_MULTIPLIER = 5;
    
    public PowerUp(float x, float y, int type) {
        super(x, y, 20);
        this.type = type;
        this.random = new Random();
        this.rotation = random.nextFloat() * 360;
        this.floatOffset = random.nextFloat() * 100;
        this.collected = false;
    }
    
    public void update(float deltaTime) {
        rotation += 2 * deltaTime * 60;
        floatOffset += deltaTime * 60;
        
        // حرکت شناور
        y += (float)Math.sin(floatOffset * 0.1f) * 0.5f * deltaTime * 60;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (collected) return;
        
        float pulse = (float)Math.sin(floatOffset * 0.05f) * 0.2f + 0.8f;
        float currentRadius = radius * pulse;
        
        // بدنه اصلی
        drawMainBody(canvas, paint, currentRadius);
        
        // نماد قدرت‌آپگرید
        drawSymbol(canvas, paint, currentRadius);
        
        // درخشش
        drawGlow(canvas, paint, currentRadius);
    }
    
    private void drawMainBody(Canvas canvas, Paint paint, float currentRadius) {
        int[] colors = getPowerUpColors(type);
        
        RadialGradient bodyGradient = new RadialGradient(
            x, y, currentRadius,
            colors,
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(bodyGradient);
        canvas.drawCircle(x, y, currentRadius, paint);
        paint.setShader(null);
        
        // حلقه بیرونی
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(200, 255, 255, 255));
        canvas.drawCircle(x, y, currentRadius, paint);
        paint.setStyle(Paint.Style.FILL);
    }
    
    private void drawSymbol(Canvas canvas, Paint paint, float currentRadius) {
        paint.setColor(Color.WHITE);
        
        switch (type) {
            case TYPE_HEALTH:
                // علامت بعلاوه
                float crossSize = currentRadius * 0.4f;
                paint.setStrokeWidth(4);
                canvas.drawLine(x - crossSize, y, x + crossSize, y, paint);
                canvas.drawLine(x, y - crossSize, x, y + crossSize, paint);
                break;
                
            case TYPE_SHIELD:
                // علامت سپر
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(4);
                canvas.drawCircle(x, y, currentRadius * 0.6f, paint);
                paint.setStyle(Paint.Style.FILL);
                break;
                
            case TYPE_SPEED:
                // علامت صاعقه
                drawLightning(canvas, paint, currentRadius);
                break;
                
            case TYPE_WEAPON:
                // علامت سلاح
                drawWeaponSymbol(canvas, paint, currentRadius);
                break;
                
            case TYPE_COIN:
                // علامت سکه
                drawCoinSymbol(canvas, paint, currentRadius);
                break;
                
            case TYPE_MULTIPLIER:
                // علامت ضرب
                drawMultiplierSymbol(canvas, paint, currentRadius);
                break;
        }
    }
    
    private void drawLightning(Canvas canvas, Paint paint, float currentRadius) {
        float size = currentRadius * 0.6f;
        Path lightning = new Path();
        lightning.moveTo(x - size * 0.3f, y - size);
        lightning.lineTo(x, y - size * 0.2f);
        lightning.lineTo(x - size * 0.2f, y);
        lightning.lineTo(x + size * 0.3f, y + size);
        lightning.lineTo(x, y + size * 0.2f);
        lightning.lineTo(x + size * 0.2f, y);
        lightning.close();
        
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(lightning, paint);
    }
    
    private void drawWeaponSymbol(Canvas canvas, Paint paint, float currentRadius) {
        float size = currentRadius * 0.5f;
        paint.setStrokeWidth(4);
        canvas.drawLine(x - size, y, x + size, y, paint);
        canvas.drawLine(x, y - size, x, y + size, paint);
        
        // نقاط انتهایی
        canvas.drawCircle(x - size, y, 3, paint);
        canvas.drawCircle(x + size, y, 3, paint);
        canvas.drawCircle(x, y - size, 3, paint);
        canvas.drawCircle(x, y + size, 3, paint);
    }
    
    private void drawCoinSymbol(Canvas canvas, Paint paint, float currentRadius) {
        paint.setColor(Color.argb(255, 255, 215, 0)); // رنگ طلایی
        canvas.drawCircle(x, y, currentRadius * 0.4f, paint);
        
        paint.setColor(Color.argb(255, 255, 255, 100));
        canvas.drawCircle(x, y, currentRadius * 0.3f, paint);
        
        // علامت دلار
        paint.setColor(Color.argb(255, 100, 80, 0));
        paint.setTextSize(currentRadius * 0.8f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("$", x, y + currentRadius * 0.3f, paint);
    }
    
    private void drawMultiplierSymbol(Canvas canvas, Paint paint, float currentRadius) {
        paint.setTextSize(currentRadius * 0.8f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("×", x, y + currentRadius * 0.3f, paint);
    }
    
    private void drawGlow(Canvas canvas, Paint paint, float currentRadius) {
        int glowColor = getGlowColor(type);
        
        RadialGradient glow = new RadialGradient(
            x, y, currentRadius * 2,
            Color.argb(80, Color.red(glowColor), Color.green(glowColor), Color.blue(glowColor)),
            Color.argb(0, Color.red(glowColor), Color.green(glowColor), Color.blue(glowColor)),
            Shader.TileMode.CLAMP
        );
        paint.setShader(glow);
        canvas.drawCircle(x, y, currentRadius * 2, paint);
        paint.setShader(null);
    }
    
    private int[] getPowerUpColors(int type) {
        switch (type) {
            case TYPE_HEALTH:
                return new int[]{
                    Color.argb(255, 255, 50, 50),
                    Color.argb(255, 200, 30, 30),
                    Color.argb(255, 150, 20, 20)
                };
            case TYPE_SHIELD:
                return new int[]{
                    Color.argb(255, 50, 150, 255),
                    Color.argb(255, 30, 100, 200),
                    Color.argb(255, 20, 70, 150)
                };
            case TYPE_SPEED:
                return new int[]{
                    Color.argb(255, 50, 255, 50),
                    Color.argb(255, 30, 200, 30),
                    Color.argb(255, 20, 150, 20)
                };
            case TYPE_WEAPON:
                return new int[]{
                    Color.argb(255, 255, 255, 50),
                    Color.argb(255, 200, 200, 30),
                    Color.argb(255, 150, 150, 20)
                };
            case TYPE_COIN:
                return new int[]{
                    Color.argb(255, 255, 215, 0),
                    Color.argb(255, 255, 200, 0),
                    Color.argb(255, 255, 180, 0)
                };
            case TYPE_MULTIPLIER:
                return new int[]{
                    Color.argb(255, 255, 100, 255),
                    Color.argb(255, 220, 70, 220),
                    Color.argb(255, 180, 50, 180)
                };
            default:
                return new int[]{Color.WHITE, Color.GRAY};
        }
    }
    
    private int getGlowColor(int type) {
        switch (type) {
            case TYPE_HEALTH: return Color.RED;
            case TYPE_SHIELD: return Color.BLUE;
            case TYPE_SPEED: return Color.GREEN;
            case TYPE_WEAPON: return Color.YELLOW;
            case TYPE_COIN: return Color.argb(255, 255, 215, 0);
            case TYPE_MULTIPLIER: return Color.MAGENTA;
            default: return Color.WHITE;
        }
    }
    
    public int getType() { return type; }
    public boolean isCollected() { return collected; }
    public void collect() { collected = true; }
    
    public void applyEffect(SpaceShip ship, GameState gameState) {
        switch (type) {
            case TYPE_HEALTH:
                ship.takeDamage(-30); // بهبود سلامت
                break;
            case TYPE_SHIELD:
                ship.activateShield();
                break;
            case TYPE_SPEED:
                // افزایش سرعت موقت
                break;
            case TYPE_WEAPON:
                // افزایش قدرت سلاح موقت
                break;
            case TYPE_COIN:
                gameState.addCoins(100000);
                break;
            case TYPE_MULTIPLIER:
                // فعال‌سازی ضریب امتیاز
                break;
        }
    }
}

// برای استفاده از Path
import android.graphics.Path;
