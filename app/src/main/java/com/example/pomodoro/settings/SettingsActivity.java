package com.example.pomodoro.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.pomodoro.MainActivity;
import com.example.pomodoro.R;

public class SettingsActivity extends AppCompatActivity implements SettingsContract {

    private Spinner spinnerWorkLengths;
    private Spinner spinnerBreakLengths;

    private int quoteDisplayOption;
    private int radioOption;
    private int workLength;
    private int breakLength;

    public static final int OPTION_RESUME = 0;
    public static final int OPTION_START_NEW = 1;
    public static final int OPTION_QUOTE = 1;
    public static final int OPTION_NO_QUOTE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getCurrentUserOptions();
        initView();
    }

    @Override
    public void getCurrentUserOptions() {
        // Get current options from Main
        Intent intent = getIntent();
        int[] arr = intent.getExtras().getIntArray("current_user_options");
        workLength = arr[0];
        breakLength = arr[1];
        quoteDisplayOption = arr[2];
    }

    @Override
    public void initView() {
        spinnerWorkLengths = findViewById(R.id.spinnerWorkLengths);
        spinnerBreakLengths = findViewById(R.id.spinnerBreakLengths);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioResume = findViewById(R.id.radioResume);
        RadioButton radioStartNew = findViewById(R.id.radioStartNew);
        SwitchCompat switchQuoteDisplay = findViewById(R.id.switchQuoteDisplay);
        ImageButton buttonBackFromSettings = findViewById(R.id.buttonBackFromSettings);

        // Always set resume
        radioResume.setChecked(true);
        radioStartNew.setChecked(false);

        // Set to current choice
        switchQuoteDisplay.setChecked(quoteDisplayOption == OPTION_QUOTE);

        // Set up listeners
        buttonBackFromSettings.setOnClickListener(v -> goBackFromSettings());
        radioGroup.setOnCheckedChangeListener(this::onCheckedRadioButtonChange);
        switchQuoteDisplay.setOnCheckedChangeListener(this::onCheckedSwitchChange);
        setUpWorkSpinnerListener();
        setUpBreakSpinnerListener();
    }

    @Override
    public void setUpWorkSpinnerListener() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterWork = ArrayAdapter.createFromResource(
                this,
                R.array.work_lengths_array,
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapterWork.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerWorkLengths.setAdapter(adapterWork);

        // Restore current user choice
        spinnerWorkLengths.setSelection(adapterWork.getPosition(String.valueOf(workLength)));

        // Listen to user selection
        spinnerWorkLengths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workLength = Integer.parseInt((String) parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @Override
    public void setUpBreakSpinnerListener() {
        ArrayAdapter<CharSequence> adapterBreak = ArrayAdapter.createFromResource(
                this,
                R.array.break_lengths_array,
                android.R.layout.simple_spinner_item);
        adapterBreak.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBreakLengths.setAdapter(adapterBreak);
        spinnerBreakLengths.setSelection(adapterBreak.getPosition(String.valueOf(breakLength)));

        spinnerBreakLengths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                breakLength = Integer.parseInt((String) parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedRadioButtonChange(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioResume:
                radioOption = OPTION_RESUME;
                break;
            case R.id.radioStartNew:
                radioOption = OPTION_START_NEW;
                break;
        }
    }

    @Override
    public void onCheckedSwitchChange(CompoundButton switchCompat, boolean isChecked) {
        if (isChecked)
            quoteDisplayOption = OPTION_QUOTE;
        else
            quoteDisplayOption = OPTION_NO_QUOTE;
    }

    @Override
    public Bundle packNewUserOptions() {
        // Wrap up new settings and send to Main
        Bundle bundle = new Bundle();
        bundle.putIntArray("new_user_options", new int[] {workLength, breakLength, quoteDisplayOption, radioOption});
        return bundle;
    }

    @Override
    public void goBackFromSettings() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtras(packNewUserOptions());
        setResult(RESULT_OK, intent);
        finish();
    }

    // Handle device back button
    @Override
    public void onBackPressed() {
        goBackFromSettings();
    }
}