// DiscountCode.java
package com.example;

public class DiscountCode {
    private int id;
    private String code;
    private double value;
    private boolean isAvailable;

    public DiscountCode(int id, String code, double value, boolean isAvailable) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public double getValue() {
        return value;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}