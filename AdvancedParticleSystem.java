package com.space.ship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private List<Particle> particles;
    private List<Emitter> emitters;
    private Random random;
    
    public ParticleSystem() {
        particles = new ArrayList<>();
        emitters = new ArrayList<>();
        random = new Random();
    }
    
    // انفجار سفینه
    public void createSupernova(float x, float y, int count, int[] colors) {
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * 360;
            float speed = 3 + random.nextFloat() * 12;
            float size = 3 + random.nextFloat() * 8;
            int life = 40 + random.nextInt(50);
            int color = colors[random.nextInt(colors.length)];
            
            particles.add(new AdvancedParticle(
                x, y,
                (float)Math.cos(Math.toRadians(angle)) * speed,
                (float)Math.sin(Math.toRadians(angle)) * speed,
                size,
                color,
                life,
                ParticleType.SUPERNOVA
            ));
        }
        
        // ایجاد امواج شوک
        createShockwave(x, y, 3, 150);
    }
    
    // انفجار سیاره
    public void createPlanetExplosion(float x, float y, int count, int planetType) {
        int[] colors = getPlanetColors(planetType);
        
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * 360;
            float speed = 2 + random.nextFloat() * 10;
            float size = 4 + random.nextFloat() * 10;
            int life = 60 + random.nextInt(80);
            int color = colors[random.nextInt(colors.length)];
            
            particles.add(new AdvancedParticle(
                x, y,
                (float)Math.cos(Math.toRadians(angle)) * speed,
                (float)Math.sin(Math.toRadians(angle)) * speed,
                size,
                color,
                life,
                ParticleType.PLANET_DEBRIS
            ));
        }
        
        // حلقه انفجار
        createExplosionRing(x, y, 5, 200);
    }
    
    // تأثیر برخورد با سیاره
    public void createPlanetImpact(float x, float y, int count, int planetType) {
        int[] colors = getPlanetColors(planetType);
        
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * 360;
            float speed = 1 + random.nextFloat() * 6;
            float size = 2 + random.nextFloat() * 5;
            int life = 20 + random.nextInt(30);
            
            particles.add(new AdvancedParticle(
                x, y,
                (float)Math.cos(Math.toRadians(angle)) * speed,
                (float)Math.sin(Math.toRadians(angle)) * speed,
                size,
                colors[random.nextInt(colors.length)],
                life,
                ParticleType.IMPACT
            ));
        }
    }
    
    // اثر سیاه‌چاله
    public void createBlackHoleEffect(float x, float y, int count) {
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * 360;
            float distance = random.nextFloat() * 100;
            float startX = x + (float)Math.cos(Math.toRadians(angle)) * distance;
            float startY = y + (float)Math.sin(Math.toRadians(angle)) * distance;
            
            particles.add(new BlackHoleParticle(
                startX, startY, x, y,
                2 + random.nextFloat() * 4,
                Color.argb(255, 100, 50, 200),
                60 + random.nextInt(60)
            ));
        }
    }
    
    // اثر احیای سفینه
    public void createRespawnEffect(float x, float y, int count) {
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * 360;
            float speed = 0.5f + random.nextFloat() * 2;
            float size = 2 + random.nextFloat() * 6;
            int life = 30 + random.nextInt(40);
            
            particles.add(new AdvancedParticle(
                x, y,
                (float)Math.cos(Math.toRadians(angle)) * speed,
                (float)Math.sin(Math.toRadians(angle)) * speed,
                size,
                Color.argb(255, 0, 200, 255),
                life,
                ParticleType.ENERGY
            ));
        }
        
        // ایجاد حلقه انرژی
        createEnergyRing(x, y, 4, 120);
    }
    
    // امواج شوک
    private void createShockwave(float x, float y, int count, float maxSize) {
        for (int i = 0; i < count; i++) {
            float size = maxSize * (i + 1) / count;
            int life = 20 + i * 10;
            
            particles.add(new ShockwaveParticle(
                x, y, size,
                Color.argb(150, 255, 100, 0),
                life
            ));
        }
    }
    
    // حلقه انفجار
    private void createExplosionRing(float x, float y, int count, float size) {
        for (int i = 0; i < count; i++) {
            float angle = i * (360f / count);
            float speed = 8 + random.nextFloat() * 4;
            
            particles.add(new RingParticle(
                x, y,
                (float)Math.cos(Math.toRadians(angle)) * speed,
                (float)Math.sin(Math.toRadians(angle)) * speed,
                6,
                Color.argb(255, 255, 200, 0),
                40
            ));
        }
    }
    
    // حلقه انرژی
    private void createEnergyRing(float x, float y, int count, float size) {
        for (int i = 0; i < count; i++) {
            float angle = i * (360f / count);
            
            particles.add(new EnergyRingParticle(
                x, y, angle, size,
                Color.argb(200, 0, 150, 255),
                60
            ));
        }
    }
    
    // رنگ‌های سیارات بر اساس نوع
    private int[] getPlanetColors(int planetType) {
        switch (planetType) {
            case 0: // زمینی
                return new int[]{
                    Color.argb(255, 100, 200, 100),
                    Color.argb(255, 150, 150, 100),
                    Color.argb(255, 80, 120, 80)
                };
            case 1: // آتشی
                return new int[]{
                    Color.argb(255, 255, 100, 0),
                    Color.argb(255, 255, 50, 0),
                    Color.argb(255, 200, 30, 0)
                };
            case 2: // یخی
                return new int[]{
                    Color.argb(255, 200, 230, 255),
                    Color.argb(255, 150, 200, 240),
                    Color.argb(255, 100, 170, 220)
                };
            case 3: // گازی
                return new int[]{
                    Color.argb(255, 255, 200, 100),
                    Color.argb(255, 255, 150, 50),
                    Color.argb(255, 200, 100, 30)
                };
            case 4: // سمی
                return new int[]{
                    Color.argb(255, 100, 255, 100),
                    Color.argb(255, 50, 200, 50),
                    Color.argb(255, 0, 150, 0)
                };
            default:
                return new int[]{Color.WHITE};
        }
    }
    
    public void update(float deltaTime) {
        // بروزرسانی ذرات
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(deltaTime);
            if (p.isDead()) {
                particles.remove(i);
            }
        }
        
        // بروزرسانی emitterها
        for (int i = emitters.size() - 1; i >= 0; i--) {
            Emitter emitter = emitters.get(i);
            emitter.update(deltaTime);
            if (emitter.isFinished()) {
                emitters.remove(i);
            }
        }
    }
    
    public void draw(Canvas canvas, Paint paint) {
        for (Particle p : particles) {
            p.draw(canvas, paint);
        }
    }
    
    public void clear() {
        particles.clear();
        emitters.clear();
    }
}

// انواع ذرات
enum ParticleType {
    SUPERNOVA, PLANET_DEBRIS, IMPACT, ENERGY, SHOCKWAVE, BLACK_HOLE
}

// ذره پیشرفته پایه
class AdvancedParticle extends Particle {
    protected ParticleType type;
    protected float rotation;
    protected float rotationSpeed;
    protected float scale = 1.0f;
    
    public AdvancedParticle(float x, float y, float velocityX, float velocityY, 
                          float size, int color, int life, ParticleType type) {
        super(x, y, velocityX, velocityY, size, color, life);
        this.type = type;
        this.rotation = random.nextFloat() * 360;
        this.rotationSpeed = (random.nextFloat() - 0.5f) * 10;
        
        // تنظیمات خاص بر اساس نوع ذره
        switch (type) {
            case SUPERNOVA:
                this.scale = 1.5f;
                break;
            case PLANET_DEBRIS:
                this.scale = 1.2f;
                break;
            case ENERGY:
                this.rotationSpeed *= 2;
                break;
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        rotation += rotationSpeed * deltaTime * 60;
        
        // اثرات خاص بر اساس نوع
        switch (type) {
            case ENERGY:
                scale = (float)Math.sin(life * 0.1f) * 0.3f + 0.7f;
                break;
            case SUPERNOVA:
                velocityX *= 0.98f;
                velocityY *= 0.98f;
                break;
        }
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        float lifeRatio = (float)life / maxLife;
        int alpha = (int)(255 * lifeRatio);
        
        // گرادیانت بر اساس نوع ذره
        RadialGradient gradient = createGradient(lifeRatio);
        paint.setShader(gradient);
        
        // رسم ذره با چرخش و مقیاس
        canvas.save();
        canvas.rotate(rotation, x, y);
        canvas.drawCircle(x, y, size * scale * lifeRatio, paint);
        canvas.restore();
        
        paint.setShader(null);
        
        // درخشش
        paint.setColor(Color.argb(alpha/3, 255, 255, 255));
        canvas.drawCircle(x, y, size * scale * lifeRatio * 1.5f, paint);
    }
    
    private RadialGradient createGradient(float lifeRatio) {
        int baseColor = this.color;
        int r = Color.red(baseColor);
        int g = Color.green(baseColor);
        int b = Color.blue(baseColor);
        
        int[] colors;
        float[] positions;
        
        switch (type) {
            case SUPERNOVA:
                colors = new int[]{
                    Color.argb((int)(255 * lifeRatio), 255, 255, 200),
                    Color.argb((int)(200 * lifeRatio), r, g, b),
                    Color.argb((int)(100 * lifeRatio), r/2, g/2, b/2)
                };
                positions = new float[]{0.0f, 0.5f, 1.0f};
                break;
                
            case ENERGY:
                colors = new int[]{
                    Color.argb((int)(255 * lifeRatio), 255, 255, 255),
                    Color.argb((int)(180 * lifeRatio), r, g, b),
                    Color.argb(0, r, g, b)
                };
                positions = new float[]{0.0f, 0.3f, 1.0f};
                break;
                
            default:
                colors = new int[]{
                    Color.argb((int)(255 * lifeRatio), r, g, b),
                    Color.argb((int)(100 * lifeRatio), r/2, g/2, b/2)
                };
                positions = new float[]{0.0f, 1.0f};
        }
        
        return new RadialGradient(x, y, size * scale, colors, positions, Shader.TileMode.CLAMP);
    }
}

// ذره موج شوک
class ShockwaveParticle extends Particle {
    private float currentSize;
    private final float maxSize;
    
    public ShockwaveParticle(float x, float y, float maxSize, int color, int life) {
        super(x, y, 0, 0, 1, color, life);
        this.maxSize = maxSize;
        this.currentSize = 1;
    }
    
    @Override
    public void update(float deltaTime) {
        life--;
        currentSize += (maxSize - currentSize) * 0.1f * deltaTime * 60;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        float lifeRatio = (float)life / maxLife;
        int alpha = (int)(150 * lifeRatio);
        
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawCircle(x, y, currentSize, paint);
        
        paint.setStyle(Paint.Style.FILL);
    }
}

// ذره سیاه‌چاله
class BlackHoleParticle extends Particle {
    private final float targetX, targetY;
    private final float attraction;
    
    public BlackHoleParticle(float x, float y, float targetX, float targetY, 
                           float size, int color, int life) {
        super(x, y, 0, 0, size, color, life);
        this.targetX = targetX;
        this.targetY = targetY;
        this.attraction = 0.1f;
    }
    
    @Override
    public void update(float deltaTime) {
        // حرکت به سمت مرکز سیاه‌چاله
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 5) {
            velocityX += (dx / distance) * attraction * deltaTime * 60;
            velocityY += (dy / distance) * attraction * deltaTime * 60;
        }
        
        super.update(deltaTime);
        
        // چرخش
        rotation += 5 * deltaTime * 60;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        float lifeRatio = (float)life / maxLife;
        int alpha = (int)(255 * lifeRatio);
        
        // گرادیانت مارپیچ
        int[] colors = {
            Color.argb(alpha, 100, 50, 200),
            Color.argb(alpha/2, 150, 100, 255),
            Color.argb(0, 200, 150, 255)
        };
        
        RadialGradient gradient = new RadialGradient(
            x, y, size * 2,
            colors,
            null,
            Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        
        canvas.save();
        canvas.rotate(rotation, x, y);
        canvas.drawCircle(x, y, size * lifeRatio, paint);
        canvas.restore();
        
        paint.setShader(null);
    }
}

// ذره حلقه انرژی
class EnergyRingParticle extends Particle {
    private final float startAngle;
    private final float radius;
    private float currentAngle;
    
    public EnergyRingParticle(float x, float y, float startAngle, float radius, int color, int life) {
        super(x, y, 0, 0, 8, color, life);
        this.startAngle = startAngle;
        this.currentAngle = startAngle;
        this.radius = radius;
    }
    
    @Override
    public void update(float deltaTime) {
        life--;
        currentAngle += 3 * deltaTime * 60;
        radius += 2 * deltaTime * 60;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        float lifeRatio = (float)life / maxLife;
        int alpha = (int)(255 * lifeRatio);
        
        float particleX = x + (float)Math.cos(Math.toRadians(currentAngle)) * radius;
        float particleY = y + (float)Math.sin(Math.toRadians(currentAngle)) * radius;
        
        paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawCircle(particleX, particleY, size * lifeRatio, paint);
        
        // درخشش
        paint.setColor(Color.argb(alpha/2, 255, 255, 255));
        canvas.drawCircle(particleX, particleY, size * lifeRatio * 1.5f, paint);
    }
}

// پایه ذره
class Particle {
    protected static Random random = new Random();
    protected float x, y;
    protected float velocityX, velocityY;
    protected float size;
    protected int color;
    protected int life;
    protected int maxLife;
    protected float rotation;
    
    public Particle(float x, float y, float velocityX, float velocityY, float size, int color, int life) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.size = size;
        this.color = color;
        this.life = life;
        this.maxLife = life;
    }
    
    public void update(float deltaTime) {
        x += velocityX * deltaTime * 60;
        y += velocityY * deltaTime * 60;
        velocityX *= 0.99f;
        velocityY *= 0.99f;
        life--;
    }
    
    public void draw(Canvas canvas, Paint paint) {
        float lifeRatio = (float)life / maxLife;
        int alpha = (int)(255 * lifeRatio);
        int particleColor = Color.argb(alpha, 
            Color.red(color), Color.green(color), Color.blue(color));
        
        paint.setColor(particleColor);
        canvas.drawCircle(x, y, size * lifeRatio, paint);
    }
    
    public boolean isDead() {
        return life <= 0;
    }
}

// emitter برای ایجاد ذرات پیوسته
class Emitter {
    private float x, y;
    private int particlesPerSecond;
    private float timer;
    private boolean finished;
    
    public Emitter(float x, float y, int particlesPerSecond) {
        this.x = x;
        this.y = y;
        this.particlesPerSecond = particlesPerSecond;
    }
    
    public void update(float deltaTime) {
        timer += deltaTime;
        if (timer >= 1.0f / particlesPerSecond) {
            createParticle();
            timer = 0;
        }
    }
    
    private void createParticle() {
        // ایجاد ذره جدید
    }
    
    public boolean isFinished() {
        return finished;
    }
              }
