package com.example.pomodoro.model;

import java.util.ArrayList;
import java.util.Arrays;

// Container for quotes
public class Motivator {

    private final ArrayList<String> quotes;

    public Motivator() {
        quotes = new ArrayList<>(Arrays.asList(
                "You can do it!",
                "Almost there!",
                "Hang on just a bit longer!",
                "Your work won't finish itself!",
                "Focus!",
                "Perseverance is the key to success.",
                "Your future self will thank you.",
                "Let's get this done!",
                "Don’t let what you cannot do interfere with what you can do.",
                "Strive for progress, not perfection.",
                "There are no shortcuts to any place worth going.",
                "Failure is the opportunity to begin again more intelligently.",
                "Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.",
                "You’ve got to get up every morning with determination if you’re going to go to bed with satisfaction."
        ));
    }

    public Motivator(ArrayList<String> newQuotes) {
        quotes = newQuotes;
    }

    public ArrayList<String> getQuotes() {
        return quotes;
    }

}
