package com.ngxdev.anticheat.autoclicker;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    private int amount = 0;
    private int low = 0;
    private int high = 0;
    private List<Integer> allHighs = new ArrayList<>();
    private List<Integer> allLows = new ArrayList<>();
    private List<Integer> patternHigh = new ArrayList<>();
    private List<Integer> patternLow = new ArrayList<>();

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public int getLow() {
        return this.low;
    }

    public void setLow(final int low) {
        this.low = low;
    }

    public int getHigh() {
        return this.high;
    }

    public void setHigh(final int high) {
        this.high = high;
    }

    public void addHighs(final int number) {
        this.allHighs.add(number);
    }

    public void addLows(final int number) {
        this.allLows.add(number);
    }

    public static int getOscillation(final List<Integer> numbers) {
        int highest = -1;
        int lowest = -1;
        for (final int number : numbers) {
            if (highest == -1) {
                highest = number;
            }
            if (lowest == -1) {
                lowest = number;
            }
            if (number > highest) {
                highest = number;
            }
            if (number < lowest) {
                lowest = number;
            }
        }
        return highest - lowest;
    }

    public List<Integer> getAllHighs() {
        return this.allHighs;
    }

    public List<Integer> getAllLows() {
        return this.allLows;
    }

    public void addPatternHigh(final int number) {
        this.patternHigh.add(number);
    }

    public void addPatternLow(final int number) {
        this.patternLow.add(number);
    }

    public List<Integer> getPatternHigh() {
        return this.patternHigh;
    }

    public List<Integer> getPatternLow() {
        return this.patternLow;
    }
}
