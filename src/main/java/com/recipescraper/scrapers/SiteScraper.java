package com.recipescraper.scrapers;

import java.util.List;
import com.recipescraper.model.Recipe;
// interface used by fallbacks

public interface SiteScraper {
    List<Recipe> search(String query, int maxResults);
}
