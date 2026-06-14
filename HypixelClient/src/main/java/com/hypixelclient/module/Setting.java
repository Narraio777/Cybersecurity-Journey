package com.hypixelclient.module;

public class Setting {
    private final String name;
    private double value;
    private final double min;
    private final double max;
    private final double step;

    public Setting(String name, double value, double min, double max, double step) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public String getName() { return name; }
    public double getValue() { return value; }
    public double getMin() { return min; }
    public double getMax() { return max; }

    public void increment() { value = Math.min(max, value + step); }
    public void decrement() { value = Math.max(min, value - step); }

    public String getDisplayValue() {
        if (step >= 1) return String.valueOf((int) value);
        return String.format("%.0f%%", value);
    }
}
