package com.example.pomodoro;

import android.os.Bundle;

import java.util.ArrayList;

public interface MainContract {

    // Display-related logic
    interface View {
        void initView();
        void initScheduledServices();
        void toggleTimer();
        void pauseTimer();
        void runProgressDisplay();
        void runQuoteDisplay();
        void updateTimeView();
        void cancelProgressDisplay();
        void cancelQuoteDisplay();
        void switchSession();
        void updateMaxTime(boolean isWorkSession);
        void setProgressBackground(boolean isWorkSession);

        Bundle packCurrentUserOptions();
        Bundle packCurrentQuotes();
        void goToSettings();
        void goToQuotesEdit();
    }

    // Pure logic
    interface Logic {
        int toSecond(int length);
        int[] toMinuteSecond(int second);
    }

    // Update/fetch data from model
    interface Model {
        String getRandomQuote();
        void updateWorkLength(int newLength);
        void updateBreakLength(int newLength);
        int getCurrentWorkLength();
        int getCurrentBreakLength();
        void updateQuoteList(ArrayList<String> newQuotes);
    }
}
