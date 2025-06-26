package com.recipescraper;

import org.junit.jupiter.api.Test;

import com.recipescraper.model.Recipe;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeTest {
    @Test
    void testRecipeConstructorAndGetters(){
        Recipe recipe = new Recipe(
            "Test Recipe",
            List.of("egg", "milk"),
            List.of("mix", "bake"),
            10, 20, "Test.com", 4.5
        );
        assertEquals("Test Recipe", recipe.getTitle());
        assertEquals(10, recipe.getPrepTimeMinutes());
        assertEquals("Test.com", recipe.getWebsite());
        assertEquals(4.5, recipe.getRating(), 0.001);

    }
    
}
