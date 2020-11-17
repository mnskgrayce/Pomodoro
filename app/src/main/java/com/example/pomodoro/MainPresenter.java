package com.example.pomodoro;

import com.example.pomodoro.model.Motivator;
import com.example.pomodoro.model.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Update models for Main and do pure logic tasks
public class MainPresenter implements MainContract.Logic, MainContract.Model {

    private final Session session = new Session();
    private Motivator motivator = new Motivator();
    private int currentWorkLength = session.getWorkLength();
    private int currentBreakLength = session.getBreakLength();

    public Motivator getMotivator() {
        return motivator;
    }

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
    public String getRandomQuote() {
        List<String> quotes = motivator.getQuotes();
        if (quotes.size() != 0) {
            Random random = new Random();
            return quotes.get(random.nextInt(motivator.getQuotes().size()));
        }
        else return "";
    }

    @Override
    public void updateWorkLength(int newLength) {
        session.setWorkLength(newLength);
        currentWorkLength = session.getWorkLength();
    }

    @Override
    public void updateBreakLength(int newLength) {
        session.setBreakLength(newLength);
        currentBreakLength = session.getBreakLength();
    }

    @Override
    public int getCurrentWorkLength() { return currentWorkLength; }

    @Override
    public int getCurrentBreakLength() { return currentBreakLength; }

    @Override
    public void updateQuoteList(ArrayList<String> newQuotes) {
        motivator = new Motivator(newQuotes);
    }
}
