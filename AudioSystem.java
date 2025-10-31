package com.space.ship.game;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.SparseIntArray;
import java.util.Random;

public class AudioSystem {
    private Context context;
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private MediaPlayer backgroundMusic;
    private Random random;
    
    // شناسه صداها
    private static final int SOUND_ENGINE = 1;
    private static final int SOUND_EXPLOSION = 2;
    private static final int SOUND_IMPACT = 3;
    private static final int SOUND_SHIELD = 4;
    private static final int SOUND_LEVEL_START = 5;
    private static final int SOUND_LEVEL_COMPLETE = 6;
    private static final int SOUND_GAME_OVER = 7;
    private static final int SOUND_BLACK_HOLE = 8;
    private static final int SOUND_PLANET_EXPLOSION = 9;
    private static final int SOUND_RESPAWN = 10;
    
    private int engineSoundId = -1;
    private float engineVolume = 0;
    
    public AudioSystem(Context context) {
        this.context = context;
        this.soundMap = new SparseIntArray();
        this.random = new Random();
        initialize();
    }
    
    private void initialize() {
        // ایجاد SoundPool با تنظیمات پیشرفته
        soundPool = new SoundPool.Builder()
                .setMaxStreams(20)
                .build();
        
        // بارگذاری صداها
        loadSounds();
        
        // راه‌اندازی موسیقی پس‌زمینه
        setupBackgroundMusic();
    }
    
    private void loadSounds() {
        // در اینجا باید فایل‌های صوتی واقعی بارگذاری شوند
        // برای نمونه از صداهای سیستمی استفاده می‌کنیم
        
        // soundMap.put(SOUND_ENGINE, soundPool.load(context, R.raw.engine, 1));
        // soundMap.put(SOUND_EXPLOSION, soundPool.load(context, R.raw.explosion, 1));
        // و بقیه صداها...
    }
    
    private void setupBackgroundMusic() {
        try {
            // backgroundMusic = MediaPlayer.create(context, R.raw.space_music);
            if (backgroundMusic != null) {
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.3f, 0.3f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void update(SpaceShip ship, float deltaTime) {
        updateEngineSound(ship, deltaTime);
    }
    
    private void updateEngineSound(SpaceShip ship, float deltaTime) {
        float targetVolume = calculateEngineVolume(ship);
        float volumeChange = deltaTime * 2.0f;
        
        if (targetVolume > engineVolume) {
            engineVolume = Math.min(targetVolume, engineVolume + volumeChange);
        } else {
            engineVolume = Math.max(targetVolume, engineVolume - volumeChange);
        }
        
        if (engineSoundId == -1 && engineVolume > 0.1f) {
            // engineSoundId = soundPool.play(soundMap.get(SOUND_ENGINE), 
            //         engineVolume, engineVolume, 1, -1, 1.0f);
        } else if (engineSoundId != -1) {
            soundPool.setVolume(engineSoundId, engineVolume, engineVolume);
            
            if (engineVolume < 0.1f) {
                soundPool.stop(engineSoundId);
                engineSoundId = -1;
            }
        }
    }
    
    private float calculateEngineVolume(SpaceShip ship) {
        float speed = (float)Math.sqrt(
            ship.getVelocityX() * ship.getVelocityX() + 
            ship.getVelocityY() * ship.getVelocityY()
        );
        
        float baseVolume = speed / 15.0f;
        float engineGlow = 0.5f; // این مقدار باید از سفینه گرفته شود
        
        return Math.min(1.0f, baseVolume + engineGlow * 0.3f);
    }
    
    // متدهای پخش صدا
    public void playExplosion() {
        playSound(SOUND_EXPLOSION, 1.0f, 1.0f);
    }
    
    public void playImpact() {
        playSound(SOUND_IMPACT, 0.7f, 0.8f + random.nextFloat() * 0.4f);
    }
    
    public void playShield() {
        playSound(SOUND_SHIELD, 0.8f, 1.0f);
    }
    
    public void playLevelStart() {
        playSound(SOUND_LEVEL_START, 1.0f, 1.0f);
    }
    
    public void playLevelComplete() {
        playSound(SOUND_LEVEL_COMPLETE, 1.0f, 1.0f);
    }
    
    public void playGameOver() {
        playSound(SOUND_GAME_OVER, 1.0f, 1.0f);
    }
    
    public void playBlackHole() {
        playSound(SOUND_BLACK_HOLE, 0.9f, 0.7f + random.nextFloat() * 0.6f);
    }
    
    public void playPlanetExplosion() {
        playSound(SOUND_PLANET_EXPLOSION, 1.0f, 0.9f + random.nextFloat() * 0.2f);
    }
    
    public void playRespawn() {
        playSound(SOUND_RESPAWN, 0.8f, 1.0f);
    }
    
    private void playSound(int soundId, float volume, float rate) {
        int soundResource = soundMap.get(soundId);
        if (soundResource != 0) {
            soundPool.play(soundResource, volume, volume, 1, 0, rate);
        }
    }
    
    public void startBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }
    
    public void pauseAll() {
        if (soundPool != null) {
            soundPool.autoPause();
        }
        stopBackgroundMusic();
    }
    
    public void resumeAll() {
        if (soundPool != null) {
            soundPool.autoResume();
        }
        startBackgroundMusic();
    }
    
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}
