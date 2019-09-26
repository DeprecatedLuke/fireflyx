package com.ngxdev.anticheat.api.events;

public class PlayerTakeVelocityEvent {
    public double deltaH, deltaV;

    public PlayerTakeVelocityEvent(double deltaH, double deltaV) {
        this.deltaH = deltaH;
        this.deltaV = deltaV;
    }
}
