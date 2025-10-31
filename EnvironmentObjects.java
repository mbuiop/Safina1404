package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import java.util.Random;

// ستاره پیشرفته با افکت‌های پارالاکس
class Star {
    private float x, y;
    private float size;
    private float speed;
    private float brightness;
    private float twinkle;
    private float twinkleSpeed;
    private Random random;
    private int color;
    
    public Star(float x, float y, float size, float speed, float brightness) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.brightness = brightness;
        this.random = new Random();
        this.twinkle = random.nextFloat();
        this.twinkleSpeed = random.nextFloat() * 0.02f + 0.01f;
        
        // رنگ‌های مختلف برای ستاره‌ها
        int colorType = random.nextInt(4);
        switch (colorType) {
            case 0: // سفید-آبی
                color = Color.argb(255, 200, 220, 255);
                break;
            case 1: // سفید-زرد
                color = Color.argb(255, 255, 250, 200);
                break;
            case 2: // سفید-قرمز
                color = Color.argb(255, 255, 200, 200);
                break;
            case 3: // سفید-سبز
                color = Color.argb(255, 200, 255, 200);
                break;
        }
    }
    
    public void update(float shipVelX, float shipVelY, float deltaTime) {
        // حرکت پارالاکس بر اساس سرعت سفینه
        x -= shipVelX * speed * 0.15f * deltaTime * 60;
        y -= shipVelY * speed * 0.15f * deltaTime * 60;
        
        // سوسو زدن
        twinkle += twinkleSpeed * deltaTime * 60;
        if (twinkle > 1) twinkle = 0;
        
        // بازگرداندن ستاره‌های خارج از صفحه
        if (x < -100) x = 2200;
        if (x > 2200) x = -100;
        if (y < -100) y = 1300;
        if (y > 1300) y = -100;
    }
    
    public void draw(Canvas canvas, Paint paint) {
        float currentBrightness = brightness * (0.7f + (float)Math.sin(twinkle * Math.PI * 2) * 0.3f);
        int alpha = (int)(255 * currentBrightness);
        
        // هسته ستاره
        paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawCircle(x, y, size, paint);
        
        // هاله نور
        RadialGradient glow = new RadialGradient(
            x, y, size * 3,
            Color.argb(alpha/3, 255, 255, 255),
            Color.argb(0, 255, 255, 255),
            Shader.TileMode.CLAMP
        );
        paint.setShader(glow);
        canvas.drawCircle(x, y, size * 3, paint);
        paint.setShader(null);
        
        // پرتوهای نور برای ستاره‌های بزرگ
        if (size > 2) {
            paint.setColor(Color.argb(alpha/4, 255, 255, 255));
            for (int i = 0; i < 4; i++) {
                float angle = i * 45 + twinkle * 360;
                float rayLength = size * 4;
                float endX = x + (float)Math.cos(Math.toRadians(angle)) * rayLength;
                float endY = y + (float)Math.sin(Math.toRadians(angle)) * rayLength;
                paint.setStrokeWidth(size * 0.5f);
                canvas.drawLine(x, y, endX, endY, paint);
            }
        }
    }
}

// سحابی
class Nebula {
    private float x, y;
    private float size;
    private int type;
    private float rotation;
    private float rotationSpeed;
    private Random random;
    
    public Nebula(float x, float y, float size, int type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
        this.random = new Random();
        this.rotation = random.nextFloat() * 360;
        this.rotationSpeed = (random.nextFloat() - 0.5f) * 0.2f;
    }
    
    public void draw(Canvas canvas, Paint paint) {
        int[] colors;
        
        switch (type) {
            case 0: // سحابی آبی-بنفش
                colors = new int[]{
                    Color.argb(40, 80, 80, 255),
                    Color.argb(30, 120, 80, 200),
                    Color.argb(20, 160, 100, 255),
                    Color.argb(0, 200, 150, 255)
                };
                break;
            case 1: // سحابی قرمز-نارنجی
                colors = new int[]{
                    Color.argb(35, 255, 80, 50),
                    Color.argb(25, 255, 120, 30),
                    Color.argb(15, 255, 80, 80),
                    Color.argb(0, 255, 150, 100)
                };
                break;
            case 2: // سحابی سبز-آبی
                colors = new int[]{
                    Color.argb(30, 50, 255, 150),
                    Color.argb(20, 80, 200, 255),
                    Color.argb(10, 120, 255, 200),
                    Color.argb(0, 150, 255, 255)
                };
                break;
            case 3: // سحابی بنفش-صورتی
                colors = new int[]{
                    Color.argb(45, 180, 80, 255),
                    Color.argb(30, 220, 100, 200),
                    Color.argb(15, 255, 120, 180),
                    Color.argb(0, 255, 150, 200)
                };
                break;
            case 4: // سحابی طلایی
                colors = new int[]{
                    Color.argb(25, 255, 200, 50),
                    Color.argb(15, 255, 180, 80),
                    Color.argb(10, 255, 220, 100),
                    Color.argb(0, 255, 240, 150)
                };
                break;
            default:
                colors = new int[]{Color.argb(20, 150, 150, 255)};
        }
        
        RadialGradient nebulaGradient = new RadialGradient(
            x, y, size,
            colors,
            null,
            Shader.TileMode.CLAMP
        );
        
        paint.setShader(nebulaGradient);
        
        canvas.save();
        canvas.rotate(rotation, x, y);
        canvas.drawCircle(x, y, size, paint);
        canvas.restore();
        
        paint.setShader(null);
        
        rotation += rotationSpeed;
    }
}

// سیاه‌چاله
class BlackHole {
    private float x, y;
    private float size;
    private float rotation;
    private float rotationSpeed;
    private float pulse;
    private Random random;
    
    public BlackHole(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.random = new Random();
        this.rotation = random.nextFloat() * 360;
        this.rotationSpeed = 0.5f + random.nextFloat() * 1.0f;
        this.pulse = random.nextFloat();
    }
    
    public void update(float deltaTime) {
        rotation += rotationSpeed * deltaTime * 60;
        pulse += deltaTime * 2;
        if (pulse > 1) pulse = 0;
    }
    
    public void draw(Canvas canvas, Paint paint) {
        float pulseSize = size * (1.0f + (float)Math.sin(pulse * Math.PI * 2) * 0.1f);
        
        // حلقه بیرونی
        RadialGradient outerRing = new RadialGradient(
            x, y, pulseSize * 1.5f,
            new int[]{
                Color.argb(100, 100, 50, 200),
                Color.argb(50, 150, 100, 255),
                Color.argb(0, 200, 150, 255)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(outerRing);
        canvas.drawCircle(x, y, pulseSize * 1.5f, paint);
        
        // حلقه داخلی چرخان
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        
        for (int i = 0; i < 3; i++) {
            float ringSize = pulseSize * (0.8f - i * 0.2f);
            int alpha = 150 - i * 50;
            
            paint.setColor(Color.argb(alpha, 150, 100, 255));
            canvas.save();
            canvas.rotate(rotation + i * 120, x, y);
            canvas.drawCircle(x, y, ringSize, paint);
            canvas.restore();
        }
        
        // هسته سیاه‌چاله
        RadialGradient core = new RadialGradient(
            x, y, pulseSize * 0.6f,
            new int[]{
                Color.argb(255, 0, 0, 0),
                Color.argb(200, 50, 0, 100),
                Color.argb(100, 100, 0, 200)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(core);
        canvas.drawCircle(x, y, pulseSize * 0.6f, paint);
        paint.setShader(null);
        
        // نقاط انرژی در حال چرخش
        paint.setColor(Color.argb(200, 200, 150, 255));
        for (int i = 0; i < 8; i++) {
            float angle = rotation + i * 45;
            float distance = pulseSize * 0.8f;
            float pointX = x + (float)Math.cos(Math.toRadians(angle)) * distance;
            float pointY = y + (float)Math.sin(Math.toRadians(angle)) * distance;
            canvas.drawCircle(pointX, pointY, 3, paint);
        }
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getSize() { return size; }
          }
