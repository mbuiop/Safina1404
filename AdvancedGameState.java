package com.space.ship.game;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static final String PREFS_NAME = "SpaceShipGameAdvanced";
    
    // آمار بازی
    private long coins;
    private int score;
    private int currentLevel;
    private int destroyedPlanets;
    private int destroyedEnemies;
    private int lives;
    private int totalPlayTime;
    private long sessionStartTime;
    
    // آمار پیشرفته
    private int totalShotsFired;
    private int totalHits;
    private int maxCombo;
    private int currentCombo;
    private long totalDistanceTraveled;
    private int planetsByType[];
    private int enemiesByType[];
    
    // قدرت‌آپگریدها
    private int shipSpeedLevel;
    private int shipHealthLevel;
    private int weaponPowerLevel;
    private int shieldCapacityLevel;
    
    // دستاوردها
    private List<Achievement> achievements;
    private List<Mission> activeMissions;
    
    public GameState() {
        coins = 1000000; // شروع با 1 میلیون سکه
        score = 0;
        currentLevel = 1;
        destroyedPlanets = 0;
        destroyedEnemies = 0;
        lives = 3;
        totalPlayTime = 0;
        sessionStartTime = System.currentTimeMillis();
        
        // آمار پیشرفته
        totalShotsFired = 0;
        totalHits = 0;
        maxCombo = 0;
        currentCombo = 0;
        totalDistanceTraveled = 0;
        planetsByType = new int[5];
        enemiesByType = new int[4];
        
        // قدرت‌آپگریدها
        shipSpeedLevel = 1;
        shipHealthLevel = 1;
        weaponPowerLevel = 1;
        shieldCapacityLevel = 1;
        
        // دستاوردها و مأموریت‌ها
        achievements = new ArrayList<>();
        activeMissions = new ArrayList<>();
        initializeAchievements();
        generateMissions();
    }
    
    private void initializeAchievements() {
        achievements.add(new Achievement("اولین قدم", "اولین سیاره را نابود کن", 1000, "planet_1"));
        achievements.add(new Achievement("شکارچی", "10 دشمن نابود کن", 2500, "enemy_10"));
        achievements.add(new Achievement("ثروتمند", "10 میلیون سکه جمع کن", 5000, "coins_10m"));
        achievements.add(new Achievement("قهرمان", "به سطح 10 برس", 10000, "level_10"));
        achievements.add(new Achievement("اسنایپر", "دقت 80% داشته باش", 7500, "accuracy_80"));
    }
    
    private void generateMissions() {
        activeMissions.add(new Mission("نابودی سیارات", "5 سیاره نابود کن", 5, 0, 2000, "planets_5"));
        activeMissions.add(new Mission("شکار دشمنان", "8 دشمن نابود کن", 8, 0, 1500, "enemies_8"));
        activeMissions.add(new Mission("جمع‌آوری ثروت", "500,000 سکه جمع کن", 500000, 0, 3000, "coins_500k"));
    }
    
    public void planetDestroyed(int planetType) {
        destroyedPlanets++;
        planetsByType[planetType]++;
        
        int baseScore = 100 * currentLevel;
        int typeBonus = getPlanetTypeBonus(planetType);
        int comboBonus = currentCombo * 10;
        
        score += baseScore + typeBonus + comboBonus;
        coins += (50000 * currentLevel) + (comboBonus * 100);
        
        currentCombo++;
        if (currentCombo > maxCombo) {
            maxCombo = currentCombo;
        }
        
        // بررسی مأموریت‌ها
        checkMissions();
        
        // بررسی دستاوردها
        checkAchievements();
    }
    
    public void enemyDestroyed(int enemyType) {
        destroyedEnemies++;
        enemiesByType[enemyType]++;
        
        int baseScore = 50 * currentLevel;
        int typeBonus = getEnemyTypeBonus(enemyType);
        
        score += baseScore + typeBonus;
        coins += 25000 * currentLevel;
        
        currentCombo++;
        if (currentCombo > maxCombo) {
            maxCombo = currentCombo;
        }
        
        checkMissions();
        checkAchievements();
    }
    
    public void shipDestroyed() {
        lives--;
        currentCombo = 0;
        
        score = Math.max(0, score - 100);
        coins = Math.max(1000000, coins - 100000);
    }
    
    public void nextLevel() {
        currentLevel++;
        destroyedPlanets = 0;
        currentCombo = 0;
        
        // پاداش سطح
        int levelBonus = currentLevel * 1000000;
        coins += levelBonus;
        score += currentLevel * 1000;
        
        // پاداش جان اضافی هر 5 سطح
        if (currentLevel % 5 == 0) {
            lives++;
        }
        
        // تولید مأموریت‌های جدید
        generateMissions();
        
        checkAchievements();
    }
    
    public void addDistance(float distance) {
        totalDistanceTraveled += (long)distance;
    }
    
    public void shotFired() {
        totalShotsFired++;
    }
    
    public void shotHit() {
        totalHits++;
    }
    
    private int getPlanetTypeBonus(int planetType) {
        switch (planetType) {
            case 0: return 50;   // زمینی
            case 1: return 100;  // آتشی
            case 2: return 75;   // یخی
            case 3: return 150;  // گازی
            case 4: return 125;  // سمی
            default: return 0;
        }
    }
    
    private int getEnemyTypeBonus(int enemyType) {
        switch (enemyType) {
            case 0: return 25;   // Scout
            case 1: return 50;   // Fighter
            case 2: return 100;  // Bomber
            case 3: return 150;  // Elite
            default: return 0;
        }
    }
    
    private void checkMissions() {
        for (int i = activeMissions.size() - 1; i >= 0; i--) {
            Mission mission = activeMissions.get(i);
            if (mission.checkCompletion(this)) {
                coins += mission.getReward();
                score += mission.getReward() / 10;
                activeMissions.remove(i);
            }
        }
    }
    
    private void checkAchievements() {
        for (Achievement achievement : achievements) {
            if (!achievement.isUnlocked() && achievement.checkCondition(this)) {
                achievement.unlock();
                coins += achievement.getReward();
                score += achievement.getReward() * 2;
            }
        }
    }
    
    // سیستم قدرت‌آپگرید
    public boolean upgradeShipSpeed() {
        int cost = getUpgradeCost(shipSpeedLevel);
        if (coins >= cost) {
            coins -= cost;
            shipSpeedLevel++;
            return true;
        }
        return false;
    }
    
    public boolean upgradeShipHealth() {
        int cost = getUpgradeCost(shipHealthLevel);
        if (coins >= cost) {
            coins -= cost;
            shipHealthLevel++;
            return true;
        }
        return false;
    }
    
    public boolean upgradeWeaponPower() {
        int cost = getUpgradeCost(weaponPowerLevel);
        if (coins >= cost) {
            coins -= cost;
            weaponPowerLevel++;
            return true;
        }
        return false;
    }
    
    public boolean upgradeShieldCapacity() {
        int cost = getUpgradeCost(shieldCapacityLevel);
        if (coins >= cost) {
            coins -= cost;
            shieldCapacityLevel++;
            return true;
        }
        return false;
    }
    
    private int getUpgradeCost(int currentLevel) {
        return currentLevel * 500000;
    }
    
    // محاسبه دقت
    public float getAccuracy() {
        if (totalShotsFired == 0) return 0;
        return (float) totalHits / totalShotsFired * 100;
    }
    
    // ذخیره و بازیابی
    public void saveGame(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putLong("coins", coins);
        editor.putInt("score", score);
        editor.putInt("level", currentLevel);
        editor.putInt("lives", lives);
        editor.putInt("totalPlayTime", totalPlayTime + (int)(System.currentTimeMillis() - sessionStartTime) / 1000);
        
        // آمار پیشرفته
        editor.putInt("totalShots", totalShotsFired);
        editor.putInt("totalHits", totalHits);
        editor.putInt("maxCombo", maxCombo);
        editor.putLong("totalDistance", totalDistanceTraveled);
        
        // قدرت‌آپگریدها
        editor.putInt("speedLevel", shipSpeedLevel);
        editor.putInt("healthLevel", shipHealthLevel);
        editor.putInt("weaponLevel", weaponPowerLevel);
        editor.putInt("shieldLevel", shieldCapacityLevel);
        
        editor.apply();
    }
    
    public void loadGame(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        coins = prefs.getLong("coins", 1000000);
        score = prefs.getInt("score", 0);
        currentLevel = prefs.getInt("level", 1);
        lives = prefs.getInt("lives", 3);
        totalPlayTime = prefs.getInt("totalPlayTime", 0);
        
        // آمار پیشرفته
        totalShotsFired = prefs.getInt("totalShots", 0);
        totalHits = prefs.getInt("totalHits", 0);
        maxCombo = prefs.getInt("maxCombo", 0);
        totalDistanceTraveled = prefs.getLong("totalDistance", 0);
        
        // قدرت‌آپگریدها
        shipSpeedLevel = prefs.getInt("speedLevel", 1);
        shipHealthLevel = prefs.getInt("healthLevel", 1);
        weaponPowerLevel = prefs.getInt("weaponLevel", 1);
        shieldCapacityLevel = prefs.getInt("shieldLevel", 1);
        
        sessionStartTime = System.currentTimeMillis();
    }
    
    // کلاس‌های داخلی برای دستاوردها و مأموریت‌ها
    class Achievement {
        private String name;
        private String description;
        private int reward;
        private String condition;
        private boolean unlocked;
        
        public Achievement(String name, String description, int reward, String condition) {
            this.name = name;
            this.description = description;
            this.reward = reward;
            this.condition = condition;
            this.unlocked = false;
        }
        
        public boolean checkCondition(GameState gameState) {
            switch (condition) {
                case "planet_1":
                    return gameState.destroyedPlanets >= 1;
                case "enemy_10":
                    return gameState.destroyedEnemies >= 10;
                case "coins_10m":
                    return gameState.coins >= 10000000;
                case "level_10":
                    return gameState.currentLevel >= 10;
                case "accuracy_80":
                    return gameState.getAccuracy() >= 80;
                default:
                    return false;
            }
        }
        
        public void unlock() {
            unlocked = true;
        }
        
        // Getter methods
        public boolean isUnlocked() { return unlocked; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getReward() { return reward; }
    }
    
    class Mission {
        private String name;
        private String description;
        private int target;
        private int progress;
        private int reward;
        private String type;
        
        public Mission(String name, String description, int target, int progress, int reward, String type) {
            this.name = name;
            this.description = description;
            this.target = target;
            this.progress = progress;
            this.reward = reward;
            this.type = type;
        }
        
        public boolean checkCompletion(GameState gameState) {
            switch (type) {
                case "planets_5":
                    progress = gameState.destroyedPlanets;
                    break;
                case "enemies_8":
                    progress = gameState.destroyedEnemies;
                    break;
                case "coins_500k":
                    progress = (int) Math.min(gameState.coins, Integer.MAX_VALUE);
                    break;
            }
            return progress >= target;
        }
        
        // Getter methods
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getProgress() { return progress; }
        public int getTarget() { return target; }
        public int getReward() { return reward; }
    }
    
    // Getter methods
    public long getCoins() { return coins; }
    public int getScore() { return score; }
    public int getCurrentLevel() { return currentLevel; }
    public int getLives() { return lives; }
    public int getDestroyedPlanets() { return destroyedPlanets; }
    public int getDestroyedEnemies() { return destroyedEnemies; }
    public int getShipSpeedLevel() { return shipSpeedLevel; }
    public int getShipHealthLevel() { return shipHealthLevel; }
    public int getWeaponPowerLevel() { return weaponPowerLevel; }
    public int getShieldCapacityLevel() { return shieldCapacityLevel; }
    public float getAccuracy() { 
        return totalShotsFired > 0 ? (float)totalHits / totalShotsFired * 100 : 0; 
    }
    public int getMaxCombo() { return maxCombo; }
    public int getCurrentCombo() { return currentCombo; }
    public List<Achievement> getAchievements() { return achievements; }
    public List<Mission> getActiveMissions() { return activeMissions; }
      }
