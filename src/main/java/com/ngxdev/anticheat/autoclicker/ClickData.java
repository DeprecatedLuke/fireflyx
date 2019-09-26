package com.ngxdev.anticheat.autoclicker;

import java.util.ArrayList;
import java.util.List;

public class ClickData
{
    private boolean legit;
    private String type;
    private List<ClicksPerSecond> clicks;
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public ClickData() {
        this.legit = true;
        (this.clicks = new ArrayList<ClicksPerSecond>()).add(new ClicksPerSecond());
    }
    
    public List<ClicksPerSecond> getClicks() {
        return this.clicks;
    }
    
    public void addClick(final ClicksPerSecond cps) {
        this.clicks.add(cps);
    }
    
    public ClicksPerSecond getMostRecent() {
        return this.clicks.get(this.clicks.size() - 1);
    }
    
    public boolean isLegit() {
        return this.legit;
    }
    
    public void setLegit(final boolean bool) {
        this.legit = bool;
    }
}
