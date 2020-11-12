package com.example.pomodoro;

import com.example.pomodoro.model.Motivator;
import com.example.pomodoro.model.Session;

import java.util.List;

public class Presenter implements MainContract.Presenter {

    private Session session = new Session();
    private Motivator motivator = new Motivator();

    public Session getSession() {
        return session;
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


}
