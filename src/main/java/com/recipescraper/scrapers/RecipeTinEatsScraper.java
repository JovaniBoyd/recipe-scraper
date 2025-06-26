package com.recipescraper.scrapers;

import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


// A fall back webscraper for RecipeTinEats
public class RecipeTinEatsScraper implements SiteScraper {

    @Override
    public List<Recipe> search(String query, int maxResults){
        List<Recipe> recipes = new ArrayList<>();

        try {
            String searchUrl = "https://www.recipetineats.com/?s=" + query.replace(" ", "+");
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("mozilla/5.0").timeout(10_000).get();

            Elements cards = doc.select("h2.entry-title > a");
            System.out.println("→ RecipeTinEats: found " + cards.size() + " recipe cards");//debug

            for (Element card: cards) {
                if (recipes.size() >= maxResults) break;

                String recipeUrl = card.absUrl("href");
                System.out.println("→ Trying: " + recipeUrl);//debug

                Recipe recipe = scrapeDetails(recipeUrl);
                if (recipe != null){
                    recipes.add(recipe);
                }
            }
        } catch (Exception e){
            System.err.println("failed to search Recipe Tin Eats: " + e.getMessage());
        }

        return recipes;

    }

    private Recipe scrapeDetails(String url){
        try{
            Document doc = Jsoup.connect(url).userAgent("mozilla/5.0").timeout(10_000).get();

            Element titleEl = doc.selectFirst("h1.entry-title");
            if (titleEl == null) return null;
            String title = titleEl.text();


            // ingredient items
            Elements ingredientEls = doc.select("div.wprm-recipe-ingredients-container li.wprm-recipe-ingredient");
            List<String> ingredients = ingredientEls.eachText();
            // instructions
            Elements instructionEls = doc.select("div.wprm-recipe-instruction-text");
            List<String> instructions = instructionEls.eachText();
            // rating
            Element ratingEl = doc.selectFirst("div.wprm-recipe-rating-star[data-rating]");
            double rating = 0.0;
            if (ratingEl != null){
                try {
                    rating = Double.parseDouble(ratingEl.attr("data-rating"));
                } catch (NumberFormatException ignored) {}
            }

            System.out.println("  → Ingredients found: " + ingredients.size());//
            System.out.println("  → Instructions found: " + instructions.size());//

            if (ingredients.size() < 2 || instructions.size() < 2){
                return null;
            }

            return new Recipe(title, ingredients, instructions, 0, 0, url, rating);

        } catch (Exception e){
            return null;
        }

    }


    
}
