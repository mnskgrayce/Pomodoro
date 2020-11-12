package com.example.pomodoro;

public interface MainContract {

    // Displays
    interface View {
        void initView();
        void setButtonVisibilityDefault();
        void setButtonVisibilityOnClickStart();
        void setButtonVisibilityOnClickPause();
        void startTimer();
        void pauseTimer();
    }

    // Update/fetch data from model
    interface Presenter {
        int toSecond(int length);
        int[] toMinuteSecond(int second);
        String getRandomQuoteFromMotivator();
        void addQuoteToMotivator(String newQuote);
        void deleteQuoteFromMotivator(int index);
    }
}
