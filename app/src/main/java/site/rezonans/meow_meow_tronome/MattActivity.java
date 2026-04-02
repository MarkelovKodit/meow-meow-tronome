package site.rezonans.meow_meow_tronome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MattActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int BPM_MIN = 60;
    private static final int BPM_MAX = 200;
    private static final int BPM_DEFAULT = 120;
    private static final float DEFAULT_VOLUME = 0.8f;
    private int bpm;

    private TextView mTempoView;

    private ToggleButton mPlayButton;
    private SeekBar mTempoChanger;
    private SeekBar mVolumeChanger;
    private Button mTempoPlusOne;
    private Button mTempoMinusOne;

    private SoundPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = new SoundPlayer(this);
        player.setBpm(BPM_DEFAULT);
        player.setVolume(DEFAULT_VOLUME);

        bpm = BPM_DEFAULT;

        mTempoView = findViewById(R.id.tempo_view);
        mTempoView.setText(String.valueOf(bpm));

        mPlayButton = findViewById(R.id.playButton);
        mPlayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("Метроном включен (PLAY)");
                } else {
                    System.out.println("Метроном выключен (STOP)");
                }
                player.toggle();
            }
        });

        mTempoChanger = findViewById(R.id.tempoChanger);
        mTempoChanger.setMax(BPM_MAX - BPM_MIN);
        mTempoChanger.setProgress(BPM_DEFAULT - BPM_MIN);

        mTempoChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bpm = progressToBpm(progress);
                mTempoView.setText(String.valueOf(bpm));
                setBpm(bpm);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // пользователь начал трогать ползунок
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // пользователь отпустил ползунок
            }
        });

        mVolumeChanger = findViewById(R.id.volumeChanger);
        mVolumeChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                player.setVolume(progress/100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTempoPlusOne = findViewById(R.id.button_plus);
        mTempoPlusOne.setOnClickListener(listener -> {
            int newBpm = bpm + 1;
            if (newBpm <= BPM_MAX) {
                setBpm(newBpm);
            }
        });

        mTempoMinusOne = findViewById(R.id.button_minus);

        mTempoMinusOne.setOnClickListener(listener -> {
            int newBpm = bpm - 1;
            if (newBpm >= BPM_MIN) {
                setBpm(newBpm);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    private boolean isBpmValid(int bpm) {
        return bpm >= BPM_MIN && bpm <= BPM_MAX;
    }


    private void setBpm(int bpm) {
        mTempoChanger.setProgress(bpmToProgress(bpm));
        player.setBpm(bpm);
        //после передаем изменения в анимацию
    }

    private int progressToBpm(int progress) {
        return progress + BPM_MIN;
    }

    // Преобразование BPM в прогресс
    private int bpmToProgress(int bpm) {
        return bpm - BPM_MIN;
    }
}