package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class VirtualJoystick {
    private float centerX, centerY;
    private float baseRadius, handleRadius;
    private float handleX, handleY;
    private boolean isActive = false;
    private float deadZone = 0.2f;
    
    // انیمیشن‌ها
    private float activationAnim = 0;
    private float pulseAnim = 0;
    
    public VirtualJoystick(float centerX, float centerY, float baseRadius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.baseRadius = baseRadius;
        this.handleRadius = baseRadius * 0.4f;
        resetHandle();
    }
    
    public void setActive(boolean active, float touchX, float touchY) {
        boolean wasActive = isActive;
        isActive = active;
        
        if (active) {
            float dx = touchX - centerX;
            float dy = touchY - centerY;
            float distance = (float)Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= baseRadius) {
                handleX = touchX;
                handleY = touchY;
            } else {
                handleX = centerX + (dx / distance) * baseRadius;
                handleY = centerY + (dy / distance) * baseRadius;
            }
            
            if (!wasActive) {
                activationAnim = 0;
            }
        } else {
            resetHandle();
        }
    }
    
    private void resetHandle() {
        handleX = centerX;
        handleY = centerY;
    }
    
    public void update(float deltaTime) {
        // انیمیشن فعال‌سازی
        if (isActive && activationAnim < 1.0f) {
            activationAnim += deltaTime * 5;
            if (activationAnim > 1.0f) activationAnim = 1.0f;
        } else if (!isActive && activationAnim > 0) {
            activationAnim -= deltaTime * 3;
            if (activationAnim < 0) activationAnim = 0;
        }
        
        // انیمیشن پالس
        pulseAnim += deltaTime * 2;
        if (pulseAnim > 1) pulseAnim = 0;
    }
    
    public void draw(Canvas canvas, Paint paint) {
        float currentBaseRadius = baseRadius * (0.9f + activationAnim * 0.1f);
        float currentHandleRadius = handleRadius * (0.8f + activationAnim * 0.2f);
        float pulseEffect = (float)Math.sin(pulseAnim * Math.PI * 2) * 0.1f + 0.9f;
        
        // پایه جویستیک با گرادیانت
        RadialGradient baseGradient = new RadialGradient(
            centerX, centerY, currentBaseRadius * pulseEffect,
            new int[]{
                Color.argb(150, 80, 80, 80),
                Color.argb(100, 60, 60, 60),
                Color.argb(50, 40, 40, 40)
            },
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(baseGradient);
        canvas.drawCircle(centerX, centerY, currentBaseRadius * pulseEffect, paint);
        
        // حلقه بیرونی
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(200, 120, 120, 120));
        canvas.drawCircle(centerX, centerY, currentBaseRadius, paint);
        
        // دسته جویستیک
        if (isActive) {
            float handleAlpha = 200 + activationAnim * 55;
            RadialGradient handleGradient = new RadialGradient(
                handleX, handleY, currentHandleRadius,
                new int[]{
                    Color.argb((int)handleAlpha, 220, 220, 220),
                    Color.argb((int)(handleAlpha * 0.7f), 180, 180, 180),
                    Color.argb((int)(handleAlpha * 0.4f), 140, 140, 140)
                },
                null,
                Shader.TileMode.CLAMP
            );
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(handleGradient);
            canvas.drawCircle(handleX, handleY, currentHandleRadius, paint);
            
            // درخشش دسته
            paint.setShader(null);
            paint.setColor(Color.argb(100, 255, 255, 255));
            canvas.drawCircle(handleX, handleY, currentHandleRadius * 1.3f, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(150, 180, 180, 180));
            canvas.drawCircle(handleX, handleY, currentHandleRadius, paint);
        }
        
        // مرکز دسته
        paint.setColor(Color.argb(255, 100, 100, 100));
        canvas.drawCircle(handleX, handleY, currentHandleRadius * 0.5f, paint);
        
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
    }
    
    public float getForceX() {
        if (!isActive) return 0;
        
        float rawForce = (handleX - centerX) / baseRadius;
        
        // اعمال Dead Zone
        if (Math.abs(rawForce) < deadZone) return 0;
        
        // اعمال منحنی پاسخگویی
        return applyResponseCurve(rawForce);
    }
    
    public float getForceY() {
        if (!isActive) return 0;
        
        float rawForce = (handleY - centerY) / baseRadius;
        
        // اعمال Dead Zone
        if (Math.abs(rawForce) < deadZone) return 0;
        
        // اعمال منحنی پاسخگویی
        return applyResponseCurve(rawForce);
    }
    
    private float applyResponseCurve(float rawForce) {
        // منحنی پاسخگویی برای کنترل بهتر
        float absForce = Math.abs(rawForce);
        float direction = rawForce > 0 ? 1 : -1;
        
        if (absForce < 0.5f) {
            // پاسخ خطی برای حرکات کوچک
            return rawForce;
        } else {
            // پاسخ نمایی برای حرکات بزرگ
            float normalized = (absForce - 0.5f) * 2;
            float curved = (float)Math.pow(normalized, 1.5f);
            return direction * (0.5f + curved * 0.5f);
        }
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setDeadZone(float deadZone) {
        this.deadZone = Math.max(0, Math.min(0.5f, deadZone));
    }
    
    public float getDeadZone() {
        return deadZone;
    }
              }
