package site.rezonans.meow_meow_tronome;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MattAnimation {

    private static final int DEFAULT_FRAME_INDEX = 12;
    private static final int MAX_FRAMES = 25;

    private Handler handler = new Handler(Looper.getMainLooper());
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> animationTask;

    private ImageView imageView;
    private Drawable[] frames;
    private int currentIndex = DEFAULT_FRAME_INDEX;
    private boolean goingRight = true;
    private Runnable animationRunnable;
    private boolean isAnimating = false;
    private int bpm;

    private long nextFrameTime = 0;
    private long delayMs;


    /////

    private long lastFrameCall = 0;
    private int frameCount = 0;
    private long startTime = 0;

//    private SurfaceHolder holder;
//    private Thread animationThread;
//    private int viewWidth = 0;
//    private int viewHeight = 0;

//    private ValueAnimator animator = new ValueAnimator();

    public MattAnimation(Context context, ImageView imageView) {
        this.imageView = imageView;
        loadFrames(context);
        showCurrentFrame();
    }

    private void loadFrames(Context context) {
        frames = new Drawable[MAX_FRAMES];
        for (int i = 0; i < MAX_FRAMES; i++) {
            String name = "matt_frame_" + String.format("%02d", i);
            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            frames[i] = context.getDrawable(resId);
        }
    }

    public void startAnimation() {
        if (isAnimating) return;
        isAnimating = true;
        long delayMs = (long) ((60000.0 / bpm) / (MAX_FRAMES));
        System.out.print("временной интервал - " + delayMs);
        currentIndex = 0;
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                nextFrame();
                handler.postDelayed(this, delayMs);
            }
        };
        handler.post(animationRunnable);
    }

//    public void startAnimation() {
//        if (animator != null && animator.isRunning()) {
//            animator.cancel();
//        }
//
//        int duration = 60000 / bpm;  // полуцикл
//        animator = ValueAnimator.ofInt(0, MAX_FRAMES - 1);
//        animator.setDuration(duration);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.addUpdateListener(animation -> {
//            currentIndex = (int) animation.getAnimatedValue();
//            showCurrentFrame();
//        });
//        animator.start();
//    }

//    public void startAnimation() {
//        if (isAnimating) return;
//        isAnimating = true;
//        currentIndex = 0;
//        delayMs = (60000 / bpm) / MAX_FRAMES;  // ваша формула
//        System.out.print("временной интервал - " + delayMs);
//
//        nextFrameTime = SystemClock.uptimeMillis() + delayMs;
//        handler.post(frameRunnable);
//    }
//
//    private Runnable frameRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (!isAnimating) return;
//
//            long now = SystemClock.uptimeMillis();
//            long wait = nextFrameTime - now;
//
//            if (wait <= 0) {
//                nextFrame();
//                nextFrameTime += delayMs;
//                wait = nextFrameTime - SystemClock.uptimeMillis();
//            }
//
//            handler.postDelayed(this, Math.max(0, wait));
//        }
//    };

//    public void startAnimation() {
//        currentIndex = 0;
//        if (isAnimating) return;
//        isAnimating = true;
//
//        long delayMs = (60000 / bpm) / 12;
//
//        scheduler = Executors.newSingleThreadScheduledExecutor();
//        animationTask = scheduler.scheduleAtFixedRate(() -> {
//            // Этот код выполняется в фоновом потоке
//            // Нельзя трогать UI напрямую!
//
//            // Переключаем кадр в UI-потоке
//            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
//            mainHandler.post(() -> {
//                nextFrame();  // здесь showCurrentFrame()
//            });
//
//        }, 0, delayMs, TimeUnit.MILLISECONDS);
//    }

    public void stopAnimation() {
        if (!isAnimating) return;
        isAnimating = false;
        if (animationRunnable != null) {
            handler.removeCallbacks(animationRunnable);
        }
        resetToDefault();
    }

//    public void stopAnimation() {
//        if (!isAnimating) return;
//        isAnimating = false;
//
//        if (animationTask != null) {
//            animationTask.cancel(false);
//        }
//        if (scheduler != null) {
//            scheduler.shutdown();
//        }
//        resetToDefault();
//    }

    private void showCurrentFrame() {
        imageView.setImageDrawable(frames[currentIndex]);
    }


    public void nextFrame() {
        long now = System.currentTimeMillis();

        if (lastFrameCall != 0) {
            long delta = now - lastFrameCall;
            System.out.println("[" + frameCount + "] Интервал: " + delta + " мс");

            // Каждые 25 кадров показываем средний интервал
            if (frameCount % 25 == 0 && frameCount > 0) {
                long avg = (now - startTime) / 25;
                System.out.println("Средний интервал за 25 кадров: " + avg + " мс");
                startTime = now;
            }
        } else {
            startTime = now;
        }

        lastFrameCall = now;
        frameCount++;

        if (goingRight) {
            if (currentIndex < MAX_FRAMES - 1) {
                currentIndex++;
            } else {
                goingRight = false;   // дошли до правого края, замираем
//                currentIndex--;
            }
        } else {
            if (currentIndex > 0) {
                currentIndex--;
            } else {
                goingRight = true;    // дошли до левого края, замираем
//                currentIndex++;
            }
        }
//        long start = System.nanoTime();
        showCurrentFrame();
//        long renderTime = (System.nanoTime() - start) / 1000;
//        System.out.println("Отрисовка: " + renderTime + " мкс");
    }

//    public void nextFrame() {
//        if (goingRight) {
//            if (currentIndex < MAX_FRAMES - 2) {  // не доходим до последнего
//                currentIndex += 2;  // шаг 2
//            } else {
//                goingRight = false;
//                currentIndex -= 2;  // сразу обратно
//            }
//        } else {
//            if (currentIndex > 1) {
//                currentIndex -= 2;
//            } else {
//                goingRight = true;
//                currentIndex += 2;
//            }
//        }
//        showCurrentFrame();
//    }

    public void resetToDefault() {
        currentIndex = DEFAULT_FRAME_INDEX;
        goingRight = true;
        showCurrentFrame();
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
        if (isAnimating) {
            stopAnimation();
            startAnimation();  // перезапускаем с новым delayMs
        }
//        if (animator != null && animator.isRunning()) {
//            startAnimation();
//        }
//        animator.setDuration(60000 / bpm);

    }

    public void syncFrame() {
        // синхронизация кадра с учётом текущего направления
        if (goingRight) {
            currentIndex = MAX_FRAMES - 1;  // правый край
        } else {
            currentIndex = 0;               // левый край
        }
        showCurrentFrame();
    }
}