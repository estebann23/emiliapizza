package com.example;

public class PizzaPriceCalculator {
    private static final double PROFIT_MARGIN_PERCENT = 0.40;
    private static final double VAT_PERCENT = 0.09;

    /**
     * Calculate the final price of a pizza based on its ingredient cost.
     *
     * @param ingredientCost The cost of the pizza ingredients.
     * @return The total price of the pizza, including profit margin and VAT.
     */
    public static double calculateFinalPrice(double ingredientCost) {
        if (ingredientCost < 0) {
            throw new IllegalArgumentException("Ingredient cost cannot be negative");
        }
        double profitMargin = calculateProfitMargin(ingredientCost);
        double priceWithMargin = ingredientCost + profitMargin;
        double vat = calculateVAT(priceWithMargin);
        return priceWithMargin + vat;
    }

    /**
     * Calculate the profit margin based on the ingredient cost.
     *
     * @param ingredientCost The cost of the pizza ingredients.
     * @return The profit margin.
     */
    public static double calculateProfitMargin(double ingredientCost) {
        if (ingredientCost < 0) {
            throw new IllegalArgumentException("Ingredient cost cannot be negative");
        }
        return ingredientCost * PROFIT_MARGIN_PERCENT;
    }

    /**
     * Calculate the VAT based on the price including the profit margin.
     *
     * @param priceWithMargin The price of the pizza with the profit margin added.
     * @return The VAT amount.
     */
    public static double calculateVAT(double priceWithMargin) {
        if (priceWithMargin < 0) {
            throw new IllegalArgumentException("Price with margin cannot be negative");
        }
        return priceWithMargin * VAT_PERCENT;
    }
}