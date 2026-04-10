//package site.rezonans.meow_meow_tronome;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//public class MattAnimationSurface implements SurfaceHolder.Callback, Runnable {
//
//    private static final int MAX_FRAMES = 25;
//    private static final int DEFAULT_FRAME_INDEX = 12;
//
//    private SurfaceHolder holder;
//    private Drawable[] frames;
//    private int currentIndex = DEFAULT_FRAME_INDEX;
//    private boolean goingRight = true;
//    private boolean isAnimating = false;
//    private Thread animationThread;
//    private int bpm = 120;
//    private int viewWidth = 0;
//    private int viewHeight = 0;
//
//    public MattAnimationSurface(SurfaceView surfaceView, Context context) {
//        holder = surfaceView.getHolder();
//        holder.addCallback(this);
//        loadFrames(context);
//    }
//
//    private void loadFrames(Context context) {
//        frames = new Drawable[MAX_FRAMES];
//        for (int i = 0; i < MAX_FRAMES; i++) {
//            String name = "matt_frame_" + String.format("%02d", i);
//            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
//            frames[i] = context.getDrawable(resId);
//        }
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        if (isAnimating) {
//            startAnimation();
//        } else {
//            drawCurrentFrame();
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        this.viewWidth = width;
//        this.viewHeight = height;
//        drawCurrentFrame();
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        stopAnimation();
//    }
//
//    public void startAnimation() {
//        if (isAnimating) return;
//        isAnimating = true;
//        currentIndex = 0;
//        goingRight = true;
//        animationThread = new Thread(this);
//        animationThread.start();
//    }
//
//    public void stopAnimation() {
//        isAnimating = false;
//        if (animationThread != null) {
//            animationThread.interrupt();
//            animationThread = null;
//        }
//        resetToDefault();
//    }
//
//    public void setBpm(int bpm) {
//        this.bpm = bpm;
//    }
//
//    public void resetToDefault() {
//        currentIndex = DEFAULT_FRAME_INDEX;
//        goingRight = true;
//        drawCurrentFrame();
//    }
//
//    private void drawCurrentFrame() {
//        if (viewWidth == 0 || viewHeight == 0) return;
//
//        Canvas canvas = holder.lockCanvas();
//        if (canvas != null) {
//            Drawable frame = frames[currentIndex];
//            frame.setBounds(0, 0, viewWidth, viewHeight);
//            frame.draw(canvas);
//            holder.unlockCanvasAndPost(canvas);
//        }
//    }
//
//    @Override
//    public void run() {
//        long delayMs = (60000 / bpm) / 25;
//
//        while (isAnimating) {
//            long frameStart = System.currentTimeMillis();
//
//            // Обновляем индекс кадра
//            if (goingRight) {
//                if (currentIndex < MAX_FRAMES - 1) {
//                    currentIndex++;
//                } else {
//                    goingRight = false;
//                }
//            } else {
//                if (currentIndex > 0) {
//                    currentIndex--;
//                } else {
//                    goingRight = true;
//                }
//            }
//
//            // Рисуем кадр
//            drawCurrentFrame();
//
//            // Компенсация времени
//            long workTime = System.currentTimeMillis() - frameStart;
//            long sleepTime = delayMs - workTime;
//            if (sleepTime > 0) {
//                try {
//                    Thread.sleep(sleepTime);
//                } catch (InterruptedException e) {
//                    break;
//                }
//            }
//        }
//    }
//}