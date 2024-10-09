package com.example;

public class CartItem {
    private String name;
    private int itemId;
    private int quantity;
    private ItemType itemType;
    private double price;

    public enum ItemType {
        PIZZA, DRINK, DESSERT
    }

    public CartItem(String name, int itemId, ItemType itemType) {
        this.name = name;
        this.itemId = itemId;
        this.itemType = itemType;
    }


    public String getName() {
        return name;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + ")";
    }
}