package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import java.util.Random;

// سیستم سیارات پیشرفته
class Planet extends GameObject {
    private int health;
    private int maxHealth;
    private int screenX, screenY;
    private int type;
    private int level;
    private Random random;
    private float rotation;
    private float cloudRotation;
    private boolean hasRings;
    private float pulse;
    
    // انواع سیارات
    public static final int TYPE_EARTH = 0;    // زمینی
    public static final int TYPE_LAVA = 1;     // آتشی
    public static final int TYPE_ICE = 2;      // یخی
    public static final int TYPE_GAS = 3;      // گازی
    public static final int TYPE_TOXIC = 4;    // سمی
    
    public Planet(float x, float y, int health, int screenX, int screenY, int type, int level) {
        super(x, y, 70 + level * 5);
        this.health = health;
        this.maxHealth = health;
        this.screenX = screenX;
        this.screenY = screenY;
        this.type = type;
        this.level = level;
        this.random = new Random();
        this.hasRings = random.nextFloat() > 0.7f;
        this.pulse = random.nextFloat();
        
        // تنظیم شعاع بر اساس نوع و سطح
        this.radius = calculateRadius(type, level);
    }
    
    private int calculateRadius(int type, int level) {
        int baseRadius;
        switch (type) {
            case TYPE_EARTH: baseRadius = 65; break;
            case TYPE_LAVA: baseRadius = 70; break;
            case TYPE_ICE: baseRadius = 60; break;
            case TYPE_GAS: baseRadius = 80; break;
            case TYPE_TOXIC: baseRadius = 75; break;
            default: baseRadius = 70;
        }
        return baseRadius + level * 3;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        rotation += 0.5f;
        cloudRotation += 1.2f;
        pulse += 0.02f;
        if (pulse > 1) pulse = 0;
        
        float healthRatio = (float)health / maxHealth;
        
        // رسم حلقه‌ها (اگر داشته باشد)
        if (hasRings) {
            drawRings(canvas, paint);
        }
        
        // رسم بدنه اصلی سیاره
        drawPlanetBody(canvas, paint, healthRatio);
        
        // رسم اتمسفر
        drawAtmosphere(canvas, paint, healthRatio);
        
        // رسم ابرها (اگر سلامت کافی باشد)
        if (healthRatio > 0.4f) {
            drawClouds(canvas, paint);
        }
        
        // نمایش سلامت
        drawHealthDisplay(canvas, paint, healthRatio);
    }
    
    private void drawPlanetBody(Canvas canvas, Paint paint, float healthRatio) {
        int[] colors = getPlanetColors(type);
        float pulseEffect = 1.0f + (float)Math.sin(pulse * Math.PI * 2) * 0.05f;
        
        RadialGradient planetGradient = new RadialGradient(
            x, y, radius * pulseEffect,
            colors,
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(planetGradient);
        canvas.drawCircle(x, y, radius * pulseEffect, paint);
        paint.setShader(null);
        
        // جزئیات سطح
        drawSurfaceDetails(canvas, paint);
        
        // نورپردازی
        drawPlanetGlow(canvas, paint);
    }
    
    private void drawSurfaceDetails(Canvas canvas, Paint paint) {
        switch (type) {
            case TYPE_EARTH:
                drawEarthDetails(canvas, paint);
                break;
            case TYPE_LAVA:
                drawLavaDetails(canvas, paint);
                break;
            case TYPE_ICE:
                drawIceDetails(canvas, paint);
                break;
            case TYPE_GAS:
                drawGasDetails(canvas, paint);
                break;
            case TYPE_TOXIC:
                drawToxicDetails(canvas, paint);
                break;
        }
    }
    
    private void drawEarthDetails(Canvas canvas, Paint paint) {
        // قاره‌ها
        paint.setColor(Color.argb(200, 50, 80, 40));
        for (int i = 0; i < 5; i++) {
            float angle = rotation + i * 72;
            float continentX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.6f;
            float continentY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.6f;
            canvas.drawCircle(continentX, continentY, radius * 0.25f, paint);
        }
        
        // دریاها
        paint.setColor(Color.argb(180, 30, 60, 120));
        for (int i = 0; i < 3; i++) {
            float angle = rotation * 0.7f + i * 120;
            float oceanX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.4f;
            float oceanY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.4f;
            canvas.drawCircle(oceanX, oceanY, radius * 0.3f, paint);
        }
    }
    
    private void drawLavaDetails(Canvas canvas, Paint paint) {
        // رودخانه‌های گدازه
        paint.setColor(Color.argb(220, 255, 100, 0));
        for (int i = 0; i < 6; i++) {
            float angle = rotation * 1.5f + i * 60;
            float lavaX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.5f;
            float lavaY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.5f;
            
            RadialGradient lavaGradient = new RadialGradient(
                lavaX, lavaY, radius * 0.15f,
                new int[]{
                    Color.argb(255, 255, 150, 0),
                    Color.argb(200, 255, 80, 0),
                    Color.argb(150, 200, 50, 0)
                },
                null,
                Shader.TileMode.CLAMP
            );
            paint.setShader(lavaGradient);
            canvas.drawCircle(lavaX, lavaY, radius * 0.15f, paint);
        }
        paint.setShader(null);
        
        // نقاط آتشفشانی
        paint.setColor(Color.argb(255, 255, 200, 100));
        for (int i = 0; i < 8; i++) {
            float angle = rotation * 0.8f + i * 45;
            float volcanoX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.7f;
            float volcanoY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.7f;
            canvas.drawCircle(volcanoX, volcanoY, radius * 0.08f, paint);
        }
    }
    
    private void drawIceDetails(Canvas canvas, Paint paint) {
        // یخچال‌ها
        paint.setColor(Color.argb(180, 200, 230, 255));
        for (int i = 0; i < 4; i++) {
            float angle = rotation + i * 90;
            float iceX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.5f;
            float iceY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.5f;
            canvas.drawCircle(iceX, iceY, radius * 0.2f, paint);
        }
        
        // درخشش یخ
        paint.setColor(Color.argb(100, 255, 255, 255));
        for (int i = 0; i < 12; i++) {
            float angle = rotation * 2 + i * 30;
            float sparkleX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.8f;
            float sparkleY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.8f;
            canvas.drawCircle(sparkleX, sparkleY, radius * 0.03f, paint);
        }
    }
    
    private void drawGasDetails(Canvas canvas, Paint paint) {
        // گرداب‌های گازی
        for (int i = 0; i < 3; i++) {
            float angle = rotation * 0.6f + i * 120;
            float stormX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.4f;
            float stormY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.4f;
            
            RadialGradient stormGradient = new RadialGradient(
                stormX, stormY, radius * 0.3f,
                new int[]{
                    Color.argb(180, 255, 200, 100),
                    Color.argb(120, 255, 150, 50),
                    Color.argb(80, 200, 100, 30)
                },
                null,
                Shader.TileMode.CLAMP
            );
            paint.setShader(stormGradient);
            canvas.drawCircle(stormX, stormY, radius * 0.3f, paint);
        }
        paint.setShader(null);
    }
    
    private void drawToxicDetails(Canvas canvas, Paint paint) {
        // ابرهای سمی
        paint.setColor(Color.argb(150, 100, 255, 100));
        for (int i = 0; i < 5; i++) {
            float angle = rotation * 0.9f + i * 72;
            float cloudX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.6f;
            float cloudY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.6f;
            canvas.drawCircle(cloudX, cloudY, radius * 0.15f, paint);
        }
        
        // نقاط سمی
        paint.setColor(Color.argb(255, 50, 255, 50));
        for (int i = 0; i < 10; i++) {
            float angle = rotation * 1.2f + i * 36;
            float toxicX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.8f;
            float toxicY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.8f;
            canvas.drawCircle(toxicX, toxicY, radius * 0.05f, paint);
        }
    }
    
    private void drawRings(Canvas canvas, Paint paint) {
        float ringWidth = radius * 0.3f;
        float ringDistance = radius * 1.4f;
        
        for (int ring = 0; ring < 2; ring++) {
            float currentDistance = ringDistance + ring * ringWidth;
            float ringRotation = rotation * (0.7f + ring * 0.3f);
            
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(ringWidth);
            
            int[] ringColors = getRingColors(type);
            LinearGradient ringGradient = new LinearGradient(
                x - currentDistance, y,
                x + currentDistance, y,
                ringColors,
                null,
                Shader.TileMode.CLAMP
            );
            paint.setShader(ringGradient);
            
            canvas.save();
            canvas.rotate(ringRotation, x, y);
            canvas.drawCircle(x, y, currentDistance, paint);
            canvas.restore();
            
            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
        }
    }
    
    private void drawAtmosphere(Canvas canvas, Paint paint, float healthRatio) {
        if (healthRatio > 0.3f) {
            float atmosphereSize = radius + 20;
            int atmosphereAlpha = (int)(80 * healthRatio);
            
            RadialGradient atmosphere = new RadialGradient(
                x, y, atmosphereSize,
                Color.argb(atmosphereAlpha, 100, 180, 255),
                Color.argb(0, 100, 180, 255),
                Shader.TileMode.CLAMP
            );
            paint.setShader(atmosphere);
            canvas.drawCircle(x, y, atmosphereSize, paint);
            paint.setShader(null);
        }
    }
    
    private void drawClouds(Canvas canvas, Paint paint) {
        paint.setColor(Color.argb(120, 255, 255, 255));
        for (int i = 0; i < 4; i++) {
            float angle = cloudRotation + i * 90;
            float cloudX = x + (float)Math.cos(Math.toRadians(angle)) * radius * 0.7f;
            float cloudY = y + (float)Math.sin(Math.toRadians(angle)) * radius * 0.7f;
            canvas.drawCircle(cloudX, cloudY, radius * 0.15f, paint);
        }
    }
    
    private void drawPlanetGlow(Canvas canvas, Paint paint) {
        RadialGradient glow = new RadialGradient(
            x, y, radius * 1.5f,
            Color.argb(50, 255, 255, 255),
            Color.argb(0, 255, 255, 255),
            Shader.TileMode.CLAMP
        );
        paint.setShader(glow);
        canvas.drawCircle(x, y, radius * 1.5f, paint);
        paint.setShader(null);
    }
    
    private void drawHealthDisplay(Canvas canvas, Paint paint, float healthRatio) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        
        String healthText = String.valueOf(health);
        canvas.drawText(healthText, x, y + 8, paint);
        
        // نوار سلامت کوچک
        float barWidth = radius * 1.5f;
        float barHeight = 4;
        float barX = x - barWidth / 2;
        float barY = y - radius - 10;
        
        paint.setColor(Color.argb(150, 100, 100, 100));
        canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paint);
        
        int healthColor = getHealthColor(healthRatio);
        paint.setColor(healthColor);
        canvas.drawRect(barX, barY, barX + (barWidth * healthRatio), barY + barHeight, paint);
    }
    
    private int[] getPlanetColors(int type) {
        switch (type) {
            case TYPE_EARTH:
                return new int[]{
                    Color.argb(255, 70, 120, 180),
                    Color.argb(255, 50, 100, 60),
                    Color.argb(255, 30, 80, 40)
                };
            case TYPE_LAVA:
                return new int[]{
                    Color.argb(255, 220, 100, 50),
                    Color.argb(255, 180, 60, 30),
                    Color.argb(255, 140, 40, 20)
                };
            case TYPE_ICE:
                return new int[]{
                    Color.argb(255, 180, 220, 255),
                    Color.argb(255, 140, 190, 240),
                    Color.argb(255, 100, 160, 220)
                };
            case TYPE_GAS:
                return new int[]{
                    Color.argb(255, 255, 200, 100),
                    Color.argb(255, 220, 160, 80),
                    Color.argb(255, 180, 120, 60)
                };
            case TYPE_TOXIC:
                return new int[]{
                    Color.argb(255, 100, 220, 100),
                    Color.argb(255, 70, 180, 70),
                    Color.argb(255, 50, 140, 50)
                };
            default:
                return new int[]{Color.WHITE, Color.GRAY};
        }
    }
    
    private int[] getRingColors(int type) {
        switch (type) {
            case TYPE_GAS:
                return new int[]{
                    Color.argb(200, 255, 200, 100),
                    Color.argb(150, 220, 160, 80),
                    Color.argb(100, 180, 120, 60),
                    Color.argb(50, 140, 80, 40)
                };
            case TYPE_ICE:
                return new int[]{
                    Color.argb(180, 200, 230, 255),
                    Color.argb(140, 170, 210, 240),
                    Color.argb(100, 140, 190, 220),
                    Color.argb(60, 110, 170, 200)
                };
            default:
                return new int[]{
                    Color.argb(150, 200, 200, 200),
                    Color.argb(100, 150, 150, 150),
                    Color.argb(50, 100, 100, 100)
                };
        }
    }
    
    private int getHealthColor(float healthRatio) {
        if (healthRatio > 0.7f) return Color.argb(255, 0, 255, 100);
        if (healthRatio > 0.4f) return Color.argb(255, 255, 255, 0);
        if (healthRatio > 0.2f) return Color.argb(255, 255, 150, 0);
        return Color.argb(255, 255, 50, 50);
    }
    
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }
    
    public boolean isDestroyed() {
        return health <= 0;
    }
    
    public int getType() { return type; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
                               }
