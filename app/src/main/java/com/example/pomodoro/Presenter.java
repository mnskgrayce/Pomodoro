package com.example.pomodoro;

import com.example.pomodoro.model.Motivator;
import com.example.pomodoro.model.Session;

import java.util.List;

public class Presenter implements MainContract.Logic, MainContract.Model {

    private Session session = new Session();
    private Motivator motivator = new Motivator();
    private int currentSessionLength = session.getWorkLength();

    @Override
    public int toSecond(int minute) {
        return minute * 60;
    }

    @Override
    public int[] toMinuteSecond(int second) {
        second %= 3600;
        int minute = second / 60;
        second %= 60;
        return new int[] {minute, second};
    }

    @Override
    public String getRandomQuoteFromMotivator() {
        return motivator.getRandomQuote();
    }

    @Override
    public void addQuoteToMotivator(String newQuote) {
        List<String> quotes = motivator.getQuotes();
        boolean isDuplicate = false;

        for (String quote: quotes) {
            if (newQuote.equals(quote)) {
                isDuplicate = true;
                break;
            }
        }
        if (!isDuplicate)
            motivator.addQuote(newQuote);
        else {
            // tell user
        }
    }

    @Override
    public void deleteQuoteFromMotivator(int index) {
        if (index >= 0 && index < motivator.getQuotes().size())
            motivator.deleteQuote(index);
        else {
            // tell user
        }
    }

    @Override
    public void switchSession(boolean isWorkSession) {
        if (isWorkSession)
            currentSessionLength = session.getWorkLength();
        else
            currentSessionLength = session.getBreakLength();
    }

    @Override
    public int getCurrentSessionLength() { return currentSessionLength; }
}
