package com.example.pomodoro.model;

public class Session {

    private int workLength;
    private int breakLength;

    public Session() {
        workLength = 25;
        breakLength = 5;
    }

    public int getWorkLength() {
        return workLength;
    }

    public void setWorkLength(int workLength) { this.workLength = workLength; }

    public int getBreakLength() {
        return breakLength;
    }

    public void setBreakLength(int breakLength) {
        this.breakLength = breakLength;
    }

}
