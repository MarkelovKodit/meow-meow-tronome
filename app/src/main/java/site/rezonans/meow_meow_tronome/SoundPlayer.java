package site.rezonans.meow_meow_tronome;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;


public class SoundPlayer {

    private boolean isRunning = false;
    private int bpm;

    private SoundPool soundPool;
    int tickId;
    int tockId;
    private float volume;

    private Handler handler;
    private Runnable tickRunnable;

    private long lastPlayTime = 0;
    private long totalDelta = 0;
    private int playCount = 0;

    private long nextTickTime = 0;
    private long lastTickTime = 0;

    private long intervalMs = 0;


    //    private MetronomeListener listener;
    public SoundPlayer(Context context) {
        handler = new Handler(Looper.getMainLooper());

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        loadSounds(context);

        tickRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                long now = SystemClock.uptimeMillis();

                if (lastTickTime == 0) {
                    lastTickTime = now;
                } else {
                    long expected = lastTickTime + intervalMs;
                    long drift = now - expected;
                    if (drift > 10) {  // если отстаём больше чем на 10 мс
                        System.out.println("Компенсация: drift = " + drift + " мс");
                        lastTickTime = expected;  // сбрасываем на ожидаемое время
                    } else {
                        lastTickTime = now;
                    }
                }
                if (callback != null) {
                    callback.onTick();
                }

                playTick();

                long nextDelay = intervalMs - (SystemClock.uptimeMillis() - lastTickTime);
                handler.postDelayed(this, Math.max(0, nextDelay));
            }
        };
    }

    private void loadSounds(Context context) {
        tickId = soundPool.load(context, R.raw.tick, 1);

        // Загружаем tock.wav из папки res/raw
        tockId = soundPool.load(context, R.raw.tock, 1);

        // Если нужно отследить момент загрузки (опционально)
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    android.util.Log.d("Metronome", "Sound loaded: " + sampleId);
                }
            }
        });
    }

    private void playTick() {
        long now = System.currentTimeMillis();
        System.out.println("volume в playTick: " + volume);


        if (lastPlayTime != 0) {
            long delta = now - lastPlayTime;
            totalDelta += delta;
            playCount++;

            System.out.println("[" + playCount + "] Интервал: " + delta + " мс");

            if (playCount % 10 == 0) {
                long avg = totalDelta / playCount;
                System.out.println("Средний интервал за " + playCount + " звуков: " + avg + " мс");
            }
        }

        lastPlayTime = now;

        if (soundPool != null && tickId != 0) {
            // play(soundId, leftVolume, rightVolume, priority, loop, rate)
            soundPool.play(tickId, volume, volume, 1, 0, 1.0f);
        }
    }

    private void playTock() {
        if (soundPool != null && tockId != 0) {
            soundPool.play(tockId, volume, volume, 1, 0, 1.0f);
        }
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            intervalMs = 60000 / bpm;  // ← добавить эту строку
            nextTickTime = SystemClock.uptimeMillis();  // ← сброс

            lastTickTime = 0;
            lastPlayTime = 0;
            totalDelta = 0;
            playCount = 0;

            handler.post(tickRunnable);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            lastTickTime = 0;
            lastPlayTime = 0;
            handler.removeCallbacks(tickRunnable);
        }
    }

    public void toggle() {
        if (isRunning) {
            stop();
        } else {
            start();
        }
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
        if (isRunning) {
            stop();
            handler.removeCallbacks(tickRunnable);
            nextTickTime = 0;  // ← сброс
            start();
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    public void release() {
        stop();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public interface TickCallback {
        void onTick();
    }

    private TickCallback callback;

    public void setCallback(TickCallback callback) {
        this.callback = callback;
    }
}
