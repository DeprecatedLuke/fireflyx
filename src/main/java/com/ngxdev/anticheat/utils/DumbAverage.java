package com.ngxdev.anticheat.utils;

public class DumbAverage {
    public double total = 0;
    public int count = 0;
    public double base;

    public DumbAverage(double base) {
        this.base = base;
    }

    public void add(double num) {
        total += num;
        count++;
        if (count > 50) {
            total -= getAverage();
            count--;
        }
    }

    public void reset() {
        total = 0;
        count = 0;
    }

    public double getAverage() {
        if (count == 0) return base;
        return total / count;
    }
}

class TestAverage {
    public static void main(String... args) {
        DumbAverage average = new DumbAverage(3.0);
        for (int i = 0; i < 30; i++) {
            average.add(3.1);
            System.out.println(average.getAverage());
        }
        for (int i = 0; i < 30; i++) {
            average.add(3.2);
            System.out.println(average.getAverage());
        }
        for (int i = 0; i < 30; i++) {
            average.add(2.9);
            System.out.println(average.getAverage());
        }
        for (int i = 0; i < 30; i++) {
            average.add(2.8);
            System.out.println(average.getAverage());
        }
    }
}
