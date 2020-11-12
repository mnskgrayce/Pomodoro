package com.example.pomodoro.model;

import java.util.Arrays;
import java.util.List;

public class Motivator {

    private List<String> quotes = Arrays.asList(
            "You can do it!",
            "Almost there!",
            "Hang on just a bit longer!"
    );

    public List<String> getQuotes() {
        return quotes;
    }

    public String getRandomQuote() {
        return quotes.get((int) (Math.random() * quotes.size()));
    }

    public void addQuote(String newQuote) {
        quotes.add(newQuote);
    }

    public void deleteQuote(int index) {
        quotes.remove(index);
    }
}
