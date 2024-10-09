package com.example;

public class CartItem {
    private String name;
    private int itemId;
    private int quantity;
    private ItemType itemType;

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
        this.quantity = quantity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public String toString() {
        return name; // Return the name of the item to display in the table
    }
}