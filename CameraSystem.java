package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Matrix;

public class CameraSystem {
    private float x, y;
    private float targetX, targetY;
    private float shakeOffsetX, shakeOffsetY;
    private int screenWidth, screenHeight;
    private float zoom = 1.0f;
    private Matrix transformMatrix;
    private Matrix inverseMatrix;
    
    // تنظیمات حرکت نرم دوربین
    private float smoothness = 0.1f;
    private float shakeIntensity = 0;
    private long lastShakeTime = 0;
    
    public CameraSystem(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = screenWidth / 2;
        this.y = screenHeight / 2;
        this.targetX = x;
        this.targetY = y;
        this.transformMatrix = new Matrix();
        this.inverseMatrix = new Matrix();
    }
    
    public void update(SpaceShip ship, float deltaTime) {
        // هدف‌گیری سفینه
        targetX = ship.getX();
        targetY = ship.getY();
        
        // حرکت نرم دوربین به سمت هدف
        x += (targetX - x) * smoothness * deltaTime * 60;
        y += (targetY - y) * smoothness * deltaTime * 60;
        
        // اثر لرزش بر اساس سرعت
        float speed = (float)Math.sqrt(
            ship.getVelocityX() * ship.getVelocityX() + 
            ship.getVelocityY() * ship.getVelocityY()
        );
        
        shakeIntensity = Math.min(5.0f, speed * 0.1f);
        
        // اعمال لرزش
        if (shakeIntensity > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime > 50) {
                shakeOffsetX = (float)(Math.random() - 0.5) * shakeIntensity;
                shakeOffsetY = (float)(Math.random() - 0.5) * shakeIntensity;
                lastShakeTime = currentTime;
            }
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
        
        // تنظیم زوم بر اساس سرعت
        zoom = 1.0f - Math.min(0.3f, speed * 0.005f);
    }
    
    public void applyTransform(Canvas canvas) {
        transformMatrix.reset();
        
        // مرکز صفحه
        float centerX = screenWidth / 2;
        float centerY = screenHeight / 2;
        
        // تبدیل‌ها
        transformMatrix.postTranslate(-x + centerX + shakeOffsetX, -y + centerY + shakeOffsetY);
        transformMatrix.postScale(zoom, zoom, centerX, centerY);
        
        canvas.concat(transformMatrix);
        
        // ذخیره ماتریس معکوس برای تبدیل مختصات
        transformMatrix.invert(inverseMatrix);
    }
    
    public void restoreTransform(Canvas canvas) {
        // بازگردانی تبدیل‌ها
        Matrix restoreMatrix = new Matrix();
        transformMatrix.invert(restoreMatrix);
        canvas.concat(restoreMatrix);
    }
    
    public void follow(float targetX, float targetY, float velocityX, float velocityY) {
        this.targetX = targetX + velocityX * 0.5f;
        this.targetY = targetY + velocityY * 0.5f;
    }
    
    public void shake(float intensity) {
        shakeIntensity = Math.max(shakeIntensity, intensity);
    }
    
    // متدهای دسترسی
    public float getX() { return x + shakeOffsetX; }
    public float getY() { return y + shakeOffsetY; }
    public float getZoom() { return zoom; }
    public Matrix getTransformMatrix() { return transformMatrix; }
    public Matrix getInverseMatrix() { return inverseMatrix; }
}
