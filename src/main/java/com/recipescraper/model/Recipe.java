package com.recipescraper.model;

import java.util.List;

public class Recipe {
    private String title;
    private List<String> ingredients;
    private List<String> instructions;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private String website;
    private double rating;
    private String imageUrl;

    // constructor 
    public Recipe(String title, List<String> ingredients, List<String> instructions, int prepTimeMinutes, int cookTimeMinutes, String website, double rating) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.website = website;
        this.rating = rating;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
    }
    
    //getters, setters
    public String getTitle() { return title; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getInstructions() { return instructions; }
    public String getWebsite() { return website; }
    public double getRating() { return rating; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public int getCookTimeMinutes() { return cookTimeMinutes; }


    //TODO: MAY NOT NEED SETTERS, included just in case

    public void setTitle(String title) { this.title = title; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }
    public void setWebsite(String website) { this.website = website; }
    public void setRating(double rating) { this.rating = rating; }
    public void setPrepTime(int prepTimeMinutes) {this.prepTimeMinutes = prepTimeMinutes; }
    public void setCookTime(int cookTimeMinutes) {this.cookTimeMinutes = cookTimeMinutes; }

    @Override
    public String toString() {
        return "Recipe: " + title + "\nBy " + website + " (Rating: " + rating + ")\n" +
               "Ingredients: " + ingredients + "\nInstructions: " + instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setter
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}