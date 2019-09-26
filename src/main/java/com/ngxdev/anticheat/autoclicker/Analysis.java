package com.ngxdev.anticheat.autoclicker;

import java.util.Map;

public class Analysis
{
    private int legitMatches;
    private int cheatMatches;
    private int totalLegit;
    private int totalCheat;
    private Map<String, Integer> typeMatches;
    private Map<String, Integer> typeTotal;
    
    public Analysis() {
        this.legitMatches = 0;
        this.cheatMatches = 0;
        this.totalLegit = 0;
        this.totalCheat = 0;
    }
    
    public int getLegitMatches() {
        return this.legitMatches;
    }
    
    public void setLegitMatches(final int legitMatches) {
        this.legitMatches = legitMatches;
    }
    
    public int getCheatMatches() {
        return this.cheatMatches;
    }
    
    public void setCheatMatches(final int cheatMatches) {
        this.cheatMatches = cheatMatches;
    }
    
    public int getTotalLegit() {
        return this.totalLegit;
    }
    
    public void setTotalLegit(final int totalLegit) {
        this.totalLegit = totalLegit;
    }
    
    public int getTotalCheat() {
        return this.totalCheat;
    }
    
    public void setTotalCheat(final int totalCheat) {
        this.totalCheat = totalCheat;
    }
    
    public Map<String, Integer> getTypeMatches() {
        return this.typeMatches;
    }
    
    public void setTypeMatches(final Map<String, Integer> typeMatches) {
        this.typeMatches = typeMatches;
    }
    
    public Map<String, Integer> getTypeTotal() {
        return this.typeTotal;
    }
    
    public void setTypeTotal(final Map<String, Integer> typeTotal) {
        this.typeTotal = typeTotal;
    }
}
