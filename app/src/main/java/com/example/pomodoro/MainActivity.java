package com.example.pomodoro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pomodoro.editquotes.QuotesEditActivity;
import com.example.pomodoro.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.pomodoro.settings.SettingsActivity.OPTION_NO_QUOTE;
import static com.example.pomodoro.settings.SettingsActivity.OPTION_QUOTE;
import static com.example.pomodoro.settings.SettingsActivity.OPTION_RESUME;
import static com.example.pomodoro.settings.SettingsActivity.OPTION_START_NEW;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ProgressBar progressBar;
    private ImageView progressBarBG;
    private ImageButton buttonToggleTimer;
    private TextView timeView;
    private TextView quoteView;

    private ScheduledExecutorService pool;
    private Future<?> progressDisplayFuture;
    private Future<?> quoteDisplayFuture;

    private int currentTime = 0;
    private int maxTime = 0;
    private boolean progressBarRunning = false;
    private boolean quoteRunning = false;
    private boolean sessionRunning = false;
    private boolean isWorkSession = true;
    private int quoteDisplayOption = OPTION_QUOTE;

    private final MainPresenter mainPresenter = new MainPresenter();

    public static final int CODE_TO_SETTINGS = 100;
    public static final int CODE_TO_QUOTES_EDIT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    // ----- LIFE CYCLES HANDLING ----- //
    @Override
    public void onPause() {
        Toast.makeText(this, "Timer is still active!", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Goodbye!", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    // Keep timer running when back button pressed
    @Override
    public void onBackPressed() {
        // God bless StackOverflow
        Log.d("Main", "onBackPressed is called!");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    // ----- CONTRACT IMPLEMENTATION ----- //
    // Set up view components
    @Override
    public void initView() {
        progressBar = findViewById(R.id.progressBar);
        progressBarBG = findViewById(R.id.progressBarBG);
        timeView = findViewById(R.id.timeView);
        quoteView = findViewById(R.id.quoteView);

        buttonToggleTimer = findViewById(R.id.buttonToggleTimer);
        ImageButton buttonSkip = findViewById(R.id.buttonSkip);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonQuotes = findViewById(R.id.buttonQuotes);

        buttonToggleTimer.setOnClickListener(v -> toggleTimer());
        buttonSkip.setOnClickListener(v -> switchSession());
        buttonSettings.setOnClickListener(v -> goToSettings());
        buttonQuotes.setOnClickListener(v -> goToQuotesEdit());
    }

    // Set up executor services
    @Override
    public void initScheduledServices() {
        if (!sessionRunning) {
            updateMaxTime(isWorkSession);
            progressBar.setMax(maxTime);
            pool = Executors.newScheduledThreadPool(2);
            sessionRunning = true;
        }
    }

    @Override
    public void toggleTimer() {
        if (!buttonToggleTimer.isActivated()) {
            initScheduledServices();
            runProgressDisplay();
            runQuoteDisplay();
        }
        else {
            cancelProgressDisplay();
        }
        // To use image button as ToggleButton
        buttonToggleTimer.setActivated(!buttonToggleTimer.isActivated());
    }

    @Override
    public void pauseTimer() {
        // NOT toggleTimer()
        cancelProgressDisplay();
        buttonToggleTimer.setActivated(false);
        Toast.makeText(this, "Timer paused!", Toast.LENGTH_SHORT).show();
    }

    // Schedule a timer update every second
    @Override
    public void runProgressDisplay() {
        if (!progressBarRunning) {
            try {
                progressDisplayFuture = pool.scheduleAtFixedRate(new ProgressDisplayTask(), 0, 1, TimeUnit.SECONDS);
                progressBarRunning = true;
            } catch (Exception e) {
                Log.e("ProgressDisplay", "Progress Display cannot be scheduled!");
            }
        }
    }

    // Display a new quote periodically
    @Override
    public void runQuoteDisplay() {
        if (!quoteRunning) {
            try {
                quoteDisplayFuture = pool.scheduleAtFixedRate(new QuoteDisplayTask(), 0, 5, TimeUnit.SECONDS);
                quoteRunning = true;
            } catch (Exception e) {
                Log.e("QuoteDisplay", "Quote Display cannot be scheduled!");
            }
        }
    }

    // Update the MM:SS text
    @SuppressLint("DefaultLocale")
    @Override
    public void updateTimeView() {
        int[] arr = mainPresenter.toMinuteSecond(maxTime - currentTime);
        timeView.setText(String.format("%02d:%02d", arr[0], arr[1]));
    }

    // Stop all timer display tasks (recoverable)
    @Override
    public void cancelProgressDisplay() {
        if (progressBarRunning) {
            try {
                progressDisplayFuture.cancel(true);
                progressBar.setProgress(currentTime);
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
                quoteDisplayFuture.cancel(false);
                quoteRunning = false;
            } catch (Exception e) {
                Log.e("QuoteDisplay", "Quote Display cannot be cancelled!");
            }
        }
    }

    // Change from work to break and vice versa
    @Override
    public void switchSession() {
        // Update internal logic
        sessionRunning = false;
        isWorkSession = !isWorkSession;
        currentTime = 0;
        updateMaxTime(isWorkSession);

        // Update graphics
        buttonToggleTimer.setActivated(false);
        progressBar.setProgress(currentTime);
        updateTimeView();
        cancelProgressDisplay();
        cancelQuoteDisplay();
        setProgressBackground(isWorkSession);
    }

    // Update max time for progress display
    @Override
    public void updateMaxTime(boolean isWorkSession) {
        if (isWorkSession)
            maxTime = mainPresenter.toSecond(mainPresenter.getCurrentWorkLength());
        else
            maxTime = mainPresenter.toSecond(mainPresenter.getCurrentBreakLength());
    }

    // Change background image and default quote between sessions
    @Override
    public void setProgressBackground(boolean isWorkSession) {
        if (isWorkSession) {
            if (quoteDisplayOption == OPTION_QUOTE)
                quoteView.setText(R.string.work_now_message);
            progressBarBG.setImageResource(R.drawable.icon_wristwatch_resized);
        }
        else {
            if (quoteDisplayOption == OPTION_QUOTE)
                quoteView.setText(R.string.work_done_message);
            progressBarBG.setImageResource(R.drawable.icon_palm_tree_resized);
        }
    }

    // Pack current user options into bundle for Settings
    @Override
    public Bundle packCurrentUserOptions() {
        Bundle bundle = new Bundle();
        int currentWorkLength = mainPresenter.getCurrentWorkLength();
        int currentBreakLength = mainPresenter.getCurrentBreakLength();
        bundle.putIntArray("current_user_options", new int[] {currentWorkLength, currentBreakLength, quoteDisplayOption});
        return bundle;
    }

    // Pack current data
    @Override
    public Bundle packCurrentQuotes() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("current_quotes", mainPresenter.getMotivator().getQuotes());
        return bundle;
    }

    // Go to Settings with current data
    @Override
    public void goToSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtras(packCurrentUserOptions());
        startActivityForResult(intent, CODE_TO_SETTINGS);
        pauseTimer();
    }

    @Override
    public void goToQuotesEdit() {
        Intent intent = new Intent(MainActivity.this, QuotesEditActivity.class);
        intent.putExtras(packCurrentQuotes());
        startActivityForResult(intent, CODE_TO_QUOTES_EDIT);
        pauseTimer();
    }

    // Handle user choices from child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If from Settings, update session lengths
        if (requestCode == CODE_TO_SETTINGS) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    int[] arr = bundle.getIntArray("new_user_options");
                    mainPresenter.updateWorkLength(arr[0]);
                    mainPresenter.updateBreakLength(arr[1]);
                    quoteDisplayOption = arr[2];
                    // Display blank is quote disabled
                    // Set and reset visibility doesn't work?
                    switch (quoteDisplayOption) {
                        case OPTION_QUOTE:
                            cancelQuoteDisplay();
                            runQuoteDisplay();
                            break;
                        case OPTION_NO_QUOTE:
                            quoteView.setText("");
                            cancelQuoteDisplay();
                    }
                    // Apply session length changes
                    int radioOption = arr[3];
                    switch (radioOption) {
                        case OPTION_RESUME:
                            // Do nothing
                            break;
                        case OPTION_START_NEW:
                            // Quick hack to reset current session
                            switchSession();
                            switchSession();
                            break;
                    }
                    Toast.makeText(this, "Settings updated!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        // If from QuotesEdit, update quote list
        else if (requestCode == CODE_TO_QUOTES_EDIT) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    ArrayList<String> arr = bundle.getStringArrayList("new_quotes");
                    mainPresenter.updateQuoteList(arr);
                    Toast.makeText(this, "Quote list updated!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // ----- HELPER CLASSES + FUNCTION BELOW ----- //
    // Update progress bar and timer text
    private class ProgressDisplayTask implements Runnable {

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (currentTime < maxTime) {
                    currentTime++;
                    progressBar.setProgress(currentTime);
                    updateTimeView();
                }
                else
                    switchSession();
            });
        }
    }

    // Display random quotes while working
    private class QuoteDisplayTask implements Runnable {

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (sessionRunning && quoteDisplayOption == OPTION_QUOTE) {
                    if (isWorkSession)
                        quoteView.setText(mainPresenter.getRandomQuote());
                    else
                        quoteView.setText(R.string.work_done_message);
                }
            });
        }
    }
}