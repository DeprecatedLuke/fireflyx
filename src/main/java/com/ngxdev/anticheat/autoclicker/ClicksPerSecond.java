package com.ngxdev.anticheat.autoclicker;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ClicksPerSecond
{
    private List<Integer> milliseconds;
    private List<Integer> ticks;
    
    public ClicksPerSecond() {
        this.milliseconds = new ArrayList<Integer>();
        this.ticks = new ArrayList<Integer>();
    }
    
    public void addMilliseconds(final double millis) {
        final int i = ((int)millis + 25) / 50 * 50;
        this.milliseconds.add(i);
    }
    
    public List<Integer> getMillis() {
        return this.milliseconds;
    }
    
    public int getClicks() {
        int count = 0;
        for (final int amount : this.ticks) {
            count += amount;
        }
        return count;
    }
    
    public List<Integer> getTicks() {
        return this.ticks;
    }
    
    public void setTicks(final List<Integer> ticks) {
        this.ticks = ticks;
    }
}
