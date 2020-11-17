package com.example.pomodoro.settings;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

public interface SettingsContract {
    void getCurrentUserOptions();
    void initView();
    void setUpWorkSpinnerListener();
    void setUpBreakSpinnerListener();
    void onCheckedRadioButtonChange(RadioGroup group, int checkedId);
    void onCheckedSwitchChange(CompoundButton switchCompat, boolean isChecked);
    Bundle packNewUserOptions();
    void goBackFromSettings();
}
