package com.example.pomodoro;

public interface MainContract {

    // Displays
    interface View {
        void initView();
        void initScheduledServices();
        void startTimer();
        void pauseTimer();
        void setButtonVisibilityDefault();
        void setButtonVisibilityOnClickStart();
        void setButtonVisibilityOnClickPause();
        void cancelProgressDisplay();
        void cancelQuoteDisplay();
        void setProgressBarColor();
        void finishSession();
    }

    // Pure logic
    interface Logic {
        int toSecond(int length);
        int[] toMinuteSecond(int second);
    }

    // Update/fetch data from model
    interface Model {
        String getRandomQuoteFromMotivator();
        void addQuoteToMotivator(String newQuote);
        void deleteQuoteFromMotivator(int index);
        void switchSession(boolean isWork);
        int getCurrentSessionLength();
    }
}
