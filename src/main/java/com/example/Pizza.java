package com.example;

import java.util.ArrayList;
import java.util.List;

public class Pizza {
    private final String name;
    private final List<Topping> toppings;

    public Pizza(String name) {
        this.name = name;
        this.toppings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public void addTopping(Topping topping) {
        toppings.add(topping);
    }
}