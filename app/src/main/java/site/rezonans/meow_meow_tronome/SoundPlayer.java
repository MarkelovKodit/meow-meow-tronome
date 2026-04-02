package site.rezonans.meow_meow_tronome;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import android.os.Handler;
import android.os.Looper;


public class SoundPlayer {

    private boolean isRunning = false;
    private int bpm;

    private SoundPool soundPool;
    int tickId;
    int tockId;
    private float volume;

    private Handler handler;
    private Runnable tickRunnable;


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
                if (isRunning) {
                    playTick();

                    int intervalMs = 60000 / bpm;
                    handler.postDelayed(this, intervalMs);
                }
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
            handler.post(tickRunnable);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
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
            // Перезапускаем таймер с новым интервалом
            stop();
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
}
