package site.rezonans.meow_meow_tronome;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

public class MattAnimation {

    private static final int DEFAULT_FRAME_INDEX = 12;
    private static final int MAX_FRAMES = 25;

    private Handler handler = new Handler(Looper.getMainLooper());

    private ImageView imageView;
    private Drawable[] frames;
    private int currentIndex = DEFAULT_FRAME_INDEX;
    private boolean goingRight = true;
    private Runnable animationRunnable;

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    private int bpm;

    // Конструктор
    public MattAnimation(Context context, ImageView imageView) {

        this.imageView = imageView;
        loadFrames(context);
        showCurrentFrame();
    }

    // Загрузка кадров из ресурсов (frame_00.png ... frame_24.png)
    private void loadFrames(Context context) {
        frames = new Drawable[MAX_FRAMES];
        for (int i = 0; i < MAX_FRAMES; i++) {
            String name = "matt_frame_" + String.format("%02d", i);
            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            frames[i] = context.getDrawable(resId);
        }
    }

    public void startAnimation() {
        int delayMs = (60000 / bpm) / (MAX_FRAMES-1);
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

    public void stopAnimation() {
        if (animationRunnable != null) {
            handler.removeCallbacks(animationRunnable);
        }
        resetToDefault();
    }

    private void showCurrentFrame() {
        imageView.setImageDrawable(frames[currentIndex]);
    }


    public void nextFrame() {
        if (goingRight == true) {
            if (currentIndex < MAX_FRAMES - 1) {
                currentIndex++;
                showCurrentFrame();
            }
        } else { // goingRight == LEFT
            if (currentIndex > 0) {
                currentIndex--;
                showCurrentFrame();
            }
        }
    }

    public void resetToDefault() {
        currentIndex = DEFAULT_FRAME_INDEX;
        goingRight = true;
        showCurrentFrame();
    }
}