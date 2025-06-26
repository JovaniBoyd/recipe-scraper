package com.recipescraper;
import com.recipescraper.core.SmartScraper;
import com.recipescraper.model.Recipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SmartScraperTest {
    private static final int MAX_RECIPES = 6;
    @Test
    void testSearchRecipesResturnsResults(){
        SmartScraper scraper = new SmartScraper();
        List<String> ingredients = List.of("chicken", "rice");

        List<Recipe> results = scraper.searchRecipes(ingredients);

        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Results should not be empty");
        assertTrue(results.size() <= MAX_RECIPES, "Should return no more than " + MAX_RECIPES + " recipes");

        for (Recipe recipe : results){
            assertNotNull(recipe.getTitle());
            assertNotNull(recipe.getIngredients());

        }


    }
}
