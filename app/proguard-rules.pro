# قوانین ProGuard برای SpaceShip Game

# نگهداری کلاس‌های اصلی بازی
-keep class com.space.ship.game.** { *; }
-keep class android.games.** { *; }

# نگهداری متدهای مورد نیاز
-keepclassmembers class * {
    public void onDraw(android.graphics.Canvas);
    public boolean onTouchEvent(android.view.MotionEvent);
    public void surfaceCreated(android.view.SurfaceHolder);
    public void surfaceChanged(android.view.SurfaceHolder, int, int, int);
    public void surfaceDestroyed(android.view.SurfaceHolder);
    public void run();
}

# نگهداری سازنده‌ها
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# نگهداری رابط‌های اندروید
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application

# نگهداری برای انعکاس
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes *Annotation*

# نگهداری منابع
-keepclassmembers class **.R$* {
    public static <fields>;
}

# کتابخانه‌های خاص
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-keep class androidx.** { *; }

# برای دیباگ
-keepattributes SourceFile,LineNumberTable
-dontobfuscate

# بهینه‌سازی
-optimizations !code/simplification/cast
-optimizationpasses 5
-allowaccessmodification
