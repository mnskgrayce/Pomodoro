package com.example.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ProgressBar progressBar;
    private TextView timeView;
    private TextView quoteView;
    private Button buttonStart;
    private Button buttonPause;
    private Button buttonResume;

    private Timer timer;

    private int currentTime = 0;
    private int maxTime = 0;

    private Presenter presenter = new Presenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        buttonStart.setOnClickListener(v -> startTimer());
        buttonPause.setOnClickListener(v -> pauseTimer());
        buttonResume.setOnClickListener(v -> startTimer());
    }


    @Override
    public void initView() {
        progressBar = findViewById(R.id.progressBar);
        timeView = findViewById(R.id.timeView);
        quoteView = findViewById(R.id.quoteView);
        buttonStart = findViewById(R.id.buttonStart);
        buttonPause = findViewById(R.id.buttonPause);
        buttonResume = findViewById(R.id.buttonResume);
        setButtonVisibilityDefault();
    }

    @Override
    public void setButtonVisibilityDefault() {
        buttonStart.setVisibility(View.VISIBLE);
        buttonPause.setVisibility(View.INVISIBLE);
        buttonResume.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setButtonVisibilityOnClickStart() {
        buttonStart.setVisibility(View.INVISIBLE);
        buttonPause.setVisibility(View.VISIBLE);
        buttonResume.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setButtonVisibilityOnClickPause() {
        buttonStart.setVisibility(View.INVISIBLE);
        buttonPause.setVisibility(View.INVISIBLE);
        buttonResume.setVisibility(View.VISIBLE);
    }

    @Override
    public void startTimer() {
        setButtonVisibilityOnClickStart();
        initProgressDisplay();
    }

    @Override
    public void pauseTimer() {
        setButtonVisibilityOnClickPause();
        timer.cancel();
    }

    // ----- HELPERS -----
    // Update progress bar and increment time
    private class ProgressDisplayTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (currentTime < maxTime) {
                    currentTime++;
                    progressBar.setProgress(currentTime);
                    int[] arr = presenter.toMinuteSecond(maxTime - currentTime);
                    timeView.setText(String.format("%02d:%02d", arr[0], arr[1]));
                }
                else {
                    timer.cancel();
                    setButtonVisibilityDefault();
                    currentTime = 0;
                }
            });
        }
    }

    public void initProgressDisplay() {
        maxTime = presenter.toSecond(presenter.getSession().getWorkLength());
        progressBar.setMax(maxTime);
        timer = new Timer();
        timer.scheduleAtFixedRate(new ProgressDisplayTask(), 0, 1000);
        timer.scheduleAtFixedRate(new QuoteDisplayTask(), 1000, 5000);
    }

    // Display random quotes at fixed interval
    private class QuoteDisplayTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (currentTime < maxTime) {
                    quoteView.setText(presenter.getRandomQuoteFromMotivator());
                }
                else {
                    quoteView.setText(R.string.success_message);
                }
            });
        }
    }
}