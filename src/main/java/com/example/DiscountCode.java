package com.example;

import java.util.List;

import static com.example.DatabaseHelper.getDiscountCodes;

public class DiscountCode {
    private String code;
    private double value;
    private boolean isUsed;

    public DiscountCode(String code, double value, boolean isUsed) {
        this.code = code;
        this.value = value;
        this.isUsed = isUsed;
    }

    public String getCode() {
        return code;
    }
    public double getValue() {

        return value;
    }

    public boolean isUsed() {
        return isUsed;
    }


    public void setCode(String code) {

        this.code = code;
    }
    public void setValue(double value) {

        this.value = value;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

}
