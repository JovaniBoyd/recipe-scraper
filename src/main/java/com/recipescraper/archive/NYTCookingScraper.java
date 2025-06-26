package com.recipescraper.scrapers;

import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class NYTCookingScraper implements SiteScraper {

    @Override
    public List<Recipe> search(String query, int maxResults) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            String searchUrl = "https://cooking.nytimes.com/search?q=" + query.replace(" ", "+");
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(10_000)
                    .get();

            Elements cards = doc.select("ul.search-results > li > a");
            System.out.println("NYT Cooking: found " + cards.size() + " results");

            for (Element card : cards) {
                if (recipes.size() >= maxResults) break;

                String url = card.absUrl("href");
                Element titleEl = card.selectFirst("h3, span");
                String title = titleEl != null ? titleEl.text() : "No title";

                System.out.println(" ? Trying: " + url);

                Recipe recipe = scrapeDetails(url);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to search NYT Cooking: " + e.getMessage());
        }

        return recipes;
    }

    private Recipe scrapeDetails(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            Element titleEl = doc.selectFirst("h1");
            if (titleEl == null) return null;
            String title = titleEl.text();

            Elements ingredientEls = doc.select("li[data-testid=ingredient-name]");
            if (ingredientEls.isEmpty()) {
                ingredientEls = doc.select("li.ingredient, ul.ingredients-list li");
            }
            List<String> ingredients = ingredientEls.eachText();

            Elements instructionEls = doc.select("li[data-testid=instruction-step]");
            if (instructionEls.isEmpty()) {
                instructionEls = doc.select("li.step, ol.instructions-list li");
            }
            List<String> instructions = instructionEls.eachText();

            System.out.println("  ✔ Ingredients found: " + ingredients.size());
            System.out.println("  ✔ Instructions found: " + instructions.size());

            if (ingredients.size() < 2 || instructions.size() < 2) return null;

            return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);
        } catch (Exception e) {
            System.err.println("Failed to scrape NYT Cooking recipe: " + e.getMessage());
            return null;
        }
    }
}
