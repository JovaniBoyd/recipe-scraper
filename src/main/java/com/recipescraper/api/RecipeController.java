package com.recipescraper.api;
import com.recipescraper.core.SmartScraper;
import com.recipescraper.model.Recipe;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST Controller
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final SmartScraper scraper = new SmartScraper();
    @GetMapping
    public List<Recipe> getRecipes(@RequestParam (name = "ingredients") List<String> ingredients){
        return scraper.searchRecipes(ingredients);
    }
}

