package com.example.pomodoro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ProgressBar progressBar;
    private TextView timeView;
    private TextView quoteView;
    private Button buttonStart;
    private Button buttonPause;
    private Button buttonResume;

    private ScheduledExecutorService pool;
    private Future<?> progressDisplayFuture;
    private Future<?> quoteDisplayFuture;

    private int currentTime = 0;
    private int maxTime = 0;
    private boolean progressBarRunning = false;
    private boolean quoteRunning = false;
    private boolean sessionRunning = false;
    private boolean isWorkSession = true;

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
    public void initScheduledServices() {
        if (!sessionRunning) {
            maxTime = presenter.toSecond(presenter.getCurrentSessionLength());
            progressBar.setMax(maxTime);
            pool = Executors.newScheduledThreadPool(2);
            sessionRunning = true;
        }
    }

    @Override
    public void startTimer() {
        setButtonVisibilityOnClickStart();
        initScheduledServices();
        if (!progressBarRunning) {
            try {
                progressDisplayFuture = pool.scheduleAtFixedRate(new ProgressDisplayTask(), 0, 1, TimeUnit.SECONDS);
                progressBarRunning = true;
            } catch (Exception e) {
                Log.e("ProgressDisplay", "Progress Display cannot be scheduled!");
            }

        }
        if (!quoteRunning) {
            try {
                quoteDisplayFuture = pool.scheduleAtFixedRate(new QuoteDisplayTask(), 0, 5, TimeUnit.SECONDS);
                quoteRunning = true;
            } catch (Exception e) {
                Log.e("QuoteDisplay", "Quote Display cannot be scheduled!");
            }
        }
    }

    @Override
    public void pauseTimer() {
        setButtonVisibilityOnClickPause();
        cancelProgressDisplay();
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
    public void cancelProgressDisplay() {
        if (progressBarRunning) {
            try {
                progressDisplayFuture.cancel(true);
                progressBarRunning = false;
            } catch (Exception e) {
                Log.e("ProgressDisplay", "Progress Display cannot be cancelled!");
            }
        }
    }

    @Override
    public void cancelQuoteDisplay() {
        if (quoteRunning) {
            try {
                quoteDisplayFuture.cancel(true);
                quoteRunning = false;
            } catch (Exception e) {
                Log.e("QuoteDisplay", "Quote Display cannot be cancelled!");
            }
        }
    }

    @Override
    public void setProgressBarColor() {
        if (isWorkSession)
            progressBar.setProgressDrawable(
                                            ResourcesCompat.getDrawable(
                                                                        getResources(),
                                                                        R.drawable.circular_progress_bar_work,
                                                                        getTheme()));
        else
            progressBar.setProgressDrawable(
                                            ResourcesCompat.getDrawable(
                                                                        getResources(),
                                                                        R.drawable.circular_progress_bar_break,
                                                                        getTheme()));
    }

    @Override
    public void finishSession() {
        sessionRunning = false;
        currentTime = 0;
        isWorkSession = !isWorkSession;
        presenter.switchSession(isWorkSession);

        setButtonVisibilityDefault();
        cancelProgressDisplay();
        cancelQuoteDisplay();
        setProgressBarColor();
        quoteView.setText(R.string.success_message);
    }

    // Update progress bar visual
    private class ProgressDisplayTask implements Runnable {

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (currentTime < maxTime) {
                    currentTime++;
                    progressBar.setProgress(currentTime);
                    int[] arr = presenter.toMinuteSecond(maxTime - currentTime);
                    timeView.setText(String.format("%02d:%02d", arr[0], arr[1]));
                }
                else
                    finishSession();
            });
        }
    }

    // Display random quotes periodically
    private class QuoteDisplayTask implements Runnable {

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (sessionRunning)
                    quoteView.setText(presenter.getRandomQuoteFromMotivator());
            });
        }
    }
}