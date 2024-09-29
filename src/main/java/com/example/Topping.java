package com.example;

public class Topping {
    private final String name;
    private final double price;
    private final boolean isVegan;
    private final boolean isVegetarian;

    public Topping(String name, double price, boolean isVegan, boolean isVegetarian) {
        this.name = name;
        this.price = price;
        this.isVegan = isVegan;
        this.isVegetarian = isVegetarian;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isVegan() {
        return isVegan;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }
}